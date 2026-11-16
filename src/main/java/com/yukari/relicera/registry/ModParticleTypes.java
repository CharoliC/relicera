package com.yukari.relicera.registry;

import com.yukari.relicera.ReliceraMod;
import com.mojang.serialization.Codec;
import com.yukari.relicera.common.particle.CircleParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ReliceraMod.MOD_ID);

    public static final RegistryObject<SimpleParticleType> ELECTRIC_SPARK = PARTICLE_TYPES.register("electric_spark", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GOLDHEART_0 = PARTICLE_TYPES.register("goldheart_0", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GOLDHEART_1 = PARTICLE_TYPES.register("goldheart_1", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GOLDHEART_2 = PARTICLE_TYPES.register("goldheart_2", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FLY = PARTICLE_TYPES.register("fly", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> DREAM_BUBBLE = PARTICLE_TYPES.register("dream_bubble", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> HOLY_LIGHT = PARTICLE_TYPES.register("holy_light", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> LICH_FIRE = PARTICLE_TYPES.register("lich_fire", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> LUMEN_EXPLOSION = PARTICLE_TYPES.register("lumen_explosion", () -> new SimpleParticleType(false));
    public static final RegistryObject<ParticleType<CircleParticleOptions>> CIRCLE = PARTICLE_TYPES.register("circle",
            () -> new ParticleType<CircleParticleOptions>(false, CircleParticleOptions.DESERIALIZER) {
                @Override
                public Codec<CircleParticleOptions> codec() {
                    return CircleParticleOptions.CODEC;
                }
            });
    public static final RegistryObject<SimpleParticleType> FEYSILVER_SOLUTION_POP = PARTICLE_TYPES.register("feysilver_solution_pop", () -> new SimpleParticleType(false));

    private ModParticleTypes() {
    }

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
