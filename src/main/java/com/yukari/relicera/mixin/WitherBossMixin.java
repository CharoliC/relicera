package com.yukari.relicera.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.yukari.relicera.common.entity.lumen.LumenMoon;
import com.yukari.relicera.common.entity.lumen.LumenStar;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin {
    @ModifyExpressionValue(method = "hurt", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;isPowered()Z"))
    private boolean relicera$allowLumenDamage(boolean original, DamageSource source, float amount) {
        Entity direct = source.getDirectEntity();
        return original && !(direct instanceof LumenMoon) && !(direct instanceof LumenStar);
    }
}
