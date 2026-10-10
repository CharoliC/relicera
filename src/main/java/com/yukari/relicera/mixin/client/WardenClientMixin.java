package com.yukari.relicera.mixin.client;

import com.yukari.relicera.common.effect.souloverload.SoulOverloadState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Warden.class)
public abstract class WardenClientMixin {
    @Inject(method = "getHeartBeatDelay", at = @At("RETURN"), cancellable = true)
    private void relicera$accelerateOverloadedHeartbeat(CallbackInfoReturnable<Integer> cir) {
        int level = ((SoulOverloadState) this).relicera$getSoulOverloadLevel();
        if (level > 0) {
            cir.setReturnValue(Math.max(4, (int) (cir.getReturnValueI() / (1.0D + 0.5D * level))));
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void relicera$showSoulOverload(CallbackInfo ci) {
        Warden warden = (Warden) (Object) this;
        if (!warden.level().isClientSide() || !warden.isAlive()) {
            return;
        }
        int level = ((SoulOverloadState) warden).relicera$getSoulOverloadLevel();
        if (level <= 0) {
            return;
        }
        if (warden.tickCount % 4 == 0) {
            warden.level().addParticle(ParticleTypes.SCULK_SOUL,
                    warden.getX() + (warden.getRandom().nextDouble() - 0.5D) * 0.2D,
                    warden.getBoundingBox().maxY + 0.1D,
                    warden.getZ() + (warden.getRandom().nextDouble() - 0.5D) * 0.2D,
                    0.0D, 0.01D, 0.0D);
        }
        if (level >= 3) {
            warden.attackAnimationState.stop();
            warden.diggingAnimationState.stop();
            warden.emergeAnimationState.stop();
            warden.roarAnimationState.stop();
            warden.sniffAnimationState.stop();
            warden.walkAnimation.setSpeed(0.0F);
            warden.walkAnimation.update(0.0F, 1.0F);
        }
    }
}
