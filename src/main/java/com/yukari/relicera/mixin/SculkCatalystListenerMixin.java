package com.yukari.relicera.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.yukari.relicera.common.block.SculkFruitCropBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SculkCatalystBlockEntity.CatalystListener.class)
public abstract class SculkCatalystListenerMixin {
    @Shadow
    public abstract PositionSource getListenerSource();

    @WrapOperation(method = "handleGameEvent", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/SculkSpreader;addCursors(Lnet/minecraft/core/BlockPos;I)V"))
    private void relicera$feedSculkFruit(SculkSpreader spreader, BlockPos sourcePos, int charge,
                                       Operation<Void> original,
                                       ServerLevel level, GameEvent event, GameEvent.Context context, Vec3 eventPos) {
        int remaining = getListenerSource().getPosition(level)
                .map(position -> level.getBlockEntity(BlockPos.containing(position).above())
                        instanceof SculkFruitCropBlockEntity crop ? crop.absorbCharge(charge) : charge)
                .orElse(charge);
        if (remaining > 0) {
            original.call(spreader, sourcePos, remaining);
        }
    }
}
