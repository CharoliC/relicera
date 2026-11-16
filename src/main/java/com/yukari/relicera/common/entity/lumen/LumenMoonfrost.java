package com.yukari.relicera.common.entity.lumen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public final class LumenMoonfrost {
    private static final String STATE_KEY = "relicera:moonfrost";
    private static final UUID SPEED_MODIFIER = UUID.fromString("a880cb28-bd42-4aa6-851f-31b3128c1f8c");

    private LumenMoonfrost() {
    }

    static void apply(LivingEntity target, int level) {
        if (target.level().isClientSide() || level <= 0 || !target.isAlive()) {
            return;
        }
        level = Mth.clamp(level, 1, 3);
        int duration = 80 * level;
        if (target.canFreeze()) {
            target.setTicksFrozen(Math.max(target.getTicksFrozen(), target.getTicksRequiredToFreeze() + 2 * duration));
        }
        CompoundTag state = target.getPersistentData().getCompound(STATE_KEY);
        if (state.getInt("Ticks") > 0 && state.getInt("Level") > level) {
            return;
        }
        state.putInt("Level", level);
        state.putInt("Ticks", Math.max(state.getInt("Ticks"), duration));
        target.getPersistentData().put(STATE_KEY, state);
        updateSpeed(target, level);
    }

    public static void tick(LivingEntity target) {
        if (target.level().isClientSide() || !target.getPersistentData().contains(STATE_KEY)) {
            return;
        }
        CompoundTag state = target.getPersistentData().getCompound(STATE_KEY);
        int remaining = state.getInt("Ticks") - 1;
        if (remaining <= 0 || !target.isAlive()) {
            target.getPersistentData().remove(STATE_KEY);
            AttributeInstance speed = target.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                speed.removeModifier(SPEED_MODIFIER);
            }
            return;
        }
        state.putInt("Ticks", remaining);
        updateSpeed(target, Mth.clamp(state.getInt("Level"), 1, 3));
    }

    private static void updateSpeed(LivingEntity target, int level) {
        AttributeInstance speed = target.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        double amount = -0.15D * level;
        AttributeModifier previous = speed.getModifier(SPEED_MODIFIER);
        if (previous == null || Double.compare(previous.getAmount(), amount) != 0) {
            speed.removeModifier(SPEED_MODIFIER);
            speed.addTransientModifier(new AttributeModifier(SPEED_MODIFIER, "relicera.moonfrost", amount,
                    AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }
}
