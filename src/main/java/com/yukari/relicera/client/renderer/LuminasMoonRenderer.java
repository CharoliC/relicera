package com.yukari.relicera.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yukari.relicera.ReliceraMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.model.data.ModelData;

public final class LuminasMoonRenderer {
    public static final ResourceLocation MOON_MODEL = ResourceLocation.fromNamespaceAndPath(
            ReliceraMod.MOD_ID,
            "block/luminas_moon"
    );

    private static final float ORBIT_SPEED = 0.025F;
    private static final float ORBIT_VERTICAL_TILT = 0.22F;
    private static final float ORBIT_DEPTH_SCALE = 0.72F;
    private static final float MOON_SCALE = 0.72F;
    private static final float SELF_ROTATION_SPEED = 2.4F;
    private static final float BOB_SPEED = 0.09F;
    private static final float BOB_HEIGHT = 0.045F;

    private LuminasMoonRenderer() {
    }

    public static void render(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick) {
        float time = entity.tickCount + partialTick;
        float phase = (entity.getId() * 37 % 360) * Mth.DEG_TO_RAD;
        float angle = time * ORBIT_SPEED + phase;
        float orbitRadius = Math.max(0.62F, entity.getBbWidth() * 0.75F + 0.52F);
        float orbitHeight = Mth.clamp(entity.getBbHeight() * 0.78F, 0.72F, 1.85F);
        float x = Mth.cos(angle) * orbitRadius;
        float y = orbitHeight + Mth.sin(angle) * ORBIT_VERTICAL_TILT + Mth.sin(time * BOB_SPEED + phase) * BOB_HEIGHT;
        float z = Mth.sin(angle) * orbitRadius * ORBIT_DEPTH_SCALE;

        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees((time * SELF_ROTATION_SPEED + entity.getId() * 13) % 360.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(12.0F));
        poseStack.scale(MOON_SCALE, MOON_SCALE, MOON_SCALE);
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        renderModel(poseStack, bufferSource);
        poseStack.popPose();
    }

    private static void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        BakedModel model = getMoonModel();
        if (model == null) {
            return;
        }

        RenderType renderType = RenderType.translucent();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                vertexConsumer,
                null,
                model,
                1.0F,
                1.0F,
                1.0F,
                LightTexture.FULL_BLOCK,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                renderType
        );
    }

    private static BakedModel getMoonModel() {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BakedModel model = modelManager.getModel(MOON_MODEL);
        return model == modelManager.getMissingModel() ? null : model;
    }
}
