package com.yukari.relicera.common.curio;

import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.common.item.IluthiasChaliceItem;
import com.yukari.relicera.registry.ModEffects;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.tags.DamageTypeTags;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import top.theillusivec4.curios.api.CuriosApi;

public final class IluthiasChaliceEffects {
    private static final int REGENERATION_DURATION_TICKS = 5 * 20;
    private static final int ENHANCED_TOTEM_FIRE_RESISTANCE_TICKS = 120 * 20;
    private static final int ENHANCED_TOTEM_REGENERATION_TICKS = 45 * 20;

    private IluthiasChaliceEffects() {
    }

    public static boolean isEquipped(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .resolve()
                .map(handler -> handler.isEquipped(ModItems.ILUTHIAS_CHALICE.get()))
                .orElse(false);
    }

    public static void tickImmunities(LivingEntity entity) {
        if (entity.level().isClientSide() || !isEquipped(entity)) {
            return;
        }

        entity.removeEffect(MobEffects.POISON);
        entity.removeEffect(MobEffects.WITHER);
        entity.removeEffect(MobEffects.HUNGER);
    }

    public static void applyDamageEffects(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        LivingEntity attacker = getResponsibleAttacker(event.getSource().getEntity(), event.getSource().getDirectEntity());

        if (attacker != null && attacker != target && isEquipped(attacker) && target.getMobType() == MobType.UNDEAD) {
            double damageBonus = ModCommonConfig.ILUTHIAS_CHALICE_UNDEAD_DAMAGE_BONUS.get();
            if (damageBonus > 0.0D) {
                event.setAmount(event.getAmount() * (1.0F + (float) damageBonus));
            }
            removeBeneficialEffects(target);
        }

        if (attacker != null && attacker.getMobType() == MobType.UNDEAD && isEquipped(target)) {
            double damageReduction = ModCommonConfig.ILUTHIAS_CHALICE_UNDEAD_DAMAGE_REDUCTION.get();
            if (damageReduction > 0.0D) {
                event.setAmount(event.getAmount() * Math.max(0.0F, 1.0F - (float) damageReduction));
            }
        }
    }

    public static void applyRegeneration(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide() || !isEquipped(entity) || event.getAmount() <= 0.0F || entity.getHealth() <= 0.0F) {
            return;
        }

        int amplifier = getRegenerationAmplifier(event.getAmount(), entity.getHealth());
        if (amplifier >= 0) {
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGENERATION_DURATION_TICKS, amplifier, false, true, true));
        }
    }

    public static boolean tryUseEnhancedTotem(LivingEntity entity, DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || !isEquipped(entity)) {
            return false;
        }

        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack held = entity.getItemInHand(hand);
            if (held.is(Items.TOTEM_OF_UNDYING) && ForgeHooks.onLivingUseTotem(entity, source, held, hand)) {
                ItemStack usedTotem = held.copy();
                held.shrink(1);
                applyEnhancedTotemEffects(entity, usedTotem);
                return true;
            }
        }

        for (ItemStack chalice : getEquippedChalices(entity)) {
            ItemStack storedTotem = IluthiasChaliceItem.peekOneTotem(chalice).orElse(ItemStack.EMPTY);
            if (!storedTotem.isEmpty() && ForgeHooks.onLivingUseTotem(entity, source, storedTotem, InteractionHand.OFF_HAND)) {
                IluthiasChaliceItem.removeOneTotem(chalice);
                applyEnhancedTotemEffects(entity, storedTotem);
                return true;
            }
        }

        return false;
    }

    private static int getRegenerationAmplifier(float damage, float currentHealth) {
        float ratio = damage / currentHealth;
        if (ratio > 0.80F) {
            return 4;
        }
        if (ratio > 0.65F) {
            return 3;
        }
        if (ratio > 0.50F) {
            return 2;
        }
        if (ratio > 0.35F) {
            return 1;
        }
        if (ratio > 0.20F) {
            return 0;
        }
        return -1;
    }

    private static void removeBeneficialEffects(LivingEntity entity) {
        List<MobEffectInstance> effects = List.copyOf(entity.getActiveEffects());
        for (MobEffectInstance effect : effects) {
            if (effect.getEffect().isBeneficial()) {
                entity.removeEffect(effect.getEffect());
            }
        }
    }

    private static List<ItemStack> getEquippedChalices(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .resolve()
                .map(handler -> handler.findCurios(ModItems.ILUTHIAS_CHALICE.get()).stream()
                        .map(top.theillusivec4.curios.api.SlotResult::stack)
                        .toList())
                .orElse(List.of());
    }

    private static void applyEnhancedTotemEffects(LivingEntity entity, ItemStack usedTotem) {
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING), 1);
            CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, usedTotem);
        }

        entity.setHealth(1.0F);
        entity.removeAllEffects();
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, ENHANCED_TOTEM_REGENERATION_TICKS, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, ENHANCED_TOTEM_FIRE_RESISTANCE_TICKS, 0));
        entity.addEffect(new MobEffectInstance(
                ModEffects.ILUTHIAS_BLESSING.get(),
                ModCommonConfig.ILUTHIAS_CHALICE_ENHANCED_TOTEM_ILUTHIAS_BLESSING_DURATION.get(),
                0
        ));
        entity.level().broadcastEntityEvent(entity, (byte) 35);
    }

    private static LivingEntity getResponsibleAttacker(Entity sourceEntity, Entity directEntity) {
        if (sourceEntity instanceof LivingEntity livingEntity) {
            return livingEntity;
        }
        if (directEntity instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity livingEntity) {
            return livingEntity;
        }
        return null;
    }
}
