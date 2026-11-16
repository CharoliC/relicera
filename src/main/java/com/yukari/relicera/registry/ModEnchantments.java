package com.yukari.relicera.registry;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.enchantment.StarcrownEnchantment;
import com.yukari.relicera.common.enchantment.DaylightEnchantment;
import com.yukari.relicera.common.enchantment.MoonfrostEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ReliceraMod.MOD_ID);
    public static final RegistryObject<Enchantment> STARCROWN =
            ENCHANTMENTS.register("starcrown", StarcrownEnchantment::new);
    public static final RegistryObject<Enchantment> DAYLIGHT =
            ENCHANTMENTS.register("daylight", DaylightEnchantment::new);
    public static final RegistryObject<Enchantment> MOONFROST =
            ENCHANTMENTS.register("moonfrost", MoonfrostEnchantment::new);

    public static boolean isLumenEnchantment(Enchantment enchantment) {
        return enchantment == STARCROWN.get() || enchantment == DAYLIGHT.get() || enchantment == MOONFROST.get();
    }

    private ModEnchantments() {
    }

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
