package com.yukari.relicera;

import com.yukari.relicera.common.compat.lootr.LootrTreasureHuntersGlovesCompat;
import com.yukari.relicera.common.compat.twilightforest.ImmortalSoulRingCompat;
import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.config.ModClientConfig;
import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.registry.ModBlockEntities;
import com.yukari.relicera.registry.ModBlocks;
import com.yukari.relicera.registry.ModCreativeModeTabs;
import com.yukari.relicera.registry.ModEffects;
import com.yukari.relicera.registry.ModEntityTypes;
import com.yukari.relicera.registry.ModEnchantments;
import com.yukari.relicera.registry.ModItems;
import com.yukari.relicera.registry.ModLootModifiers;
import com.yukari.relicera.registry.ModMenuTypes;
import com.yukari.relicera.registry.ModParticleTypes;
import com.yukari.relicera.registry.ModRecipeSerializers;
import com.yukari.relicera.registry.ModRecipeTypes;
import com.yukari.relicera.registry.ModSoundEvents;
import com.yukari.relicera.registry.ModStructures;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ReliceraMod.MOD_ID)
public class ReliceraMod {
    public static final String MOD_ID = "relicera";
    public static final Rarity LEGENDARY = Rarity.create("RELICERA_LEGENDARY", ChatFormatting.GOLD);

    public ReliceraMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        context.registerConfig(ModConfig.Type.CLIENT, ModClientConfig.SPEC);
        context.registerConfig(ModConfig.Type.COMMON, ModCommonConfig.SPEC);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEffects.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModItems.register(modEventBus);
        ModEnchantments.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModParticleTypes.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);
        ModSoundEvents.register(modEventBus);
        ModStructures.register(modEventBus);
        ModNetworking.register();

        if (ModList.get().isLoaded("lootr")) {
            MinecraftForge.EVENT_BUS.register(LootrTreasureHuntersGlovesCompat.class);
        }
        if (ModList.get().isLoaded("twilightforest")) {
            MinecraftForge.EVENT_BUS.register(ImmortalSoulRingCompat.class);
        }
    }
}
