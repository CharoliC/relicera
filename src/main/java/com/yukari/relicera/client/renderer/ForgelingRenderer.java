package com.yukari.relicera.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.client.model.ForgelingModel;
import com.yukari.relicera.common.entity.forgeling.Forgeling;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public final class ForgelingRenderer extends MobRenderer<Forgeling, ForgelingModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ReliceraMod.MOD_ID, "textures/entity/forgeling.png");
    private static final ResourceLocation EMISSIVE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ReliceraMod.MOD_ID, "textures/entity/forgeling_emissive.png");
    private static final ResourceLocation SILVERED_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ReliceraMod.MOD_ID, "textures/entity/forgeling_silvered.png");
    private static final float VISUAL_SIZE = 2.0F;

    public ForgelingRenderer(EntityRendererProvider.Context context) {
        super(context, new ForgelingModel(), 0.5F);
        this.addLayer(new ForgelingEmissiveLayer(this));
    }

    @Override
    protected void scale(Forgeling forgeling, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.999F, 0.999F, 0.999F);
        poseStack.translate(0.0F, 0.001F, 0.0F);
        float squish = forgeling.getSquish(partialTick) / (VISUAL_SIZE * 0.5F + 1.0F);
        float horizontalScale = 1.0F / (squish + 1.0F);
        poseStack.scale(horizontalScale * VISUAL_SIZE,
                VISUAL_SIZE / horizontalScale,
                horizontalScale * VISUAL_SIZE);
    }

    @Override
    public ResourceLocation getTextureLocation(Forgeling forgeling) {
        return forgeling.isSilvered() ? SILVERED_TEXTURE : TEXTURE;
    }

    private static final class ForgelingEmissiveLayer extends RenderLayer<Forgeling, ForgelingModel> {
        private ForgelingEmissiveLayer(RenderLayerParent<Forgeling, ForgelingModel> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                           Forgeling forgeling, float limbSwing, float limbSwingAmount, float partialTick,
                           float ageInTicks, float netHeadYaw, float headPitch) {
            int magmaCubeLight = LightTexture.pack(15, LightTexture.sky(packedLight));
            VertexConsumer vertexConsumer = bufferSource.getBuffer(
                    RenderType.entityCutoutNoCull(EMISSIVE_TEXTURE)
            );
            this.getParentModel().renderToBuffer(
                    poseStack,
                    vertexConsumer,
                    magmaCubeLight,
                    OverlayTexture.NO_OVERLAY,
                    1.0F,
                    1.0F,
                    1.0F,
                    1.0F
            );
        }
    }
}
