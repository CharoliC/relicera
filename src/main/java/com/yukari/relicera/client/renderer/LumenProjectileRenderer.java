package com.yukari.relicera.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.yukari.relicera.ReliceraMod;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public final class LumenProjectileRenderer<T extends AbstractArrow> extends EntityRenderer<T> {
    public static final ResourceLocation MOON_MODEL = ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "entity/lumen_moon");
    public static final ResourceLocation MOON_OUTLINE_MODEL = ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "entity/lumen_moon_outline");
    public static final ResourceLocation STAR_MODEL = ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "entity/lumen_star");
    public static final ResourceLocation STAR_OUTLINE_MODEL = ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "entity/lumen_star_outline");
    private final ResourceLocation coreModel;
    private final ResourceLocation outlineModel;
    private final float scale;

    public LumenProjectileRenderer(EntityRendererProvider.Context context, boolean star) {
        super(context);
        coreModel = star ? STAR_MODEL : MOON_MODEL;
        outlineModel = star ? STAR_OUTLINE_MODEL : MOON_OUTLINE_MODEL;
        scale = star ? 1.08F : 2.16F;
    }

    @Override
    public void render(T projectile, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, projectile.getBbHeight() * 0.5D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees((projectile.tickCount + partialTick) * 2.4F));
        poseStack.mulPose(Axis.XP.rotationDegrees(12.0F));
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5D, -0.25D, -0.5D);
        renderModel(coreModel, LumenRenderTypes.core(TextureAtlas.LOCATION_BLOCKS), poseStack, buffer);
        renderModel(outlineModel, LumenRenderTypes.glow(TextureAtlas.LOCATION_BLOCKS), poseStack, buffer);
        poseStack.popPose();
        super.render(projectile, yaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T projectile) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    private static void renderModel(ResourceLocation location, RenderType type, PoseStack poseStack,
                                    MultiBufferSource buffer) {
        Minecraft minecraft = Minecraft.getInstance();
        var model = minecraft.getModelManager().getModel(location);
        if (model != minecraft.getModelManager().getMissingModel()) {
            minecraft.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(type),
                    null, model, 1.0F, 1.0F, 1.0F, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                    ModelData.EMPTY, type);
        }
    }
}
