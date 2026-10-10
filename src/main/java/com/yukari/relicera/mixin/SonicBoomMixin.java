package com.yukari.relicera.mixin;

import com.yukari.relicera.common.item.SoulknotStoneEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SonicBoom.class)
public abstract class SonicBoomMixin {
        @ModifyArg(method = "*", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
            index = 1)
    private static float relicera$increaseSoulOverloadDamage(DamageSource source, float damage) {
        if (source.getEntity() instanceof Warden warden) {
            return damage + SoulknotStoneEffects.damageBonus(warden);
        }
        return damage;
    }
}
