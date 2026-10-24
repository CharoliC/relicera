package com.yukari.relicera.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerItemInHandLayer.class)
public abstract class PlayerItemInHandLayerMixin {
    @ModifyExpressionValue(
            method = "renderArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z",
                    ordinal = 0
            )
    )
    private boolean relicera$recognizeLuminasCelestialLens(boolean original, LivingEntity entity, ItemStack stack,
                                                          ItemDisplayContext displayContext, HumanoidArm arm,
                                                          PoseStack poseStack, MultiBufferSource buffers, int light) {
        return original || stack.is(ModItems.LUMINAS_CELESTIAL_LENS.get());
    }
}
