package com.yukari.relicera.mixin;

import com.yukari.relicera.common.curio.GranbellsFurnaceEffects;
import com.yukari.relicera.common.curio.IluthiasChaliceEffects;
import com.yukari.relicera.common.curio.WarpCrystalEffects;
import com.yukari.relicera.common.curio.SufferersExultationEffects;
import com.yukari.relicera.common.curio.CurativeTalismanEffects;
import com.yukari.relicera.common.item.TempestsReinsEffects;
import com.yukari.relicera.common.item.silkofnight.SilkOfNightDecoration;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    protected float lastHurt;

    @Inject(method = "onEffectRemoved", at = @At("TAIL"))
    private void relicera$recordCurativeRecovery(MobEffectInstance effect, CallbackInfo ci) {
        CurativeTalismanEffects.onEffectRemoved((LivingEntity) (Object) this, effect);
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    private void relicera$beforeExultationHealthLoss(DamageSource source, float amount, CallbackInfo ci) {
        SufferersExultationEffects.beforeHealthChange((LivingEntity) (Object) this, source);
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V", shift = At.Shift.AFTER))
    private void relicera$afterExultationHealthLoss(DamageSource source, float amount, CallbackInfo ci) {
        SufferersExultationEffects.afterHealthChange((LivingEntity) (Object) this, source);
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    private void relicera$rememberWarpCrystalPreHurtState(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        WarpCrystalEffects.beginHurt(self, self.invulnerableTime, this.lastHurt);
    }

    @Inject(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void relicera$suppressWarpCrystalLethalHitFeedback(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        WarpCrystalEffects.PreHurtState state = WarpCrystalEffects.consumeSuppressedHitFeedback(self, source);
        if (state == null) {
            return;
        }

        self.invulnerableTime = state.invulnerableTime();
        this.lastHurt = state.lastHurt();
        cir.setReturnValue(false);
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    private void relicera$clearWarpCrystalPreHurtState(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        WarpCrystalEffects.endHurt((LivingEntity) (Object) this);
    }

    @Inject(method = "playHurtSound", at = @At("HEAD"), cancellable = true)
    private void relicera$suppressSilkOfNightFallHurtSound(DamageSource source, CallbackInfo ci) {
        if ((Object) this instanceof Player player
                && source.is(DamageTypeTags.IS_FALL)
                && SilkOfNightDecoration.isWearing(player)) {
            ci.cancel();
        }
    }

    @Inject(method = "playBlockFallSound", at = @At("HEAD"), cancellable = true)
    private void relicera$suppressSilkOfNightBlockFallSound(CallbackInfo ci) {
        if ((Object) this instanceof Player player && SilkOfNightDecoration.isWearing(player)) {
            ci.cancel();
        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void relicera$useEnhancedTotemWithIluthiasChalice(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (IluthiasChaliceEffects.tryUseEnhancedTotem(self, source)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canStandOnFluid", at = @At("RETURN"), cancellable = true)
    private void relicera$canStandOnLavaWithGranbellsFurnace(FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }

        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player
                && !player.isCrouching()
                && fluidState.is(FluidTags.LAVA)
                && GranbellsFurnaceEffects.isEquipped(player)) {
            cir.setReturnValue(true);
            return;
        }

        if (fluidState.is(FluidTags.WATER) && TempestsReinsEffects.allowsWaterStanding(self)) {
            cir.setReturnValue(true);
        }
    }
}
