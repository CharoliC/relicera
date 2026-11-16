package com.yukari.relicera.common.entity.lumen;

import com.yukari.relicera.common.particle.CircleParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

final class LumenCircles {
    static final CircleParticleOptions MOON_IMPACT =
            new CircleParticleOptions(new Vec3(0.0D, 1.0D, 0.0D), 1.2F, 9.0F, 24, 8);
    static final CircleParticleOptions STAR_IMPACT =
            new CircleParticleOptions(new Vec3(0.0D, 1.0D, 0.0D), 0.6F, 4.5F, 16, 5);

    private LumenCircles() {
    }

    static CircleParticleOptions flight(Vec3 direction, double speed) {
        float scale = 1.0F + (float) Mth.clamp((speed - 0.3D) / 2.7D, 0.0D, 1.0D);
        return new CircleParticleOptions(direction, 0.6F * scale, 1.6F * scale, 14, 4);
    }
}
