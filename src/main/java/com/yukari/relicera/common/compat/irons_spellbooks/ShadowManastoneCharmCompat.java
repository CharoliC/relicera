package com.yukari.relicera.common.compat.irons_spellbooks;

import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.registry.ModItems;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.UUID;

public final class ShadowManastoneCharmCompat {
    private static final UUID MANA_REGEN = UUID.fromString("c313afbe-d381-4c8e-aa0d-f172347ef3e0");
    private static final UUID SPELL_POWER = UUID.fromString("92c0765d-1850-4348-9686-bca36aba5c63");

    private ShadowManastoneCharmCompat() {
    }

    public static double getSpellPowerBonus(Player player) {
        long count = SpellRegistry.getEnabledSpells().stream()
                .filter(spell -> spell.getSchoolType().equals(SchoolRegistry.ELDRITCH.get()))
                .filter(spell -> spell.isLearned(player)).count();
        return count * ModCommonConfig.SHADOW_MANASTONE_CHARM_POWER_PER_SPELL.get();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        int count = !player.isAlive() ? 0 : CuriosApi.getCuriosInventory(player).resolve()
                .map(inventory -> (int) inventory.findCurios(ModItems.SHADOW_MANASTONE_CHARM.get()).stream()
                        .filter(result -> !result.slotContext().cosmetic()
                                && "necklace".equals(result.slotContext().identifier())).count()).orElse(0);
        AttributeInstance regen = getAttribute(player, "mana_regen");
        AttributeInstance power = getAttribute(player, "spell_power");
        if (count == 0) {
            if (regen != null) regen.removeModifier(MANA_REGEN);
            if (power != null) power.removeModifier(SPELL_POWER);
            return;
        }
        boolean changed = update(regen, MANA_REGEN, count * 0.20D);
        if (changed || player.tickCount % 20 == 0 || (power != null && power.getModifier(SPELL_POWER) == null)) {
            update(power, SPELL_POWER, count * getSpellPowerBonus(player));
        }
    }

    private static AttributeInstance getAttribute(Player player, String id) {
        var attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", id));
        return attribute == null ? null : player.getAttribute(attribute);
    }

    private static boolean update(AttributeInstance attribute, UUID id, double amount) {
        if (attribute == null) {
            return false;
        }
        AttributeModifier previous = attribute.getModifier(id);
        if (previous != null && Double.compare(previous.getAmount(), amount) == 0) {
            return false;
        }
        attribute.removeModifier(id);
        attribute.addTransientModifier(new AttributeModifier(id, "Relicera Shadow Manastone Charm",
                amount, AttributeModifier.Operation.MULTIPLY_BASE));
        return true;
    }
}
