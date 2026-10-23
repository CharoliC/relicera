package com.yukari.relicera.common.curio;

import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayDeque;
import java.util.Deque;

public final class SufferersExultationEffects {
    private static final double HEALING_RANGE = 8.0D;
    private static final ThreadLocal<Deque<EffectDamage>> ACTIVE = new ThreadLocal<>();

    private SufferersExultationEffects() {
    }

    public static boolean isEquipped(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).resolve()
                .map(handler -> handler.isEquipped(ModItems.SUFFERERS_EXULTATION.get())).orElse(false);
    }

    public static void tick(LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            TalismanEffectOwnership.update(entity, MobEffects.UNLUCK,
                    entity.isAlive() && !entity.isSpectator() && isEquipped(entity) ? 0 : -1);
        }
    }

    public static void modifyDamage(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide() || event.getAmount() <= 0.0F) {
            return;
        }
        double multiplier = 1.0D;
        if (event.getSource().getEntity() instanceof LivingEntity attacker
                && attacker != event.getEntity() && isEquipped(attacker)) {
            multiplier *= 1.0D + ModCommonConfig.SUFFERERS_EXULTATION_DAMAGE_BONUS_PER_EFFECT.get()
                    * CurativeTalismanEffects.countHarmfulEffects(attacker);
        }
        if (isEquipped(event.getEntity())) {
            multiplier *= 1.0D + ModCommonConfig.SUFFERERS_EXULTATION_DAMAGE_TAKEN_PER_EFFECT.get()
                    * CurativeTalismanEffects.countHarmfulEffects(event.getEntity());
        }
        event.setAmount((float) (event.getAmount() * multiplier));
    }

    public static void increaseVanillaCriticalDamage(CriticalHitEvent event) {
        if (event.isVanillaCritical() && isEquipped(event.getEntity())) {
            event.setDamageModifier(event.getDamageModifier()
                    + ModCommonConfig.SUFFERERS_EXULTATION_CRITICAL_BONUS_PER_EFFECT.get().floatValue()
                    * CurativeTalismanEffects.countHarmfulEffects(event.getEntity()));
        }
    }

    public static void beginEffect(MobEffect effect, LivingEntity entity) {
        if (entity.level().isClientSide() || effect != MobEffects.POISON && effect != MobEffects.WITHER) {
            return;
        }
        Deque<EffectDamage> contexts = ACTIVE.get();
        if (contexts == null) {
            contexts = new ArrayDeque<>();
            ACTIVE.set(contexts);
        }
        contexts.push(new EffectDamage(entity, effect == MobEffects.POISON
                ? entity.damageSources().magic() : entity.damageSources().wither()));
    }

    public static void finishEffect(MobEffect effect, LivingEntity entity) {
        if (entity.level().isClientSide() || effect != MobEffects.POISON && effect != MobEffects.WITHER) {
            return;
        }
        Deque<EffectDamage> contexts = ACTIVE.get();
        if (contexts == null || contexts.isEmpty() || contexts.peek().victim != entity) {
            return;
        }
        EffectDamage damage = contexts.pop();
        if (contexts.isEmpty()) {
            ACTIVE.remove();
        }
        healNearbyWearers(entity, damage.healthLost);
    }

    public static void beforeHealthChange(LivingEntity entity, DamageSource source) {
        EffectDamage damage = current(entity, source);
        if (damage != null) {
            damage.healthBefore = entity.getHealth();
        }
    }

    public static void afterHealthChange(LivingEntity entity, DamageSource source) {
        EffectDamage damage = current(entity, source);
        if (damage != null) {
            damage.healthLost += Math.max(0.0F, damage.healthBefore - entity.getHealth());
        }
    }

    private static EffectDamage current(LivingEntity entity, DamageSource source) {
        Deque<EffectDamage> contexts = ACTIVE.get();
        EffectDamage damage = contexts == null ? null : contexts.peek();
        return damage != null && damage.victim == entity && damage.source == source ? damage : null;
    }

    private static void healNearbyWearers(LivingEntity victim, float healthLost) {
        if (!(victim.level() instanceof ServerLevel level) || healthLost <= 0.0F) {
            return;
        }
        double multiplier = ModCommonConfig.SUFFERERS_EXULTATION_HEALING_MULTIPLIER.get();
        if (multiplier <= 0.0D) {
            return;
        }
        for (LivingEntity wearer : level.getEntitiesOfClass(LivingEntity.class,
                victim.getBoundingBox().inflate(HEALING_RANGE), entity -> entity != victim
                        && entity.isAlive() && !entity.isSpectator()
                        && entity.distanceToSqr(victim) <= HEALING_RANGE * HEALING_RANGE)) {
            if (wearer.getHealth() < wearer.getMaxHealth() && isEquipped(wearer)) {
                wearer.heal((float) (healthLost * multiplier));
            }
        }
    }

    private static final class EffectDamage {
        private final LivingEntity victim;
        private final DamageSource source;
        private float healthBefore;
        private float healthLost;

        private EffectDamage(LivingEntity victim, DamageSource source) {
            this.victim = victim;
            this.source = source;
        }
    }
}
