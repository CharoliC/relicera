package com.yukari.relicera.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.yukari.relicera.common.curio.RingOfSatietyEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "intValue=18"))
    private int relicera$lowerNaturalRegenerationThreshold(int original, Player player) {
        return RingOfSatietyEffects.isActive(player) ? Math.min(original, 14) : original;
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "intValue=80", ordinal = 0))
    private int relicera$accelerateNaturalRegeneration(int original, Player player) {
        return RingOfSatietyEffects.isActive(player) && player.getFoodData().getFoodLevel() >= 16
                ? Math.min(original, 10)
                : original;
    }

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Math;min(FF)F"
            )
    )
    private float relicera$keepSaturationRegenerationConsistent(float saturation, float maximum,
                                                                 Operation<Float> original, Player player) {
        return original.call(RingOfSatietyEffects.isActive(player) ? maximum : saturation, maximum);
    }
}
