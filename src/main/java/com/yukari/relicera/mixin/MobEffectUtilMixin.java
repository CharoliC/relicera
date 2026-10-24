package com.yukari.relicera.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.yukari.relicera.common.curio.NereiasCrownEffects;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEffectUtil.class)
public abstract class MobEffectUtilMixin {
    @ModifyExpressionValue(method = "addEffectToPlayersAround", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getPlayers(Ljava/util/function/Predicate;)Ljava/util/List;"))
    private static List<ServerPlayer> relicera$skipNereiasCrownWearers(List<ServerPlayer> original,
                                                                    ServerLevel level, Entity source, Vec3 position,
                                                                    double radius, MobEffectInstance effect,
                                                                    int displayLimit) {
        if (!(source instanceof ElderGuardian) || effect.getEffect() != MobEffects.DIG_SLOWDOWN) {
            return original;
        }
        List<ServerPlayer> players = new ArrayList<>(original);
        players.removeIf(NereiasCrownEffects::isEquipped);
        return players;
    }
}
