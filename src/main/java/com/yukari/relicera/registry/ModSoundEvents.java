package com.yukari.relicera.registry;

import com.yukari.relicera.ReliceraMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ReliceraMod.MOD_ID);

    public static final RegistryObject<SoundEvent> PASTORAL_MELODY_PLAY = register("item.pastoral_melody.play");
    public static final RegistryObject<SoundEvent> RABBITS_POCKET_WATCH_USE = register("item.rabbits_pocket_watch.use");
    public static final RegistryObject<SoundEvent> IMMORTAL_SOUL_RING_SHIELD = register("item.immortal_soul_ring.shield");
    public static final RegistryObject<SoundEvent> LUMEN_FIRE = register("item.lumen.fire");

    private ModSoundEvents() {
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

    private static RegistryObject<SoundEvent> register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
