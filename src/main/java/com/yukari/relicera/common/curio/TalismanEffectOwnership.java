package com.yukari.relicera.common.curio;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.MobEffectEvent;

public final class TalismanEffectOwnership {
    private static final String REGENERATION_TAG = "ReliceraCurativeRegeneration";
    private static final String UNLUCK_TAG = "ReliceraExultationUnluck";
    private static final ThreadLocal<MobEffectInstance> GRANTING = new ThreadLocal<>();
    private static final ThreadLocal<MobEffectInstance> REMOVING = new ThreadLocal<>();

    private TalismanEffectOwnership() {
    }

    static void update(LivingEntity entity, MobEffect effect, int amplifier) {
        String key = keyFor(effect);
        CompoundTag data = entity.getPersistentData();
        if (data.contains(key, Tag.TAG_COMPOUND)) {
            if (!entity.hasEffect(effect)) {
                data.remove(key);
            } else {
                CompoundTag grant = data.getCompound(key);
                grant.putLong("Elapsed", grant.getLong("Elapsed") + 1L);
                if (amplifier == grant.getInt("Amplifier")) {
                    return;
                }
                release(entity, effect, key);
                if (data.contains(key, Tag.TAG_COMPOUND)) {
                    return;
                }
            }
        }
        if (amplifier < 0 || !entity.isAlive()) {
            return;
        }

        MobEffectInstance existing = entity.getEffect(effect);
        if (existing != null && (existing.getAmplifier() > amplifier
                || existing.getAmplifier() == amplifier && existing.isInfiniteDuration())) {
            return;
        }

        CompoundTag grant = new CompoundTag();
        grant.putInt("Amplifier", amplifier);
        if (existing != null) {
            grant.put("External", existing.save(new CompoundTag()));
        }
        MobEffectInstance supplied = new MobEffectInstance(
                effect, MobEffectInstance.INFINITE_DURATION, amplifier, false, true, true);
        if (addEffect(entity, supplied)) {
            data.put(key, grant);
        }
    }

    public static void rememberExternalEffect(MobEffectEvent.Added event) {
        MobEffectInstance incoming = event.getEffectInstance();
        if (event.getEntity().level().isClientSide() || GRANTING.get() == incoming) {
            return;
        }
        String key = keyFor(incoming.getEffect());
        if (key == null) {
            return;
        }
        CompoundTag data = event.getEntity().getPersistentData();
        if (!data.contains(key, Tag.TAG_COMPOUND)) {
            return;
        }
        if (event.getOldEffectInstance() == null) {
            data.remove(key);
            return;
        }
        CompoundTag grant = data.getCompound(key);
        MobEffectInstance external = remainingExternal(grant);
        if (external == null) {
            external = MobEffectInstance.load(incoming.save(new CompoundTag()));
        } else {
            external.update(incoming);
        }
        if (external != null) {
            grant.put("External", external.save(new CompoundTag()));
            grant.putLong("ExternalTick", grant.getLong("Elapsed"));
        }
    }

    private static void release(LivingEntity entity, MobEffect effect, String key) {
        CompoundTag data = entity.getPersistentData();
        CompoundTag grant = data.getCompound(key);
        MobEffectInstance previous = REMOVING.get();
        REMOVING.set(entity.getEffect(effect));
        try {
            if (!entity.removeEffect(effect) && entity.hasEffect(effect)) {
                return;
            }
        } finally {
            if (previous == null) {
                REMOVING.remove();
            } else {
                REMOVING.set(previous);
            }
        }
        data.remove(key);
        MobEffectInstance external = remainingExternal(grant);
        if (external != null && entity.isAlive()) {
            addEffect(entity, external);
        }
    }

    static boolean isInternalRemoval(MobEffectInstance effect) {
        return REMOVING.get() == effect;
    }

    private static boolean addEffect(LivingEntity entity, MobEffectInstance effect) {
        MobEffectInstance previous = GRANTING.get();
        GRANTING.set(effect);
        try {
            return entity.addEffect(effect);
        } finally {
            if (previous == null) {
                GRANTING.remove();
            } else {
                GRANTING.set(previous);
            }
        }
    }

    private static MobEffectInstance remainingExternal(CompoundTag grant) {
        if (!grant.contains("External", Tag.TAG_COMPOUND)) {
            return null;
        }
        CompoundTag external = ageEffect(grant.getCompound("External").copy(),
                Math.max(0L, grant.getLong("Elapsed") - grant.getLong("ExternalTick")));
        return external == null ? null : MobEffectInstance.load(external);
    }

    private static CompoundTag ageEffect(CompoundTag effect, long elapsed) {
        CompoundTag hidden = effect.contains("HiddenEffect", Tag.TAG_COMPOUND)
                ? ageEffect(effect.getCompound("HiddenEffect"), elapsed) : null;
        effect.remove("HiddenEffect");
        if (hidden != null) {
            effect.put("HiddenEffect", hidden);
        }
        int duration = effect.getInt("Duration");
        if (duration != MobEffectInstance.INFINITE_DURATION) {
            if (duration <= elapsed) {
                return hidden;
            }
            effect.putInt("Duration", (int) (duration - elapsed));
        }
        return effect;
    }

    private static String keyFor(MobEffect effect) {
        return effect == MobEffects.REGENERATION ? REGENERATION_TAG
                : effect == MobEffects.UNLUCK ? UNLUCK_TAG : null;
    }
}
