package com.yukari.relicera.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.client.model.WarpedFungusBottleModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public final class WarpedFungusBottleRenderer implements ICurioRenderer.HumanoidRender {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ReliceraMod.MOD_ID, "textures/entity/curio/warped_fungus_bottle.png");
    private final WarpedFungusBottleModel model = new WarpedFungusBottleModel(
            Minecraft.getInstance().getEntityModels().bakeLayer(WarpedFungusBottleModel.LAYER_LOCATION));

    @Override
    public HumanoidModel<LivingEntity> getModel(ItemStack stack, SlotContext slotContext) {
        return model;
    }

    @Override
    public ResourceLocation getModelTexture(ItemStack stack, SlotContext slotContext) {
        return TEXTURE;
    }

    @Override
    public void renderModel(ItemStack stack, SlotContext slotContext, PoseStack poseStack,
                            RenderLayerParent<LivingEntity, EntityModel<LivingEntity>> renderLayerParent,
                            MultiBufferSource buffers, int packedLight) {
        if (renderLayerParent.getModel() instanceof HumanoidModel<?> humanoidModel) {
            model.body.copyFrom(humanoidModel.body);
        }
        model.body.visible = true;
        model.body.render(poseStack,
                ItemRenderer.getFoilBuffer(buffers, model.renderType(TEXTURE), false, stack.hasFoil()),
                packedLight, OverlayTexture.NO_OVERLAY);
    }
}
