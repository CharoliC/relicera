package com.yukari.relicera.common.compat.irons_spellbooks;

import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.registry.ModItems;
import io.redspace.ironsspellbooks.api.item.ISpellbook;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.item.Scroll;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

public final class MagicSwordsmansMedalCompat {
    private static final TagKey<DamageType> MAGIC = TagKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath("forge", "is_magic"));

    private MagicSwordsmansMedalCompat() {
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        var source = event.getSource();
        if (event.getEntity().level().isClientSide || event.getAmount() <= 0
                || !(source.getEntity() instanceof LivingEntity wearer) || source.getDirectEntity() != wearer
                || source instanceof SpellDamageSource || source.is(MAGIC)
                || source.is(DamageTypeTags.IS_PROJECTILE) || source.is(DamageTypeTags.IS_EXPLOSION)
                || source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC)
                || !hasInscribedWeapon(wearer.getMainHandItem())) {
            return;
        }
        boolean equipped = CuriosApi.getCuriosInventory(wearer).resolve()
                .map(inventory -> inventory.findCurios(ModItems.MAGIC_SWORDSMANS_MEDAL.get()).stream()
                        .anyMatch(result -> !result.slotContext().cosmetic()
                                && "relicera_brooch".equals(result.slotContext().identifier()))).orElse(false);
        if (equipped) {
            event.setAmount(event.getAmount() * (1.0F + ModCommonConfig.MAGIC_SWORDSMANS_MEDAL_MELEE_BONUS.get().floatValue()));
        }
    }

    private static boolean hasInscribedWeapon(ItemStack stack) {
        return !stack.isEmpty() && !(stack.getItem() instanceof ISpellbook)
                && !(stack.getItem() instanceof Scroll) && !(stack.getItem() instanceof ArmorItem)
                && stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE)
                && ISpellContainer.isSpellContainer(stack) && !ISpellContainer.get(stack).isEmpty();
    }
}
