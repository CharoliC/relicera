package com.yukari.relicera.registry;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.compat.twilightforest.DepthbreakerItem;
import com.yukari.relicera.common.item.BaromsCovenantStoneItem;
import com.yukari.relicera.common.item.AstralLensItem;
import com.yukari.relicera.common.item.AstralStorybookItem;
import com.yukari.relicera.common.item.BrutalPlunderBadgeItem;
import com.yukari.relicera.common.item.CovenantTabletItem;
import com.yukari.relicera.common.item.armor.devilsbearskin.DevilsBearskinItem;
import com.yukari.relicera.common.item.EphemeralBloomItem;
import com.yukari.relicera.common.item.EphemeralBloomPendantItem;
import com.yukari.relicera.common.item.EnvironmentallyIndestructibleItem;
import com.yukari.relicera.common.item.feysilver.FeysilverForgingArtVolumeOneItem;
import com.yukari.relicera.common.item.ForgelingBucketItem;
import com.yukari.relicera.common.item.FourfoldSherdPendantItem;
import com.yukari.relicera.common.item.IluthiasChaliceItem;
import com.yukari.relicera.common.item.InscribedCovenantTabletItem;
import com.yukari.relicera.common.item.LeatherGlovesItem;
import com.yukari.relicera.common.item.LittleTailorsBeltItem;
import com.yukari.relicera.common.item.LuminasCelestialLensItem;
import com.yukari.relicera.common.item.PastoralMelodyItem;
import com.yukari.relicera.common.item.QuickEquipCurioItem;
import com.yukari.relicera.common.item.RabbitsPocketWatchItem;
import com.yukari.relicera.common.item.RelicCurioItem;
import com.yukari.relicera.common.item.RevivalNectarItem;
import com.yukari.relicera.common.item.RippleheartRingItem;
import com.yukari.relicera.common.item.RippleheartRingsItem;
import com.yukari.relicera.common.item.SculkFruitItem;
import com.yukari.relicera.common.item.SculkFruitSeedsItem;
import com.yukari.relicera.common.item.SolarEmberItem;
import com.yukari.relicera.common.item.StonewallGreatshieldItem;
import com.yukari.relicera.common.item.TempestsReinsItem;
import com.yukari.relicera.common.item.ThousandweightGauntletsItem;
import com.yukari.relicera.common.item.TorchflowerEmberItem;
import com.yukari.relicera.common.item.TurncoatsMedalItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ReliceraMod.MOD_ID);

    public static final RegistryObject<Item> FEYSILVER_INGOT = ITEMS.register("feysilver_ingot", () ->
            new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COBBLESTONE_TEMPLATE_BASE = ITEMS.register("cobblestone_template_base", () ->
            new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> TERRACOTTA_TEMPLATE_BASE = ITEMS.register("terracotta_template_base", () ->
            new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> FEYSILVER_FORGING_ART_VOLUME_ONE = ITEMS.register("feysilver_forging_art_volume_one", () ->
            new FeysilverForgingArtVolumeOneItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> FORGOTTEN_THREAD = ITEMS.register("forgotten_thread", () ->
            new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> SCULK_FRUIT_SEEDS = ITEMS.register("sculk_fruit_seeds", () ->
            new SculkFruitSeedsItem(ModBlocks.SCULK_FRUIT_CROP.get(), new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> SCULK_FRUIT = ITEMS.register("sculk_fruit", () ->
            new SculkFruitItem(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)
                    .food(SculkFruitItem.createFoodProperties())));

    public static final RegistryObject<Item> SILK_OF_NIGHT = ITEMS.register("silk_of_night", () ->
            new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> ROTTEN_TUSK = ITEMS.register("rotten_tusk", () ->
            new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> RAW_HALLOWED_ENAMEL = ITEMS.register("raw_hallowed_enamel", () ->
            new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> HALLOWED_ENAMEL = ITEMS.register("hallowed_enamel", () ->
            new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> PRIMAL_SOIL = ITEMS.register("primal_soil", () ->
            new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> PASTORAL_MELODY = ITEMS.register("pastoral_melody", () ->
            new PastoralMelodyItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> STORMSCALE = ITEMS.register("stormscale", () ->
            new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> TEMPESTS_REINS = ITEMS.register("tempests_reins", () ->
            new TempestsReinsItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> RIPPLEHEART_PEARL = ITEMS.register("rippleheart_pearl", () ->
            new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> CHALICE_LINING = ITEMS.register("chalice_lining", () ->
            new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> TORCHFLOWER_EMBER = ITEMS.register("torchflower_ember", () ->
            new TorchflowerEmberItem(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> SOLAR_EMBER = ITEMS.register("solar_ember", () ->
            new SolarEmberItem(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> EPHEMERAL_BLOOM = ITEMS.register("ephemeral_bloom", () ->
            new EphemeralBloomItem(ModBlocks.EPHEMERAL_BLOOM.get(), new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> REVIVAL_NECTAR = ITEMS.register("revival_nectar", () ->
            new RevivalNectarItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .food(RevivalNectarItem.createFoodProperties())));

    public static final RegistryObject<Item> WARFIRE_FRAGMENT = ITEMS.register("warfire_fragment", () ->
            new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)
                    .fireResistant()));

    public static final RegistryObject<Item> ASTRAL_LENS = ITEMS.register("astral_lens", () ->
            new AstralLensItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final RegistryObject<Item> ASTRAL_STORYBOOK = ITEMS.register("astral_storybook", () ->
            new AstralStorybookItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> HEART_OF_THE_MOUNTAIN = ITEMS.register("heart_of_the_mountain", () ->
            new EnvironmentallyIndestructibleItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)
                    .fireResistant()));

    public static final RegistryObject<Item> STONEWALL_GREATSHIELD = ITEMS.register("stonewall_greatshield", () ->
            new StonewallGreatshieldItem(new Item.Properties()
                    .durability(StonewallGreatshieldItem.MAX_DURABILITY)
                    .rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> DEPTHBREAKER = ModList.get().isLoaded("twilightforest")
            ? ITEMS.register("depthbreaker", DepthbreakerItem::create)
            : RegistryObject.createOptional(ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "depthbreaker"),
                    ForgeRegistries.Keys.ITEMS, ReliceraMod.MOD_ID);

    public static final RegistryObject<Item> LITTLE_TAILORS_BELT = ITEMS.register("little_tailors_belt", () ->
            new LittleTailorsBeltItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> WARRIOR_BELT = ITEMS.register("warrior_belt", () ->
            new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> BRUTE_BELT = ITEMS.register("brute_belt", () ->
            new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> VERDANT_SCALE_BELT = ModList.get().isLoaded("twilightforest")
            ? ITEMS.register("verdant_scale_belt", () -> new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)))
            : RegistryObject.createOptional(ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "verdant_scale_belt"),
                    ForgeRegistries.Keys.ITEMS, ReliceraMod.MOD_ID);

    public static final RegistryObject<Item> DEVILS_BEARSKIN = ITEMS.register("devils_bearskin", () ->
            new DevilsBearskinItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .setNoRepair()));

    public static final RegistryObject<Item> RABBITS_POCKET_WATCH = ITEMS.register("rabbits_pocket_watch", () ->
            new RabbitsPocketWatchItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> EPHEMERAL_BLOOM_PENDANT = ITEMS.register("ephemeral_bloom_pendant", () ->
            new EphemeralBloomPendantItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> FOURFOLD_SHERD_PENDANT = ITEMS.register("fourfold_sherd_pendant", () ->
            new FourfoldSherdPendantItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> LEATHER_GLOVES = ITEMS.register("leather_gloves", () ->
            new LeatherGlovesItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> NIGHT_GLOVES = ITEMS.register("night_gloves", () ->
            new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> TREASURE_HUNTERS_GLOVES = ITEMS.register("treasure_hunters_gloves", () ->
            new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> THOUSANDWEIGHT_GAUNTLETS = ITEMS.register("thousandweight_gauntlets", () ->
            new ThousandweightGauntletsItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .setNoRepair()));

    public static final RegistryObject<Item> DIVINE_SEVERANCE_RING = ITEMS.register("divine_severance_ring", () ->
            new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> RING_OF_SATIETY = ITEMS.register("ring_of_satiety", () ->
            new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> ASHEN_TOUCH = ITEMS.register("ashen_touch", () ->
            new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> RIPPLEHEART_RINGS = ITEMS.register("rippleheart_rings", () ->
            new RippleheartRingsItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> IMMORTAL_SOUL_RING = ModList.get().isLoaded("twilightforest")
            ? ITEMS.register("immortal_soul_ring", () -> new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)))
            : RegistryObject.createOptional(ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "immortal_soul_ring"),
                    ForgeRegistries.Keys.ITEMS, ReliceraMod.MOD_ID);

    public static final RegistryObject<RippleheartRingItem> RIPPLEHEART_RING_RED = ITEMS.register("rippleheart_ring_red", () ->
            new RippleheartRingItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC), RippleheartRingItem.RingSide.RED));

    public static final RegistryObject<RippleheartRingItem> RIPPLEHEART_RING_BLUE = ITEMS.register("rippleheart_ring_blue", () ->
            new RippleheartRingItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC), RippleheartRingItem.RingSide.BLUE));

    public static final RegistryObject<Item> STRIDER_SPURS = ITEMS.register("strider_spurs", () ->
            new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> COVENANT_TABLET = ITEMS.register("covenant_tablet", () ->
            new CovenantTabletItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> INSCRIBED_COVENANT_TABLET = ITEMS.register("inscribed_covenant_tablet", () ->
            new InscribedCovenantTabletItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> WARP_CRYSTAL = ITEMS.register("warp_crystal", () ->
            new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> BRUTAL_PLUNDER_BADGE = ITEMS.register("brutal_plunder_badge", () ->
            new BrutalPlunderBadgeItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> MASTER_SMITHS_BROOCH = ITEMS.register("master_smiths_brooch", () ->
            new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> TWO_HANDED_SWORD_BROOCH = ITEMS.register("two_handed_sword_brooch", () ->
            new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> VINDICATORS_MEDAL = ITEMS.register("vindicators_medal", () ->
            new QuickEquipCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> TURNCOATS_MEDAL = ITEMS.register("turncoats_medal", () ->
            new TurncoatsMedalItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> EXTINGUISHED_SOLAR_FURNACE = ITEMS.register("extinguished_solar_furnace", () ->
            new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> GRANBELLS_FURNACE = ITEMS.register("granbells_furnace", () ->
            new RelicCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(ReliceraMod.LEGENDARY)));

    public static final RegistryObject<Item> WITHERED_LIFE_CHALICE = ITEMS.register("withered_life_chalice", () ->
            new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> ILUTHIAS_CHALICE = ITEMS.register("iluthias_chalice", () ->
            new IluthiasChaliceItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(ReliceraMod.LEGENDARY)));

    public static final RegistryObject<Item> DRIED_CROWN = ITEMS.register("dried_crown", () ->
            new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> NEREIAS_CROWN = ITEMS.register("nereias_crown", () ->
            new RelicCurioItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(ReliceraMod.LEGENDARY)));

    public static final RegistryObject<Item> WEATHERED_MOUNTAIN_COVENANT = ITEMS.register("weathered_mountain_covenant", () ->
            new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> BAROMS_COVENANT_STONE = ITEMS.register("baroms_covenant_stone", () ->
            new BaromsCovenantStoneItem(new Item.Properties().stacksTo(1).rarity(ReliceraMod.LEGENDARY)));

    public static final RegistryObject<Item> LUMINAS_CELESTIAL_LENS = ITEMS.register("luminas_celestial_lens", () ->
            new LuminasCelestialLensItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(ReliceraMod.LEGENDARY)));

    public static final RegistryObject<Item> RELIC_REPAIR_TABLE = ITEMS.register("relic_repair_table", () ->
            new BlockItem(ModBlocks.RELIC_REPAIR_TABLE.get(), new Item.Properties()
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> DREAMCATCHER_BOX = ITEMS.register("dreamcatcher_box", () ->
            new BlockItem(ModBlocks.DREAMCATCHER_BOX.get(), new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> FORGELING_SPAWN_EGG = ITEMS.register("forgeling_spawn_egg", () ->
            new ForgeSpawnEggItem(ModEntityTypes.FORGELING, 0x24120C, 0xE95C20,
                    new Item.Properties().rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> FORGELING_BUCKET = ITEMS.register("forgeling_bucket", () ->
            new ForgelingBucketItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.COMMON)));

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
