package com.yukari.relicera.mixin;

import com.yukari.relicera.common.curio.ThousandweightGauntletsEffects;
import com.yukari.relicera.common.curio.TwoHandedSwordBroochEffects;
import com.yukari.relicera.common.item.silkofnight.SilkOfNightDecoration;
import com.yukari.relicera.common.item.silkofnight.SilkOfNightFallContext;
import com.yukari.relicera.common.item.StonewallGreatshieldEffects;
import com.yukari.relicera.common.item.chronometer.ChronometerCooldownControl;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin implements SilkOfNightFallContext {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void relicera$bindChronometerCooldowns(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        ((ChronometerCooldownControl) self.getCooldowns()).relicera$setCooldownOwner(self);
    }

    @Unique
    private float relicera$primaryAttackDamage;
    @Unique
    private float relicera$smashFallDistance;
    @Unique
    private boolean relicera$takingFallDamage;
    @Unique
    private boolean relicera$stonewallBash;

    @Inject(method = "disableShield", at = @At("HEAD"), cancellable = true)
    private void relicera$preventStonewallGreatshieldDisable(boolean fromAxe, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (StonewallGreatshieldEffects.isUsing(self)) {
            StonewallGreatshieldEffects.triggerResistedShieldDisable(self);
            ci.cancel();
        }
    }

    @Inject(method = "isScoping", at = @At("RETURN"), cancellable = true)
    private void relicera$recognizeLuminasCelestialLens(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            return;
        }

        Player self = (Player) (Object) this;
        cir.setReturnValue(self.isUsingItem() && self.getUseItem().is(ModItems.LUMINAS_CELESTIAL_LENS.get()));
    }

    @Inject(method = "getMovementEmission", at = @At("HEAD"), cancellable = true)
    private void relicera$suppressMovementEmission(CallbackInfoReturnable<Entity.MovementEmission> cir) {
        Player self = (Player) (Object) this;
        if (SilkOfNightDecoration.isWearing(self)) {
            cir.setReturnValue(Entity.MovementEmission.NONE);
        }
    }

    @Inject(method = "causeFallDamage", at = @At("HEAD"))
    private void relicera$beginFallDamage(float distance, float multiplier, DamageSource source,
                                          CallbackInfoReturnable<Boolean> cir) {
        relicera$takingFallDamage = SilkOfNightDecoration.isWearing((Player) (Object) this);
    }

    @Inject(method = "causeFallDamage", at = @At("RETURN"))
    private void relicera$endFallDamage(float distance, float multiplier, DamageSource source,
                                        CallbackInfoReturnable<Boolean> cir) {
        relicera$takingFallDamage = false;
    }

    @Override
    public boolean relicera$isTakingFallDamage() {
        return relicera$takingFallDamage;
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void relicera$prepareAttack(Entity target, CallbackInfo ci) {
        relicera$primaryAttackDamage = 0.0F;
        relicera$smashFallDistance = 0.0F;
        relicera$stonewallBash = false;

        Player self = (Player) (Object) this;
        if (!StonewallGreatshieldEffects.isUsingInMainHand(self)) {
            return;
        }
        if (!StonewallGreatshieldEffects.canBash(self)) {
            ci.cancel();
            return;
        }

        relicera$stonewallBash = true;
        StonewallGreatshieldEffects.beginBash(self);
    }

    @Inject(method = "attack", at = @At("RETURN"))
    private void relicera$finishAttack(Entity target, CallbackInfo ci) {
        if (relicera$stonewallBash) {
            StonewallGreatshieldEffects.endBash((Player) (Object) this);
        }
        relicera$stonewallBash = false;
    }

    @ModifyArg(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
            ),
            index = 4
    )
    private SoundEvent relicera$replaceStonewallBashSound(SoundEvent original) {
        if (relicera$stonewallBash && original != SoundEvents.PLAYER_ATTACK_NODAMAGE) {
            return SoundEvents.IRON_GOLEM_REPAIR;
        }
        return original;
    }

    @ModifyVariable(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/common/ForgeHooks;getCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/minecraftforge/event/entity/player/CriticalHitEvent;",
                    remap = false
            ),
            ordinal = 0
    )
    private float relicera$applySmashDamageBeforeCritical(float damage, Entity target) {
        relicera$smashFallDistance = ThousandweightGauntletsEffects.getSmashFallDistance(
                (Player) (Object) this,
                target
        );
        if (relicera$smashFallDistance > ThousandweightGauntletsEffects.SMASH_MINIMUM_FALL_DISTANCE) {
            return damage + ThousandweightGauntletsEffects.getSmashDamageBonus(relicera$smashFallDistance);
        }
        return damage;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean relicera$handlePrimaryAttack(Entity target, DamageSource source, float damage) {
        relicera$primaryAttackDamage = damage;
        float smashFallDistance = relicera$smashFallDistance;
        relicera$smashFallDistance = 0.0F;

        boolean hurt = target.hurt(source, damage);
        if (hurt) {
            ThousandweightGauntletsEffects.handleSuccessfulAttack((Player) (Object) this, target, smashFallDistance);
        }
        return hurt;
    }

    @ModifyArg(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private float relicera$applyFullSweepDamage(float damage) {
        Player self = (Player) (Object) this;
        if (relicera$primaryAttackDamage > 0.0F && TwoHandedSwordBroochEffects.hasSwordActive(self)) {
            return relicera$primaryAttackDamage;
        }
        return damage;
    }
}
