package com.yukari.relicera.common.compat.irons_spellbooks;

import io.redspace.ironsspellbooks.api.item.ISpellbook;
import com.yukari.relicera.common.item.ArchmagesSilverRingItem;
import com.yukari.relicera.registry.ModItems;
import io.redspace.ironsspellbooks.api.events.ModifySpellLevelEvent;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import net.minecraft.resources.ResourceLocation;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class ArchmagesSilverRingCompat {
    private ArchmagesSilverRingCompat() {
    }

    public static boolean isSpellbook(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ISpellbook;
    }

    public static Component getSpellName(SpellData data, @Nullable Player player) {
        var spell = data.getSpell();
        return spell.getDisplayName(player).copy().withStyle(spell.getSchoolType().getDisplayName().getStyle());
    }

    @Nullable
    public static Component getSchoolName(ResourceLocation powerAttribute) {
        String suffix = "_spell_power";
        if (!powerAttribute.getPath().endsWith(suffix) || powerAttribute.getPath().length() <= suffix.length()) {
            return null;
        }
        String path = powerAttribute.getPath();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(powerAttribute.getNamespace(),
                path.substring(0, path.length() - suffix.length()));
        var school = SchoolRegistry.REGISTRY.get().getValue(id);
        return school == null ? null : school.getDisplayName().copy();
    }

    public static List<Component> getSelectedSpellNames(ItemStack book, boolean excludeInscribed, @Nullable Player player) {
        return getSelectedSpells(book, excludeInscribed).stream().map(spell -> getSpellName(spell, player)).toList();
    }

    public static List<SpellData> getSelectedSpells(ItemStack book, boolean excludeInscribed) {
        List<SpellData> result = new ArrayList<>(4);
        if (!isSpellbook(book) || !ISpellContainer.isSpellContainer(book)) {
            return result;
        }
        ISpellContainer container = ISpellContainer.get(book);
        for (int index = 0; index < container.getMaxSpellCount() && result.size() < 4; index++) {
            SpellData spell = container.getSpellAtIndex(index);
            if (spell.getSpell() != SpellRegistry.none() && (!excludeInscribed || !spell.isLocked())) {
                result.add(spell);
            }
        }
        return result;
    }

    @SubscribeEvent
    public static void onModifySpellLevel(ModifySpellLevelEvent event) {
        if (event.getEntity() == null || !ModItems.ARCHMAGES_SILVER_RING.isPresent()) {
            return;
        }
        CuriosApi.getCuriosInventory(event.getEntity()).ifPresent(inventory -> {
            for (var equipped : inventory.findCurios(ModItems.ARCHMAGES_SILVER_RING.get())) {
                if (equipped.slotContext().cosmetic() || !"ring".equals(equipped.slotContext().identifier())) {
                    continue;
                }
                ItemStack ring = equipped.stack();
                boolean matches = getSelectedSpells(ArchmagesSilverRingItem.getSpellbook(ring),
                        ArchmagesSilverRingItem.excludesInscribedSpells(ring)).stream()
                        .anyMatch(spell -> spell.getSpell().equals(event.getSpell()));
                if (matches) {
                    event.addLevels(1);
                }
            }
        });
    }
}
