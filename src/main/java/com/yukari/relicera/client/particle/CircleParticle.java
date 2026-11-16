package com.yukari.relicera.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yukari.relicera.common.particle.CircleParticleOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class CircleParticle extends TextureSheetParticle {
    private final CircleParticleOptions options;
    private final Quaternionf rotation;

    private CircleParticle(ClientLevel level, double x, double y, double z,
                           SpriteSet sprites, CircleParticleOptions options) {
        super(level, x, y, z);
        this.options = options;
        lifetime = options.duration();
        hasPhysics = false;
        pickSprite(sprites);
        Vec3 normal = options.normal();
        rotation = new Quaternionf().rotationTo(0.0F, 0.0F, 1.0F,
                (float) normal.x, (float) normal.y, (float) normal.z);
        setBoundingBox(new AABB(x, y, z, x, y, z)
                .inflate(Math.max(options.startDiameter(), options.endDiameter()) * 0.5D));
    }

    @Override
    public void tick() {
        if (++age >= lifetime) {
            remove();
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        float progress = Mth.clamp((age + partialTick) / lifetime, 0.0F, 1.0F);
        float remaining = 1.0F - progress;
        float radius = Mth.lerp(1.0F - remaining * remaining,
                options.startDiameter(), options.endDiameter()) * 0.5F;
        float fade = Mth.clamp((age + partialTick - options.opaqueTicks())
                / (lifetime - options.opaqueTicks()), 0.0F, 1.0F);
        float opacity = 1.0F - fade * fade * (3.0F - 2.0F * fade);
        Vec3 origin = new Vec3(x, y, z).subtract(camera.getPosition());
        Vector3f[] corners = {
                new Vector3f(-radius, -radius, 0.0F), new Vector3f(-radius, radius, 0.0F),
                new Vector3f(radius, radius, 0.0F), new Vector3f(radius, -radius, 0.0F)
        };
        float[] u = {getU1(), getU1(), getU0(), getU0()};
        float[] v = {getV1(), getV0(), getV0(), getV1()};
        for (Vector3f corner : corners) {
            corner.rotate(rotation).add((float) origin.x, (float) origin.y, (float) origin.z);
        }
        for (int side = 0; side < 2; side++) {
            for (int vertex = 0; vertex < 4; vertex++) {
                int i = side == 0 ? vertex : 3 - vertex;
                Vector3f point = corners[i];
                buffer.vertex(point.x, point.y, point.z).uv(u[i], v[i])
                        .color(1.0F, 1.0F, 1.0F, opacity).uv2(LightTexture.FULL_BRIGHT).endVertex();
            }
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<CircleParticleOptions> {
        @Override
        public Particle createParticle(CircleParticleOptions options, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new CircleParticle(level, x, y, z, sprites, options);
        }
    }
}
