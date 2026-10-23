package com.yukari.relicera.mixin;

import com.yukari.relicera.common.curio.SufferersExultationEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEffect.class)
public abstract class MobEffectMixin {
    @Inject(method = "applyEffectTick", at = @At("HEAD"))
    private void relicera$beginExultationEffectDamage(LivingEntity entity, int amplifier, CallbackInfo ci) {
        SufferersExultationEffects.beginEffect((MobEffect) (Object) this, entity);
    }

    @Inject(method = "applyEffectTick", at = @At("RETURN"))
    private void relicera$finishExultationEffectDamage(LivingEntity entity, int amplifier, CallbackInfo ci) {
        SufferersExultationEffects.finishEffect((MobEffect) (Object) this, entity);
    }
}
