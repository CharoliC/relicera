package com.yukari.relicera.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public abstract class LumenRenderTypes extends RenderType {
    private static final TransparencyStateShard ADDITIVE = new TransparencyStateShard("lumen_additive", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    private static final Function<ResourceLocation, RenderType> GLOW = Util.memoize(texture ->
            create("lumen_glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
                    CompositeState.builder()
                            .setShaderState(RENDERTYPE_EYES_SHADER)
                            .setTextureState(new TextureStateShard(texture, false, false))
                            .setTransparencyState(ADDITIVE)
                            .setCullState(NO_CULL)
                            .setLightmapState(NO_LIGHTMAP)
                            .setOverlayState(NO_OVERLAY)
                            .setWriteMaskState(COLOR_WRITE)
                            .createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> CORE = Util.memoize(texture ->
            create("lumen_core", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
                    CompositeState.builder()
                            .setShaderState(RENDERTYPE_EYES_SHADER)
                            .setTextureState(new TextureStateShard(texture, false, false))
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                            .setCullState(NO_CULL)
                            .setLightmapState(NO_LIGHTMAP)
                            .setOverlayState(NO_OVERLAY)
                            .setWriteMaskState(COLOR_DEPTH_WRITE)
                            .createCompositeState(false)));

    private LumenRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int size,
                             boolean crumbling, boolean sort, Runnable setup, Runnable clear) {
        super(name, format, mode, size, crumbling, sort, setup, clear);
    }

    public static RenderType glow(ResourceLocation texture) {
        return GLOW.apply(texture);
    }

    public static RenderType core(ResourceLocation texture) {
        return CORE.apply(texture);
    }
}
