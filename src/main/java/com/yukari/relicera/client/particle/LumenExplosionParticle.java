package com.yukari.relicera.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class LumenExplosionParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    private LumenExplosionParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.lifetime = 21;
        this.quadSize = 2.0F;
        this.hasPhysics = false;
        this.setSprite(sprites.get(0, 12));
        this.setBoundingBox(this.getBoundingBox().inflate(2.1D).move(0.0D, 1.9D, 0.0D));
    }

    @Override
    public void tick() {
        if (++this.age >= this.lifetime) {
            this.remove();
        } else {
            int frame = this.age < 5 ? this.age : 5 + (this.age - 5) / 2;
            this.setSprite(this.sprites.get(frame, 12));
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        Vec3 cameraPos = camera.getPosition();
        float x = (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x);
        float y = (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y);
        float z = (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z);
        Quaternionf rotation = new Quaternionf().rotationY(-camera.getYRot() * Mth.DEG_TO_RAD);
        float halfWidth = this.quadSize * 95.0F / 96.0F;
        Vector3f[] corners = {
                new Vector3f(-halfWidth, -0.1F, 0.0F), new Vector3f(-halfWidth, 3.9F, 0.0F),
                new Vector3f(halfWidth, 3.9F, 0.0F), new Vector3f(halfWidth, -0.1F, 0.0F)
        };
        float[] u = {getU1(), getU1(), getU0(), getU0()};
        float[] v = {getV1(), getV0(), getV0(), getV1()};
        for (int i = 0; i < corners.length; i++) {
            Vector3f point = corners[i].rotate(rotation).add(x, y, z);
            buffer.vertex(point.x, point.y, point.z).uv(u[i], v[i])
                    .color(this.rCol, this.gCol, this.bCol, this.alpha)
                    .uv2(LightTexture.FULL_BRIGHT).endVertex();
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

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new LumenExplosionParticle(level, x, y, z, this.sprites);
        }
    }
}
