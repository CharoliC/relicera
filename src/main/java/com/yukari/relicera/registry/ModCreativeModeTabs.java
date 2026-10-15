package com.yukari.relicera.registry;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.curio.CovenantTabletEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ReliceraMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> RELICERA = CREATIVE_MODE_TABS.register("relicera", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.relicera"))
                    .icon(() -> ModItems.ASTRAL_LENS.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        ModItems.ITEMS.getEntries().stream()
                                .map(RegistryObject::get)
                                .filter(item -> item != ModItems.LUMINAS_CELESTIAL_LENS.get()
                                        && item != ModItems.FORGELING_SPAWN_EGG.get()
                                        && item != ModItems.INSCRIBED_COVENANT_TABLET.get())
                                .forEach(item -> {
                                    if (item == ModItems.COVENANT_TABLET.get()) {
                                        // empty
                                        output.accept(item.getDefaultInstance());

                                        // full
                                        output.accept(CovenantTabletEffects.createFullyUnlockedStack());
                                    } else {
                                        output.accept(item.getDefaultInstance());
                                    }
                                });
                    })
                    .build());

    private ModCreativeModeTabs() {
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
