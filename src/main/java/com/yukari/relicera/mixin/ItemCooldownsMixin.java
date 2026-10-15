package com.yukari.relicera.mixin;

import com.yukari.relicera.common.item.chronometer.ChronometerCooldownControl;
import com.yukari.relicera.common.item.chronometer.StoppedSilverChronometerEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(ItemCooldowns.class)
public abstract class ItemCooldownsMixin implements ChronometerCooldownControl {
    @Shadow
    @Final
    private Map<Item, ?> cooldowns;

    @Shadow
    public abstract void tick();

    @Shadow
    public abstract void removeCooldown(Item item);

    @Unique
    private Player relicera$owner;
    @Unique
    private double relicera$fraction;
    @Unique
    private boolean relicera$advancing;

    @Override
    public void relicera$setCooldownOwner(Player player) {
        relicera$owner = player;
    }

    @Override
    public void relicera$clearChronometerCooldowns() {
        relicera$fraction = 0.0D;
        for (Item item : List.copyOf(cooldowns.keySet())) {
            removeCooldown(item);
        }
    }

    @Unique
    private int relicera$activeStep() {
        return relicera$owner == null ? 0 : StoppedSilverChronometerEffects.getActiveStep(relicera$owner);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void relicera$advanceChronometerTime(CallbackInfo ci) {
        if (relicera$owner == null || relicera$advancing) {
            return;
        }
        int step = relicera$activeStep();
        if (step == StoppedSilverChronometerEffects.MIN_STEP) {
            relicera$clearChronometerCooldowns();
            ci.cancel();
            return;
        }
        if (step == 0 && relicera$fraction == 0.0D) {
            return;
        }
        double elapsed = relicera$fraction + 20.0D / (20 + step);
        int ticks = (int) Math.floor(elapsed + 1.0E-9D);
        relicera$fraction = Math.max(0.0D, elapsed - ticks);
        relicera$advancing = true;
        try {
            for (int i = 0; i < ticks; i++) {
                tick();
            }
        } finally {
            relicera$advancing = false;
        }
        ci.cancel();
    }

    @Inject(method = "addCooldown", at = @At("HEAD"), cancellable = true)
    private void relicera$skipInstantCooldown(Item item, int duration, CallbackInfo ci) {
        if (relicera$activeStep() == StoppedSilverChronometerEffects.MIN_STEP) {
            removeCooldown(item);
            ci.cancel();
        }
    }

    @Inject(method = "getCooldownPercent", at = @At("HEAD"), cancellable = true)
    private void relicera$hideInstantCooldown(Item item, float partialTick, CallbackInfoReturnable<Float> cir) {
        if (cooldowns.containsKey(item) && relicera$activeStep() == StoppedSilverChronometerEffects.MIN_STEP) {
            cir.setReturnValue(0.0F);
        }
    }

    @ModifyVariable(method = "getCooldownPercent", at = @At("HEAD"), argsOnly = true)
    private float relicera$interpolateChronometerTime(float partialTick) {
        if (relicera$owner == null || cooldowns.isEmpty()) {
            return partialTick;
        }
        int step = relicera$activeStep();
        if (step == StoppedSilverChronometerEffects.MIN_STEP) {
            return 0.0F;
        }
        return (float) (relicera$fraction + partialTick * 20.0D / (20 + step));
    }
}
