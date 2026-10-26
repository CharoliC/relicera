package com.yukari.relicera.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TemptGoal.class)
public abstract class TemptGoalMixin {
    @Shadow
    @Final
    protected PathfinderMob mob;

    @ModifyReturnValue(method = "shouldFollow", at = @At("RETURN"))
    private boolean relicera$recognizePastoralMelody(boolean original, LivingEntity holder) {
        return original || mob instanceof Animal
                && (holder.getMainHandItem().is(ModItems.PASTORAL_MELODY.get())
                || holder.getOffhandItem().is(ModItems.PASTORAL_MELODY.get()));
    }
}
