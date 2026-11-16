package com.yukari.relicera.registry;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.entity.forgeling.Forgeling;
import com.yukari.relicera.common.entity.lumen.LumenMoon;
import com.yukari.relicera.common.entity.lumen.LumenStar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ReliceraMod.MOD_ID);

    public static final RegistryObject<EntityType<Forgeling>> FORGELING = ENTITY_TYPES.register("forgeling", () ->
            EntityType.Builder.of(Forgeling::new, MobCategory.CREATURE)
                    .sized(1.04F, 1.04F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .updateInterval(3)
                    .build("forgeling"));

    public static final RegistryObject<EntityType<LumenMoon>> LUMEN_MOON = ENTITY_TYPES.register("lumen_moon", () ->
            EntityType.Builder.<LumenMoon>of(LumenMoon::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .build("lumen_moon"));

    public static final RegistryObject<EntityType<LumenStar>> LUMEN_STAR = ENTITY_TYPES.register("lumen_star", () ->
            EntityType.Builder.<LumenStar>of(LumenStar::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .build("lumen_star"));

    private ModEntityTypes() {
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
