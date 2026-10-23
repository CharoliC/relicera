package com.yukari.relicera.registry;

import com.mojang.serialization.Codec;
import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.loot.AddAstralStorybookLootModifier;
import com.yukari.relicera.common.loot.AddAlchemicalResidueLootModifier;
import com.yukari.relicera.common.loot.AddBruteBeltLootModifier;
import com.yukari.relicera.common.loot.AddDriedCrownLootModifier;
import com.yukari.relicera.common.loot.AddEphemeralBloomLootModifier;
import com.yukari.relicera.common.loot.AddExtinguishedSolarFurnaceLootModifier;
import com.yukari.relicera.common.loot.AddFeysilverIngotLootModifier;
import com.yukari.relicera.common.loot.AddForgottenThreadLootModifier;
import com.yukari.relicera.common.loot.AddHeartOfTheMountainLootModifier;
import com.yukari.relicera.common.loot.AddOceanRuinArchaeologyLootModifier;
import com.yukari.relicera.common.loot.AddReinforcedDeepslateLootModifier;
import com.yukari.relicera.common.loot.AddSculkFruitLootModifier;
import com.yukari.relicera.common.loot.AddWitheredLifeChaliceLootModifier;
import com.yukari.relicera.common.loot.AddVindicatorsMedalLootModifier;
import com.yukari.relicera.common.loot.AddWeatheredMountainCovenantLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ReliceraMod.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_FORGOTTEN_THREAD =
            LOOT_MODIFIER_SERIALIZERS.register("add_forgotten_thread", () -> AddForgottenThreadLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_EPHEMERAL_BLOOM =
            LOOT_MODIFIER_SERIALIZERS.register("add_ephemeral_bloom", () -> AddEphemeralBloomLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_FEYSILVER_INGOT =
            LOOT_MODIFIER_SERIALIZERS.register("add_feysilver_ingot", () -> AddFeysilverIngotLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ASTRAL_STORYBOOK =
            LOOT_MODIFIER_SERIALIZERS.register("add_astral_storybook", () -> AddAstralStorybookLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_EXTINGUISHED_SOLAR_FURNACE =
            LOOT_MODIFIER_SERIALIZERS.register("add_extinguished_solar_furnace", () -> AddExtinguishedSolarFurnaceLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_WITHERED_LIFE_CHALICE =
            LOOT_MODIFIER_SERIALIZERS.register("add_withered_life_chalice", () -> AddWitheredLifeChaliceLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_OCEAN_RUIN_ARCHAEOLOGY =
            LOOT_MODIFIER_SERIALIZERS.register("add_ocean_ruin_archaeology", () -> AddOceanRuinArchaeologyLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_DRIED_CROWN =
            LOOT_MODIFIER_SERIALIZERS.register("add_dried_crown", () -> AddDriedCrownLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_VINDICATORS_MEDAL =
            LOOT_MODIFIER_SERIALIZERS.register("add_vindicators_medal", () -> AddVindicatorsMedalLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_HEART_OF_THE_MOUNTAIN =
            LOOT_MODIFIER_SERIALIZERS.register("add_heart_of_the_mountain", () -> AddHeartOfTheMountainLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_WEATHERED_MOUNTAIN_COVENANT =
            LOOT_MODIFIER_SERIALIZERS.register("add_weathered_mountain_covenant",
                    () -> AddWeatheredMountainCovenantLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_BRUTE_BELT =
            LOOT_MODIFIER_SERIALIZERS.register("add_brute_belt", () -> AddBruteBeltLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_REINFORCED_DEEPSLATE =
            LOOT_MODIFIER_SERIALIZERS.register("add_reinforced_deepslate", () -> AddReinforcedDeepslateLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ALCHEMICAL_RESIDUE =
            LOOT_MODIFIER_SERIALIZERS.register("add_alchemical_residue", () -> AddAlchemicalResidueLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_SCULK_FRUIT =
            LOOT_MODIFIER_SERIALIZERS.register("add_sculk_fruit", () -> AddSculkFruitLootModifier.CODEC);

    private ModLootModifiers() {
    }

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
    }
}
