package com.yukari.relicera.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class ModCommonConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.DoubleValue FORGOTTEN_THREAD_VILLAGE_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue FORGOTTEN_THREAD_ANCIENT_CITY_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue FORGOTTEN_THREAD_CAT_GIFT_CHANCE;
    public static final ForgeConfigSpec.IntValue REVIVAL_NECTAR_ILUTHIAS_BLESSING_DURATION;
    public static final ForgeConfigSpec.IntValue ILUTHIAS_BLESSING_MAX_ABSORPTION;
    public static final ForgeConfigSpec.DoubleValue EPHEMERAL_BLOOM_SNIFFER_DIGGING_NEW_MOON_CHANCE;
    public static final ForgeConfigSpec.DoubleValue EPHEMERAL_BLOOM_STRONGHOLD_LIBRARY_CHEST_CHANCE;
    public static final ForgeConfigSpec.IntValue EPHEMERAL_BLOOM_ABSORPTION_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue EPHEMERAL_BLOOM_MAX_ABSORPTION;
    public static final ForgeConfigSpec.IntValue SCULK_FRUIT_EXPERIENCE_PER_STAGE;
    public static final ForgeConfigSpec.DoubleValue NIGHT_GLOVES_NIGHT_ATTACK_DAMAGE_BONUS;
    public static final ForgeConfigSpec.DoubleValue TREASURE_HUNTERS_GLOVES_LUCK_PER_CHEST;
    public static final ForgeConfigSpec.IntValue TREASURE_HUNTERS_GLOVES_MAX_ITEMS_TAKEN_BEFORE_REFRESH;
    public static final ForgeConfigSpec.DoubleValue ASTRAL_STORYBOOK_ADVANCED_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue WARFIRE_FRAGMENT_IRON_GOLEM_DROP_CHANCE;
    public static final ForgeConfigSpec.DoubleValue WARFIRE_FRAGMENT_ALLAY_AURA_RANGE;
    public static final ForgeConfigSpec.DoubleValue ROTTEN_TUSK_PIGLIN_REPEL_RANGE;
    public static final ForgeConfigSpec.DoubleValue FEYSILVER_INGOT_OVERWORLD_CHEST_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> FEYSILVER_FORGING_ART_VOLUME_ONE_ITEM_BLACKLIST;
    public static final ForgeConfigSpec.DoubleValue MASTER_SMITHS_BROOCH_ARMOR_TOUGHNESS_PER_TRIMMED_ARMOR;
    public static final ForgeConfigSpec.DoubleValue MASTER_SMITHS_BROOCH_DAMAGE_REDUCTION_PER_COATED_ARMOR;
    public static final ForgeConfigSpec.DoubleValue TWO_HANDED_SWORD_BROOCH_SWORD_DAMAGE_BONUS;
    public static final ForgeConfigSpec.DoubleValue TWO_HANDED_SWORD_BROOCH_ATTACK_SPEED_PENALTY;
    public static final ForgeConfigSpec.DoubleValue TWO_HANDED_SWORD_BROOCH_ENTITY_REACH_BONUS;
    public static final ForgeConfigSpec.DoubleValue VINDICATORS_MEDAL_AXE_ATTACK_SPEED_BONUS;
    public static final ForgeConfigSpec.DoubleValue VINDICATORS_MEDAL_VINDICATOR_DROP_CHANCE;
    public static final ForgeConfigSpec.DoubleValue VINDICATORS_MEDAL_STRUCTURE_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue TURNCOATS_MEDAL_HERO_DAMAGE_BONUS_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue TURNCOATS_MEDAL_BAD_OMEN_LIFE_STEAL_PER_LEVEL;
    public static final ForgeConfigSpec.IntValue TURNCOATS_MEDAL_BANNER_EFFECT_EXTENSION_TICKS;
    public static final ForgeConfigSpec.IntValue TURNCOATS_MEDAL_PATROL_ATTEMPT_INTERVAL_TICKS;
    public static final ForgeConfigSpec.DoubleValue STORMSCALE_ELDER_GUARDIAN_THUNDERSTORM_DROP_CHANCE;
    public static final ForgeConfigSpec.DoubleValue RIPPLEHEART_PEARL_AXOLOTL_ASSIST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue RIPPLEHEART_PEARL_WARM_OCEAN_RUIN_ARCHAEOLOGY_CHANCE;
    public static final ForgeConfigSpec.DoubleValue PASTORAL_MELODY_ANIMAL_RANGE;
    public static final ForgeConfigSpec.DoubleValue EXTINGUISHED_SOLAR_FURNACE_BASTION_TREASURE_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue EXTINGUISHED_SOLAR_FURNACE_NETHER_FORTRESS_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue EXTINGUISHED_SOLAR_FURNACE_WEAPONSMITH_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue WITHERED_LIFE_CHALICE_WOODLAND_MANSION_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue WITHERED_LIFE_CHALICE_DESERT_PYRAMID_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue WITHERED_LIFE_CHALICE_JUNGLE_TEMPLE_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue WITHERED_LIFE_CHALICE_SIMPLE_DUNGEON_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue DRIED_CROWN_SHIPWRECK_TREASURE_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue DRIED_CROWN_UNDERWATER_RUIN_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue DRIED_CROWN_BURIED_TREASURE_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue DRIED_CROWN_OCEAN_RUIN_ARCHAEOLOGY_CHANCE;
    public static final ForgeConfigSpec.DoubleValue WEATHERED_MOUNTAIN_COVENANT_TRAIL_RUINS_ARCHAEOLOGY_CHANCE;
    public static final ForgeConfigSpec.DoubleValue WEATHERED_MOUNTAIN_COVENANT_PILLAGER_OUTPOST_CHEST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue WEATHERED_MOUNTAIN_COVENANT_MINESHAFT_DUNGEON_CHEST_CHANCE;
    public static final ForgeConfigSpec.IntValue BRUTAL_PLUNDER_BADGE_LOOTING_BONUS;
    public static final ForgeConfigSpec.DoubleValue BRUTAL_PLUNDER_BADGE_DAMAGE_BONUS_PER_LOOTING_LEVEL;
    public static final ForgeConfigSpec.DoubleValue BRUTAL_PLUNDER_BADGE_MAX_DAMAGE_BONUS;
    public static final ForgeConfigSpec.DoubleValue ASHEN_TOUCH_BURNING_TARGET_DAMAGE_BONUS;
    public static final ForgeConfigSpec.DoubleValue RIPPLEHEART_RINGS_ACTIVATION_RANGE;
    public static final ForgeConfigSpec.IntValue RIPPLEHEART_RINGS_COMBAT_EFFECT_DURATION_TICKS;
    public static final ForgeConfigSpec.DoubleValue RIPPLEHEART_RINGS_RED_DAMAGE_BONUS;
    public static final ForgeConfigSpec.DoubleValue RIPPLEHEART_RINGS_RED_ATTACK_SPEED_BONUS;
    public static final ForgeConfigSpec.DoubleValue RIPPLEHEART_RINGS_BLUE_DAMAGE_REDUCTION;
    public static final ForgeConfigSpec.DoubleValue RIPPLEHEART_RINGS_BLUE_HEALING_BONUS;
    public static final ForgeConfigSpec.DoubleValue LITTLE_TAILORS_BELT_REACTION_RANGE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LITTLE_TAILORS_BELT_INSTANT_KILL_ENTITY_TYPES;
    public static final ForgeConfigSpec.DoubleValue WARRIOR_BELT_KNOCKBACK_RESISTANCE;
    public static final ForgeConfigSpec.DoubleValue WARRIOR_BELT_MOVEMENT_SPEED_BONUS;
    public static final ForgeConfigSpec.DoubleValue WARRIOR_BELT_CRITICAL_DAMAGE_BONUS;
    public static final ForgeConfigSpec.DoubleValue BRUTE_BELT_MAX_HEALTH_BONUS;
    public static final ForgeConfigSpec.DoubleValue BRUTE_BELT_PIGLIN_BRUTE_DROP_CHANCE;
    public static final ForgeConfigSpec.DoubleValue VERDANT_SCALE_BELT_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.DoubleValue IMMORTAL_SOUL_RING_MAGIC_DAMAGE_RATIO;
    public static final ForgeConfigSpec.DoubleValue IMMORTAL_SOUL_RING_KILL_HEAL_RATIO;
    public static final ForgeConfigSpec.DoubleValue IMMORTAL_SOUL_RING_COMBO_THRESHOLD;
    public static final ForgeConfigSpec.IntValue IMMORTAL_SOUL_RING_COMBO_TIMEOUT_TICKS;
    public static final ForgeConfigSpec.IntValue IMMORTAL_SOUL_RING_MAX_SHIELDS;
    public static final ForgeConfigSpec.DoubleValue THOUSANDWEIGHT_GAUNTLETS_ATTACK_SPEED_REDUCTION;
    public static final ForgeConfigSpec.DoubleValue THOUSANDWEIGHT_GAUNTLETS_WIND_BURST_LAUNCH_HEIGHT;
    public static final ForgeConfigSpec.IntValue RABBITS_POCKET_WATCH_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DIVINE_SEVERANCE_RING_HEAD_DROPS;
    public static final ForgeConfigSpec.DoubleValue STRIDER_SPURS_SPEED_BONUS;
    public static final ForgeConfigSpec.DoubleValue WARP_CRYSTAL_DODGE_CHANCE;
    public static final ForgeConfigSpec.DoubleValue COVENANT_TABLET_DAMAGE_TASK_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue COVENANT_TABLET_MINING_SPEED_BONUS;
    public static final ForgeConfigSpec.DoubleValue COVENANT_TABLET_VILLAGER_TRADE_DISCOUNT;
    public static final ForgeConfigSpec.DoubleValue GRANBELLS_FURNACE_DAMAGE_BONUS;
    public static final ForgeConfigSpec.DoubleValue BAROM_VOLUME_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue BAROM_HUSK_HUNGER_CHANCE;
    public static final ForgeConfigSpec.DoubleValue BAROM_WITCH_POTION_PRESERVATION_CHANCE;
    public static final ForgeConfigSpec.BooleanValue GRANBELLS_FURNACE_PRESERVE_SMITHING_TEMPLATES;
    public static final ForgeConfigSpec.BooleanValue GRANBELLS_FURNACE_KEEP_INVENTORY_IN_FIRE_OR_LAVA;
    public static final ForgeConfigSpec.DoubleValue ILUTHIAS_CHALICE_UNDEAD_DAMAGE_BONUS;
    public static final ForgeConfigSpec.DoubleValue ILUTHIAS_CHALICE_UNDEAD_DAMAGE_REDUCTION;
    public static final ForgeConfigSpec.IntValue ILUTHIAS_CHALICE_ENHANCED_TOTEM_ILUTHIAS_BLESSING_DURATION;
    public static final ForgeConfigSpec.DoubleValue NEREIAS_CROWN_ACTIVE_DAMAGE_REDUCTION;
    public static final ForgeConfigSpec.DoubleValue NEREIAS_CROWN_AQUATIC_AURA_RANGE;
    public static final ForgeConfigSpec.DoubleValue NEREIAS_CROWN_AQUATIC_ATTACK_DAMAGE_SHARE;
    public static final ForgeConfigSpec.DoubleValue NEREIAS_CROWN_AQUATIC_MAX_HEALTH_ARMOR_SHARE;
    public static final ForgeConfigSpec.DoubleValue DREAMCATCHER_BOX_SLEEP_RANGE;
    public static final ForgeConfigSpec.DoubleValue TEMPEST_SPRINT_SPEED_BONUS;
    public static final ForgeConfigSpec.DoubleValue TEMPEST_SPRINT_DAMAGE_REDUCTION;
    public static final ForgeConfigSpec.DoubleValue TEMPEST_SPRINT_COLLISION_KNOCKBACK;
    public static final ForgeConfigSpec.DoubleValue TEMPEST_SPRINT_COLLISION_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue TEMPEST_SPRINT_FULL_JUMP_SHOCKWAVE_RANGE;
    public static final ForgeConfigSpec.DoubleValue TEMPEST_SPRINT_FULL_JUMP_SHOCKWAVE_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue TEMPEST_SPRINT_FULL_JUMP_SHOCKWAVE_KNOCKBACK;
    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("sculk_fruit");
        SCULK_FRUIT_EXPERIENCE_PER_STAGE = BUILDER
                .comment("Sculk Catalyst charge consumed per growth stage.")
                .defineInRange("experiencePerStage", 30, 1, 1000000);
        BUILDER.pop();

        BUILDER.push("forgotten_thread");

        FORGOTTEN_THREAD_VILLAGE_CHEST_CHANCE = BUILDER
                .comment("Chance for Forgotten Thread to be added to village chest loot. 0.04 = 4%.")
                .defineInRange("villageChestChance", 0.06D, 0.0D, 1.0D);

        FORGOTTEN_THREAD_ANCIENT_CITY_CHEST_CHANCE = BUILDER
                .comment("Chance for Forgotten Thread to be added to ancient city chest loot. 0.03 = 3%.")
                .defineInRange("ancientCityChestChance", 0.03D, 0.0D, 1.0D);

        FORGOTTEN_THREAD_CAT_GIFT_CHANCE = BUILDER
                .comment("Chance for Forgotten Thread to be added to cat morning gifts. 0.20 = 20%.")
                .defineInRange("catGiftChance", 0.20D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("revival_nectar");

        REVIVAL_NECTAR_ILUTHIAS_BLESSING_DURATION = BUILDER
                .comment("Duration in ticks for Iluthia's Blessing granted by Revival Nectar. 400 ticks = 20 seconds.")
                .defineInRange("iluthiasBlessingDuration", 20 * 20, 1, 20 * 60 * 60);

        BUILDER.pop();

        BUILDER.push("iluthias_blessing");

        ILUTHIAS_BLESSING_MAX_ABSORPTION = BUILDER
                .comment("Maximum total absorption amount Iluthia's Blessing can accumulate. 60 = 30 absorption hearts. Existing absorption above this value is not reduced.")
                .defineInRange("maxAbsorption", 60, 0, 1024);

        BUILDER.pop();

        BUILDER.push("ephemeral_bloom");

        EPHEMERAL_BLOOM_SNIFFER_DIGGING_NEW_MOON_CHANCE = BUILDER
                .comment("Chance for Ephemeral Bloom to be added to sniffer digging loot during a new moon.")
                .defineInRange("snifferDiggingNewMoonChance", 0.17D, 0.0D, 1.0D);

        EPHEMERAL_BLOOM_STRONGHOLD_LIBRARY_CHEST_CHANCE = BUILDER
                .comment("Chance for Ephemeral Bloom to be added to stronghold library chest loot.")
                .defineInRange("strongholdLibraryChestChance", 0.52D, 0.0D, 1.0D);

        EPHEMERAL_BLOOM_ABSORPTION_INTERVAL_TICKS = BUILDER
                .comment("Interval of absorption grants from placed Ephemeral Blooms and Pendants. 120 ticks = 6 seconds.")
                .defineInRange("absorptionIntervalTicks", 120, 1, Integer.MAX_VALUE);

        EPHEMERAL_BLOOM_MAX_ABSORPTION = BUILDER
                .comment("Maximum absorption from Ephemeral Blooms and Pendants.")
                .defineInRange("maxAbsorption", 20, 0, 1024);

        BUILDER.pop();

        BUILDER.push("warrior_belt");

        WARRIOR_BELT_KNOCKBACK_RESISTANCE = BUILDER
                .comment("Knockback resistance bonus. 0.10 = +10%")
                .defineInRange("knockbackResistance", 0.10D, 0.0D, 1.0D);

        WARRIOR_BELT_MOVEMENT_SPEED_BONUS = BUILDER
                .comment("Movement speed bonus. 0.10 = +10%.")
                .defineInRange("movementSpeedBonus", 0.10D, 0.0D, 10.0D);

        WARRIOR_BELT_CRITICAL_DAMAGE_BONUS = BUILDER
                .comment("Vanilla critical hit bonus. 0.08 = 1.50 -> 1.58.")
                .defineInRange("criticalDamageBonus", 0.08D, 0.0D, 10.0D);

        BUILDER.pop();

        BUILDER.push("brute_belt");

        BRUTE_BELT_MAX_HEALTH_BONUS = BUILDER
                .comment("Maximum health bonus. 0.20 = +20%.")
                .defineInRange("maxHealthBonus", 0.20D, 0.0D, 10.0D);

        BRUTE_BELT_PIGLIN_BRUTE_DROP_CHANCE = BUILDER
                .comment("Base chance for Piglin Brutes to drop Belt of Brute. Wearing Brutal Plunder Badge adds 0.15.")
                .defineInRange("piglinBruteDropChance", 0.024D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("verdant_scale_belt");

        VERDANT_SCALE_BELT_ARMOR_TOUGHNESS = BUILDER
                .comment("Armor toughness bonus.")
                .defineInRange("armorToughness", 2.0D, 0.0D, 1024.0D);

        BUILDER.pop();

        BUILDER.push("night_gloves");

        NIGHT_GLOVES_NIGHT_ATTACK_DAMAGE_BONUS = BUILDER
                .comment("Melee attack damage bonus while Night Gloves are equipped at night. 0.20 = 20%.")
                .defineInRange("nightAttackDamageBonus", 0.20D, 0.0D, 10.0D);

        BUILDER.pop();

        BUILDER.push("treasure_hunters_gloves");

        TREASURE_HUNTERS_GLOVES_LUCK_PER_CHEST = BUILDER
                .comment("Luck gained for each first-opened loot container. 0.05 = +0.05 Luck.")
                .defineInRange("luckPerChest", 0.05D, 0.0D, 1024.0D);

        TREASURE_HUNTERS_GLOVES_MAX_ITEMS_TAKEN_BEFORE_REFRESH = BUILDER
                .comment("Max items that can be removed before refresh.")
                .defineInRange("maxItemsTakenBeforeRefresh", 2, 0, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("thousandweight_gauntlets");

        THOUSANDWEIGHT_GAUNTLETS_ATTACK_SPEED_REDUCTION = BUILDER
                .comment("Attack speed reduction. 0.60 = -60%.")
                .defineInRange("attackSpeedReduction", 0.60D, 0.0D, 0.90D);

        THOUSANDWEIGHT_GAUNTLETS_WIND_BURST_LAUNCH_HEIGHT = BUILDER
                .comment("Wind burst launch height in blocks.")
                .defineInRange("windBurstLaunchHeight", 12.0D, 0.0D, 64.0D);

        BUILDER.pop();

        BUILDER.push("astral_storybook");

        ASTRAL_STORYBOOK_ADVANCED_CHEST_CHANCE = BUILDER
                .comment("Chance for Astral Storybook to be added to certain chest. 0.016 = 1.6%.")
                .defineInRange("advancedChestChance", 0.016D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("warfire_fragment");

        WARFIRE_FRAGMENT_IRON_GOLEM_DROP_CHANCE = BUILDER
                .comment("Chance for Warfire Fragment to drop when an iron golem is killed during a raid by a raider, Vex, or eligible medal wearer. 0.42 = 42%.")
                .defineInRange("ironGolemDropChance", 0.42D, 0.0D, 1.0D);

        WARFIRE_FRAGMENT_ALLAY_AURA_RANGE = BUILDER
                .comment("Range in blocks for Allays holding a Warfire Fragment to grant Strength II and Resistance I.")
                .defineInRange("allayAuraRange", 24.0D, 0.0D, 128.0D);

        BUILDER.pop();

        BUILDER.push("rotten_tusk");

        ROTTEN_TUSK_PIGLIN_REPEL_RANGE = BUILDER
                .comment("Range in blocks for players holding a Rotten Tusk to repel ordinary Piglins.")
                .defineInRange("piglinRepelRange", 12.0D, 0.0D, 64.0D);

        BUILDER.pop();

        BUILDER.push("feysilver_ingot");

        FEYSILVER_INGOT_OVERWORLD_CHEST_CHANCE = BUILDER
                .comment("Chance for Feysilver Ingot to be added to selected overworld chest loot. 0.067 = 6.7%.")
                .defineInRange("overworldChestChance", 0.067D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("feysilver_forging_art_volume_one");

        FEYSILVER_FORGING_ART_VOLUME_ONE_ITEM_BLACKLIST = BUILDER
                .comment("Blacklist of feysilver coating.")
                .defineList("itemBlacklist", List.of("twilightforest:glass_sword"), ModCommonConfig::isValidResourceLocation);

        BUILDER.pop();

        BUILDER.push("master_smiths_brooch");

        MASTER_SMITHS_BROOCH_ARMOR_TOUGHNESS_PER_TRIMMED_ARMOR = BUILDER
                .comment("Armor toughness added per trimmed armor.")
                .defineInRange("armorToughnessPerTrimmedArmor", 2.0D, 0.0D, 20.0D);

        MASTER_SMITHS_BROOCH_DAMAGE_REDUCTION_PER_COATED_ARMOR = BUILDER
                .comment("Damage reduction per armor with feysilver coating. 0.06 = -6%.")
                .defineInRange("damageReductionPerCoatedArmor", 0.06D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("two_handed_sword_brooch");

        TWO_HANDED_SWORD_BROOCH_SWORD_DAMAGE_BONUS = BUILDER
                .comment("Attack damage bonus while holding #minecraft:swords. 0.40 = +40%.")
                .defineInRange("swordDamageBonus", 0.40D, 0.0D, 10.0D);

        TWO_HANDED_SWORD_BROOCH_ATTACK_SPEED_PENALTY = BUILDER
                .comment("Attack speed penalty while holding #minecraft:swords. 0.20 = -20%.")
                .defineInRange("attackSpeedPenalty", 0.20D, 0.0D, 1.0D);

        TWO_HANDED_SWORD_BROOCH_ENTITY_REACH_BONUS = BUILDER
                .comment("Entity reach bonus.")
                .defineInRange("entityReachBonus", 1.0D, 0.0D, 64.0D);

        BUILDER.pop();

        BUILDER.push("vindicators_medal");

        VINDICATORS_MEDAL_AXE_ATTACK_SPEED_BONUS = BUILDER
                .comment("Attack speed bonus while holding #minecraft:axes. 0.20 = +20%.")
                .defineInRange("axeAttackSpeedBonus", 0.20D, 0.0D, 10.0D);

        VINDICATORS_MEDAL_VINDICATOR_DROP_CHANCE = BUILDER
                .comment("Chance for Vindicator's Medal to be added to Vindicator loot. 0.031 = 3.1%.")
                .defineInRange("vindicatorDropChance", 0.031D, 0.0D, 1.0D);

        VINDICATORS_MEDAL_STRUCTURE_CHEST_CHANCE = BUILDER
                .comment("Chance for Vindicator's Medal to be added to Pillager Outpost or Woodland Mansion chest. 0.16 = 16%.")
                .defineInRange("structureChestChance", 0.16D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("turncoats_medal");

        TURNCOATS_MEDAL_HERO_DAMAGE_BONUS_PER_LEVEL = BUILDER
                .comment("Damage bonus per level of Hero of the Village. 0.25 = +25% per level.")
                .defineInRange("heroDamageBonusPerLevel", 0.25D, 0.0D, 10.0D);

        TURNCOATS_MEDAL_BAD_OMEN_LIFE_STEAL_PER_LEVEL = BUILDER
                .comment("Life steal per level of Bad Omen. 0.15 = 15% per level.")
                .defineInRange("badOmenLifeStealPerLevel", 0.15D, 0.0D, 10.0D);

        TURNCOATS_MEDAL_BANNER_EFFECT_EXTENSION_TICKS = BUILDER
                .comment("Ticks added to Hero of the Village and Bad Omen when consuming an Ominous Banner. 12000 ticks = 600 seconds.")
                .defineInRange("bannerEffectExtensionTicks", 12000, 0, Integer.MAX_VALUE);

        TURNCOATS_MEDAL_PATROL_ATTEMPT_INTERVAL_TICKS = BUILDER
                .comment("Base interval in ticks between extra patrol spawn attempts for each Turncoat's Medal wearer. 18000 ticks = 15 minutes. Set to 0 to disable extra patrols.")
                .defineInRange("patrolAttemptIntervalTicks", 18000, 0, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("stormscale");

        STORMSCALE_ELDER_GUARDIAN_THUNDERSTORM_DROP_CHANCE = BUILDER
                .comment("Chance for Stormscale to drop when an Elder Guardian dies during a thunderstorm. 0.28 = 28%.")
                .defineInRange("elderGuardianThunderstormDropChance", 0.28D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("rippleheart_pearl");

        RIPPLEHEART_PEARL_AXOLOTL_ASSIST_CHANCE = BUILDER
                .comment("Chance for Rippleheart Pearl to drop when an Axolotl grants its assist Regeneration to a player. 0.06 = 6%.")
                .defineInRange("axolotlAssistChance", 0.06D, 0.0D, 1.0D);

        RIPPLEHEART_PEARL_WARM_OCEAN_RUIN_ARCHAEOLOGY_CHANCE = BUILDER
                .comment("Chance for Rippleheart Pearl to be added to warm ocean ruin archaeology loot. 0.15 = 15%.")
                .defineInRange("warmOceanRuinArchaeologyChance", 0.15D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("pastoral_melody");

        PASTORAL_MELODY_ANIMAL_RANGE = BUILDER
                .comment("Range in blocks for Pastoral Melody to put nearby adult animals into love mode.")
                .defineInRange("animalRange", 36.0D, 0.0D, 128.0D);

        BUILDER.pop();

        BUILDER.push("extinguished_solar_furnace");

        EXTINGUISHED_SOLAR_FURNACE_BASTION_TREASURE_CHEST_CHANCE = BUILDER
                .comment("Chance for Extinguished Solar Furnace to be added to bastion treasure chest loot. 0.37 = 37%.")
                .defineInRange("bastionTreasureChestChance", 0.37D, 0.0D, 1.0D);

        EXTINGUISHED_SOLAR_FURNACE_NETHER_FORTRESS_CHEST_CHANCE = BUILDER
                .comment("Chance for Extinguished Solar Furnace to be added to nether fortress chest loot. 0.055 = 5.5%.")
                .defineInRange("netherFortressChestChance", 0.055D, 0.0D, 1.0D);

        EXTINGUISHED_SOLAR_FURNACE_WEAPONSMITH_CHEST_CHANCE = BUILDER
                .comment("Chance for Extinguished Solar Furnace to be added to village weaponsmith chest loot. 0.031 = 3.1%.")
                .defineInRange("weaponsmithChestChance", 0.031D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("withered_life_chalice");

        WITHERED_LIFE_CHALICE_WOODLAND_MANSION_CHEST_CHANCE = BUILDER
                .comment("Chance for Withered Life Chalice to be added to each woodland mansion chest. 10.8%.")
                .defineInRange("woodlandMansionChestChance", 0.108D, 0.0D, 1.0D);

        WITHERED_LIFE_CHALICE_DESERT_PYRAMID_CHEST_CHANCE = BUILDER
                .comment("Chance for Withered Life Chalice to be added to each desert pyramid chest. 4.2%")
                .defineInRange("desertPyramidChestChance", 0.042D, 0.0D, 1.0D);

        WITHERED_LIFE_CHALICE_JUNGLE_TEMPLE_CHEST_CHANCE = BUILDER
                .comment("Chance for Withered Life Chalice to be added to each jungle temple chest. 10%")
                .defineInRange("jungleTempleChestChance", 0.10D, 0.0D, 1.0D);

        WITHERED_LIFE_CHALICE_SIMPLE_DUNGEON_CHEST_CHANCE = BUILDER
                .comment("Chance for Withered Life Chalice to be added to each dungeon chest. 1.6%")
                .defineInRange("simpleDungeonChestChance", 0.016D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("dried_crown");

        DRIED_CROWN_SHIPWRECK_TREASURE_CHEST_CHANCE = BUILDER
                .comment("Chance for Dried Crown to be added to shipwreck treasure chest loot. 0.05 = 5%.")
                .defineInRange("shipwreckTreasureChestChance", 0.05D, 0.0D, 1.0D);

        DRIED_CROWN_UNDERWATER_RUIN_CHEST_CHANCE = BUILDER
                .comment("Chance for Dried Crown to be added to big and small underwater ruin chest loot. 0.04 = 4%.")
                .defineInRange("underwaterRuinChestChance", 0.04D, 0.0D, 1.0D);

        DRIED_CROWN_BURIED_TREASURE_CHEST_CHANCE = BUILDER
                .comment("Chance for Dried Crown to be added to buried treasure chest loot. 0.08 = 8%.")
                .defineInRange("buriedTreasureChestChance", 0.08D, 0.0D, 1.0D);

        DRIED_CROWN_OCEAN_RUIN_ARCHAEOLOGY_CHANCE = BUILDER
                .comment("Chance for Dried Crown to be added to cold and warm ocean ruin archaeology loot. 0.024 = 2.4%.")
                .defineInRange("oceanRuinArchaeologyChance", 0.024D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("weathered_mountain_covenant");

        WEATHERED_MOUNTAIN_COVENANT_TRAIL_RUINS_ARCHAEOLOGY_CHANCE = BUILDER
                .comment("Chance per suspicious gravel loot in Trail Ruins. 0.039 = 3.9%.")
                .defineInRange("trailRuinsArchaeologyChance", 0.039D, 0.0D, 1.0D);

        WEATHERED_MOUNTAIN_COVENANT_PILLAGER_OUTPOST_CHEST_CHANCE = BUILDER
                .comment("Chance for Weathered Mountain Covenant to be added to Pillager Outpost chest. 0.032 = 3.2%.")
                .defineInRange("pillagerOutpostChestChance", 0.032D, 0.0D, 1.0D);

        WEATHERED_MOUNTAIN_COVENANT_MINESHAFT_DUNGEON_CHEST_CHANCE = BUILDER
                .comment("Chance for Weathered Mountain Covenant to be added to mineshaft minecart or dungeon chest. 0.019 = 1.9%.")
                .defineInRange("mineshaftDungeonChestChance", 0.019D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("divine_severance_ring");

        DIVINE_SEVERANCE_RING_HEAD_DROPS = BUILDER
                .comment("Head drops for Ring of Judgment when the wearer kills a glowing configured undead entity. Format: entity_id|item_id")
                .defineList("headDrops", List.of(
                        "minecraft:zombie|minecraft:zombie_head",
                        "minecraft:zombie_villager|minecraft:zombie_head",
                        "minecraft:drowned|minecraft:zombie_head",
                        "minecraft:husk|minecraft:zombie_head",
                        "minecraft:skeleton|minecraft:skeleton_skull",
                        "minecraft:stray|minecraft:skeleton_skull",
                        "minecraft:wither_skeleton|minecraft:wither_skeleton_skull"
                ), ModCommonConfig::isValidHeadDropEntry);

        BUILDER.pop();

        BUILDER.push("ashen_touch");

        ASHEN_TOUCH_BURNING_TARGET_DAMAGE_BONUS = BUILDER
                .comment("Damage bonus for melee attacks against targets that were already burning before being hit while Ashen Touch is equipped. 4.0 = +400%.")
                .defineInRange("burningTargetDamageBonus", 4.0D, 0.0D, 100.0D);

        BUILDER.pop();

        BUILDER.push("rippleheart_rings");

        RIPPLEHEART_RINGS_ACTIVATION_RANGE = BUILDER
                .comment("Maximum distance in blocks for a matching pair of Rippleheart Rings to remain active.")
                .defineInRange("activationRange", 32.0D, 0.0D, 256.0D);

        RIPPLEHEART_RINGS_COMBAT_EFFECT_DURATION_TICKS = BUILDER
                .comment("Duration in ticks of Resistance/Strength granted. 160 ticks = 8 seconds.")
                .defineInRange("combatEffectDurationTicks", 8 * 20, 1, 20 * 60 * 60);

        RIPPLEHEART_RINGS_RED_DAMAGE_BONUS = BUILDER
                .comment("Damage bonus of Red Ring. 0.20 = +20%.")
                .defineInRange("redDamageBonus", 0.20D, 0.0D, 100.0D);

        RIPPLEHEART_RINGS_RED_ATTACK_SPEED_BONUS = BUILDER
                .comment("Attack speed bonus of Red Ring. 0.20 = +20%.")
                .defineInRange("redAttackSpeedBonus", 0.20D, 0.0D, 100.0D);

        RIPPLEHEART_RINGS_BLUE_DAMAGE_REDUCTION = BUILDER
                .comment("Damage reduction of Blue Ring. 0.20 = -20%.")
                .defineInRange("blueDamageReduction", 0.20D, 0.0D, 1.0D);

        RIPPLEHEART_RINGS_BLUE_HEALING_BONUS = BUILDER
                .comment("Healing bonus of Blue Ring. 0.20 = +20%.")
                .defineInRange("blueHealingBonus", 0.20D, 0.0D, 100.0D);

        BUILDER.pop();

        BUILDER.push("immortal_soul_ring");

        IMMORTAL_SOUL_RING_MAGIC_DAMAGE_RATIO = BUILDER
                .comment("Additional indirect magic damage. 0.20 = 20%.")
                .defineInRange("magicDamageRatio", 0.20D, 0.0D, 10.0D);
        IMMORTAL_SOUL_RING_KILL_HEAL_RATIO = BUILDER
                .comment("Fraction of maximum health restored when killing a mob.")
                .defineInRange("killHealRatio", 0.10D, 0.0D, 1.0D);
        IMMORTAL_SOUL_RING_COMBO_THRESHOLD = BUILDER
                .comment("Melee combo points needed for one shield.")
                .defineInRange("comboThreshold", 300.0D, 1.0D, 1000000.0D);
        IMMORTAL_SOUL_RING_COMBO_TIMEOUT_TICKS = BUILDER
                .comment("Maximum gap between melee hits before combo points reset. 60 ticks = 3 seconds.")
                .defineInRange("comboTimeoutTicks", 60, 1, 72000);
        IMMORTAL_SOUL_RING_MAX_SHIELDS = BUILDER
                .comment("Maximum shields gain from combo.")
                .defineInRange("maxShields", 3, 0, 64);

        BUILDER.pop();

        BUILDER.push("little_tailors_belt");

        LITTLE_TAILORS_BELT_REACTION_RANGE = BUILDER
                .comment("Range in blocks in which humanoids afraid the wearer, or witness a failed one-hit.")
                .defineInRange("reactionRange", 16.0D, 0.0D, 128.0D);

        LITTLE_TAILORS_BELT_INSTANT_KILL_ENTITY_TYPES = BUILDER
                .comment("Entity types auto killed within 4 blocks.")
                .defineList("instantKillEntityTypes", List.of(
                        "alexsmobs:fly",
                        "alexsmobs:crimson_mosquito"
                ), ModCommonConfig::isValidResourceLocation);

        BUILDER.pop();

        BUILDER.push("rabbits_pocket_watch");

        RABBITS_POCKET_WATCH_COOLDOWN_TICKS = BUILDER
                .comment("Cooldown in ticks after using Rabbit's Pocket Watch, 400 ticks = 20 seconds.")
                .defineInRange("cooldownTicks", 20 * 20, 0, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("strider_spurs");

        STRIDER_SPURS_SPEED_BONUS = BUILDER
                .comment("Movement speed bonus for ridden Striders while the rider has Strider Spurs equipped and holds a Warped Fungus on a Stick. 1.5 = +150%.")
                .defineInRange("speedBonus", 1.5D, 0.0D, 10.0D);

        BUILDER.pop();

        BUILDER.push("brutal_plunder_badge");

        BRUTAL_PLUNDER_BADGE_LOOTING_BONUS = BUILDER
                .comment("Looting level bonus while Brutal Plunder Badge is equipped.")
                .defineInRange("lootingBonus", 2, 0, 32);

        BRUTAL_PLUNDER_BADGE_DAMAGE_BONUS_PER_LOOTING_LEVEL = BUILDER
                .comment("Damage bonus per effective Looting level while Brutal Plunder Badge is equipped. 0.06D = 6%.")
                .defineInRange("damageBonusPerLootingLevel", 0.06D, 0.0D, 10.0D);

        BRUTAL_PLUNDER_BADGE_MAX_DAMAGE_BONUS = BUILDER
                .comment("Maximum total damage bonus from Brutal Plunder Badge. 6.0 = +600%, for up to 7x total damage.")
                .defineInRange("maxDamageBonus", 6.0D, 0.0D, 100.0D);

        BUILDER.pop();

        BUILDER.push("warp_crystal");

        WARP_CRYSTAL_DODGE_CHANCE = BUILDER
                .comment("Chance to dodge an eligible attack while Warp Crystal is equipped. 0.08 = 8%.")
                .defineInRange("dodgeChance", 0.08D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("covenant_tablet");

        COVENANT_TABLET_DAMAGE_TASK_THRESHOLD = BUILDER
                .comment("Damage threshold for first task.")
                .defineInRange("damageTaskThreshold", 100.0D, 0.0D, 1000000.0D);

        COVENANT_TABLET_MINING_SPEED_BONUS = BUILDER
                .comment("Mining speed bonus for second task. 0.20 = +20%.")
                .defineInRange("miningSpeedBonus", 0.20D, 0.0D, 10.0D);

        COVENANT_TABLET_VILLAGER_TRADE_DISCOUNT = BUILDER
                .comment("Villager discount for third task. 0.15 = -15%.")
                .defineInRange("villagerTradeDiscount", 0.15D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("granbells_furnace");

        GRANBELLS_FURNACE_DAMAGE_BONUS = BUILDER
                .comment("Attack damage attribute bonus while Granbell's Furnace is equipped.")
                .defineInRange("damageBonus", 4.0D, 0.0D, 1024.0D);

        GRANBELLS_FURNACE_PRESERVE_SMITHING_TEMPLATES = BUILDER
                .comment("If true, smithing templates are restored when players wearing Granbell's Furnace take a smithing table result.")
                .define("preserveSmithingTemplates", true);

        GRANBELLS_FURNACE_KEEP_INVENTORY_IN_FIRE_OR_LAVA = BUILDER
                .comment("If true, players wearing Granbell's Furnace keep their inventory and curios when they die while in fire or lava.")
                .define("keepInventoryInFireOrLava", true);

        BUILDER.pop();

        BUILDER.push("iluthias_chalice");

        ILUTHIAS_CHALICE_UNDEAD_DAMAGE_BONUS = BUILDER
                .comment("Damage bonus dealt to undead creatures while Iluthia's Chalice is equipped. 0.30 = +30%.")
                .defineInRange("undeadDamageBonus", 0.30D, 0.0D, 10.0D);

        ILUTHIAS_CHALICE_UNDEAD_DAMAGE_REDUCTION = BUILDER
                .comment("Damage reduction against damage from undead creatures while Iluthia's Chalice is equipped. 0.30 = -30%.")
                .defineInRange("undeadDamageReduction", 0.30D, 0.0D, 1.0D);

        ILUTHIAS_CHALICE_ENHANCED_TOTEM_ILUTHIAS_BLESSING_DURATION = BUILDER
                .comment("Duration in ticks for Iluthia's Blessing granted by Totems of Undying. 100 ticks = 5 seconds.")
                .defineInRange("enhancedTotemIluthiasBlessingDuration", 5 * 20, 1, 20 * 60 * 60);

        BUILDER.pop();

        BUILDER.push("nereias_crown");

        NEREIAS_CROWN_ACTIVE_DAMAGE_REDUCTION = BUILDER
                .comment("Damage reduction while Nereia's Crown is active in water or exposed rain. 0.40 = -40%.")
                .defineInRange("activeDamageReduction", 0.40D, 0.0D, 1.0D);

        NEREIAS_CROWN_AQUATIC_AURA_RANGE = BUILDER
                .comment("Range in blocks for Nereia's Crown to count nearby aquatic creatures for its attribute aura.")
                .defineInRange("aquaticAuraRange", 24.0D, 0.0D, 128.0D);

        NEREIAS_CROWN_AQUATIC_ATTACK_DAMAGE_SHARE = BUILDER
                .comment("Share of nearby aquatic creatures' total attack damage added to the wearer. 0.30 = 30%.")
                .defineInRange("aquaticAttackDamageShare", 0.30D, 0.0D, 10.0D);

        NEREIAS_CROWN_AQUATIC_MAX_HEALTH_ARMOR_SHARE = BUILDER
                .comment("Share of nearby aquatic creatures' total max health added as armor to the wearer. 0.10 = 10%.")
                .defineInRange("aquaticMaxHealthArmorShare", 0.10D, 0.0D, 10.0D);

        BUILDER.pop();

        BUILDER.push("baroms_covenant_stone");
        BAROM_VOLUME_MULTIPLIER = BUILDER
                .comment("Volume multiplier for silverfish covenant, if mob volume < volumeMultiplier * wearer volume, the damage is ignored.")
                .defineInRange("volumeMultiplier", 1.0D, 0.0D, 1024.0D);
        BAROM_HUSK_HUNGER_CHANCE = BUILDER
                .comment("Chance to gain 1 hunger after melee attack with the Husk covenant. 0.15 = 15%.")
                .defineInRange("huskHungerChance", 0.15D, 0.0D, 1.0D);
        BAROM_WITCH_POTION_PRESERVATION_CHANCE = BUILDER
                .comment("Chance to preserve a potion with the Witch covenant. 0.50 = 50%.")
                .defineInRange("witchPotionPreservationChance", 0.50D, 0.0D, 1.0D);
        BUILDER.pop();

        BUILDER.push("dreamcatcher_box");

        DREAMCATCHER_BOX_SLEEP_RANGE = BUILDER
                .comment("Range in blocks for Dreamcatcher Boxes to react to nearby sleeping players and villagers.")
                .defineInRange("sleepRange", 8.0D, 0.0D, 64.0D);

        BUILDER.pop();

        BUILDER.push("tempest_sprint");

        TEMPEST_SPRINT_SPEED_BONUS = BUILDER
                .comment("Movement speed bonus for vanilla Horses under Tempest Sprint. 0.40 = +40%.")
                .defineInRange("speedBonus", 0.40D, 0.0D, 10.0D);

        TEMPEST_SPRINT_DAMAGE_REDUCTION = BUILDER
                .comment("Damage reduction for vanilla Horses under Tempest Sprint. 0.60 = -60%.")
                .defineInRange("damageReduction", 0.60D, 0.0D, 1.0D);

        TEMPEST_SPRINT_COLLISION_KNOCKBACK = BUILDER
                .comment("Base knockback to hostile mobs when colliding.")
                .defineInRange("collisionKnockback", 2.0D, 0.0D, 64.0D);

        TEMPEST_SPRINT_COLLISION_DAMAGE = BUILDER
                .comment("Base damage to hostile mobs when colliding.")
                .defineInRange("collisionDamage", 8.0D, 0.0D, 1024.0D);

        TEMPEST_SPRINT_FULL_JUMP_SHOCKWAVE_RANGE = BUILDER
                .comment("Range of full jump shockwave.")
                .defineInRange("fullJumpShockwaveRange", 12.0D, 0.0D, 64.0D);

        TEMPEST_SPRINT_FULL_JUMP_SHOCKWAVE_DAMAGE = BUILDER
                .comment("Damage applied when full jump under Tempest Sprint.")
                .defineInRange("fullJumpShockwaveDamage", 18.0D, 0.0D, 1024.0D);

        TEMPEST_SPRINT_FULL_JUMP_SHOCKWAVE_KNOCKBACK = BUILDER
                .comment("Knockback applied when full jump under Tempest Sprint.")
                .defineInRange("fullJumpShockwaveKnockback", 2.5D, 0.0D, 64.0D);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private ModCommonConfig() {
    }

    private static boolean isValidResourceLocation(Object value) {
        return value instanceof String string && ResourceLocation.isValidResourceLocation(string);
    }

    private static boolean isValidHeadDropEntry(Object value) {
        if (!(value instanceof String string)) {
            return false;
        }

        String[] parts = string.split("\\|");
        return parts.length == 2
                && ResourceLocation.isValidResourceLocation(parts[0].trim())
                && ResourceLocation.isValidResourceLocation(parts[1].trim());
    }
}
