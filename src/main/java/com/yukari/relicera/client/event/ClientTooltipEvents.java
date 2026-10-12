package com.yukari.relicera.client.event;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.astral.AstralObservationClientData;
import com.yukari.relicera.common.curio.BrutalPlunderBadgeEffects;
import com.yukari.relicera.common.curio.CovenantTabletEffects;
import com.yukari.relicera.common.curio.FourfoldSherdPendantEffects;
import com.yukari.relicera.common.curio.MasterSmithsBroochEffects;
import com.yukari.relicera.common.curio.NereiasCrownEffects;
import com.yukari.relicera.common.curio.RippleheartRingClientData;
import com.yukari.relicera.common.curio.TurncoatsMedalEffects;
import com.yukari.relicera.common.item.BaromsCovenantStoneItem;
import com.yukari.relicera.common.item.InscribedCovenantTabletItem;
import com.yukari.relicera.common.item.feysilver.FeysilverForgingClientData;
import com.yukari.relicera.common.item.RippleheartRingItem;
import com.yukari.relicera.common.item.RippleheartRingItem.RingSide;
import com.yukari.relicera.common.item.TempestsReinsEffects;
import com.yukari.relicera.common.item.armor.devilsbearskin.DevilsBearskinEffects;
import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.registry.ModItems;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ReliceraMod.MOD_ID, value = Dist.CLIENT)
public final class ClientTooltipEvents {
    private static final String LIBTOOLTIPS_SHIFT_UP_KEY = "tooltip.libtooltips.generic.shift_up";
    private static final String LIBTOOLTIPS_SHIFT_DOWN_KEY = "tooltip.libtooltips.generic.shift_down";
    private static final int LIBTOOLTIPS_MAX_LINES = 100;
    private static final int LIBTOOLTIPS_MAX_SPACES_BEFORE_TOOLTIP = 10;

    private ClientTooltipEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAstralLensTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().is(ModItems.ASTRAL_LENS.get())) {
            appendAstralLensTooltip(event);
        }

        if (event.getItemStack().is(ModItems.FEYSILVER_FORGING_ART_VOLUME_ONE.get())) {
            appendFeysilverForgingArtVolumeOneTooltip(event);
        }

        if (event.getItemStack().is(ModItems.SILK_OF_NIGHT.get())) {
            appendSilkOfNightTooltip(event);
        }

        if (event.getItemStack().is(ModItems.WARFIRE_FRAGMENT.get())) {
            appendWarfireFragmentTooltip(event);
        }

        if (event.getItemStack().is(ModItems.NIGHT_GLOVES.get())) {
            appendNightGlovesTooltip(event);
        }

        if (event.getItemStack().is(ModItems.TREASURE_HUNTERS_GLOVES.get())) {
            appendTreasureHuntersGlovesTooltip(event);
        }

        if (event.getItemStack().is(ModItems.THOUSANDWEIGHT_GAUNTLETS.get())) {
            appendThousandweightGauntletsTooltip(event);
        }

        if (event.getItemStack().is(ModItems.STONEWALL_GREATSHIELD.get())) {
            appendStonewallGreatshieldTooltip(event);
        }

        if (event.getItemStack().is(ModItems.ASHEN_TOUCH.get())) {
            appendAshenTouchTooltip(event);
        }

        if (event.getItemStack().is(ModItems.STRIDER_SPURS.get())) {
            appendStriderSpursTooltip(event);
        }

        if (event.getItemStack().is(ModItems.WARP_CRYSTAL.get())) {
            appendWarpCrystalTooltip(event);
        }

        if (event.getItemStack().is(ModItems.COVENANT_TABLET.get())) {
            appendCovenantTabletTooltip(event);
        }

        if (event.getItemStack().is(ModItems.EPHEMERAL_BLOOM_PENDANT.get())) {
            appendEphemeralBloomPendantTooltip(event);
        }

        if (event.getItemStack().is(ModItems.BRUTAL_PLUNDER_BADGE.get())) {
            appendBrutalPlunderBadgeTooltip(event);
        }

        if (event.getItemStack().is(ModItems.MASTER_SMITHS_BROOCH.get())) {
            appendMasterSmithsBroochTooltip(event);
        }

        if (event.getItemStack().is(ModItems.TWO_HANDED_SWORD_BROOCH.get())) {
            appendTwoHandedSwordBroochTooltip(event);
        }

        if (event.getItemStack().is(ModItems.VINDICATORS_MEDAL.get())) {
            appendVindicatorsMedalTooltip(event);
        }

        if (event.getItemStack().is(ModItems.TURNCOATS_MEDAL.get())) {
            appendTurncoatsMedalTooltip(event);
        }

        if (event.getItemStack().is(ModItems.FOURFOLD_SHERD_PENDANT.get())) {
            appendFourfoldSherdPendantTooltip(event);
        }

        if (event.getItemStack().is(ModItems.GRANBELLS_FURNACE.get())) {
            appendGranbellsFurnaceTooltip(event);
        }

        if (event.getItemStack().is(ModItems.BAROMS_COVENANT_STONE.get())) {
            appendBaromsCovenantStoneTooltip(event);
        }

        if (event.getItemStack().is(ModItems.ILUTHIAS_CHALICE.get())) {
            appendIluthiasChaliceTooltip(event);
        }

        if (event.getItemStack().is(ModItems.NEREIAS_CROWN.get())) {
            appendNereiasCrownTooltip(event);
        }

        if (event.getItemStack().is(ModItems.LUMINAS_CELESTIAL_LENS.get())) {
            appendLuminasCelestialLensTooltip(event);
        }

        if (event.getItemStack().is(ModItems.DREAMCATCHER_BOX.get())) {
            event.getToolTip().add(tooltipLine("dreamcatcher_box", 0));
        }

        if (event.getItemStack().is(ModItems.RIPPLEHEART_PEARL.get())) {
            appendRippleheartPearlTooltip(event);
        }

        if (event.getItemStack().is(ModItems.TEMPESTS_REINS.get())) {
            appendTempestsReinsTooltip(event);
        }

        if (event.getItemStack().is(ModItems.DIVINE_SEVERANCE_RING.get())) {
            appendDivineSeveranceRingTooltip(event);
        }

        if (event.getItemStack().is(ModItems.RING_OF_SATIETY.get())) {
            appendRingOfSatietyTooltip(event);
        }

        if (event.getItemStack().is(ModItems.LITTLE_TAILORS_BELT.get())) {
            appendLittleTailorsBeltTooltip(event);
        }

        if (event.getItemStack().is(ModItems.WARRIOR_BELT.get())) {
            appendWarriorBeltTooltip(event);
        }

        if (event.getItemStack().is(ModItems.BRUTE_BELT.get())) {
            appendBruteBeltTooltip(event);
        }

        if (ModItems.VERDANT_SCALE_BELT.isPresent()
                && event.getItemStack().is(ModItems.VERDANT_SCALE_BELT.get())) {
            appendVerdantScaleBeltTooltip(event);
        }

        if (event.getItemStack().is(ModItems.DEVILS_BEARSKIN.get())) {
            appendDevilsBearskinTooltip(event);
        }

        if (event.getItemStack().is(ModItems.RABBITS_POCKET_WATCH.get())) {
            appendRabbitsPocketWatchTooltip(event);
        }

        if (event.getItemStack().is(ModItems.RIPPLEHEART_RINGS.get())) {
            appendRippleheartRingsTooltip(event);
        }

        if (ModItems.IMMORTAL_SOUL_RING.isPresent()
                && event.getItemStack().is(ModItems.IMMORTAL_SOUL_RING.get())) {
            appendImmortalSoulRingTooltip(event);
        }

        if (event.getItemStack().getItem() instanceof RippleheartRingItem ringItem) {
            appendRippleheartRingTooltip(event, ringItem.getSide());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSpyglassTooltip(ItemTooltipEvent event) {
        if (!event.getItemStack().is(Items.SPYGLASS) || event.getEntity() == null) {
            return;
        }

        int observedCount = AstralObservationClientData.getObservedCount();
        if (observedCount <= 0 || AstralObservationClientData.hasClaimedAstralLens()) {
            return;
        }

        event.getToolTip().add(Component.translatable("tooltip.relicera.spyglass.observed_moon_phases")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(" "))
                .append(Component.literal(observedCount + " / 8").withStyle(ChatFormatting.WHITE)));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onReliceraTooltipOrder(ItemTooltipEvent event) {
        if (isReliceraItem(event.getItemStack())) {
            removeLibTooltipsGeneratedLines(event);
            moveReliceraModNameToEnd(event.getToolTip());
        }
    }

    private static void appendAstralLensTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(tooltipLine("astral_lens", 0));
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("astral_lens", 1));
            return;
        }

        event.getToolTip().add(tooltipLine("astral_lens", 2));
        event.getToolTip().add(tooltipLine("astral_lens", 3, gold("-85%")));
        event.getToolTip().add(tooltipLine("astral_lens", 4, gold("-30%")));
    }

    private static void appendFeysilverForgingArtVolumeOneTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(tooltipLine("feysilver_forging_art_volume_one", 0));
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("feysilver_forging_art_volume_one", 1));
            return;
        }

        event.getToolTip().add(tooltipLine("feysilver_forging_art_volume_one", 2));
        event.getToolTip().add(tooltipLine("feysilver_forging_art_volume_one", 3));
        if (FeysilverForgingClientData.hasVolumeOne()) {
            event.getToolTip().add(Component.empty());
            event.getToolTip().add(tooltipLine("feysilver_forging_art_volume_one", 4));
        }
    }

    private static void appendSilkOfNightTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(tooltipLine("silk_of_night", 0));
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("silk_of_night", 1));
            return;
        }

        event.getToolTip().add(tooltipLine("silk_of_night", 2));
        event.getToolTip().add(tooltipLine("silk_of_night", 3));
        event.getToolTip().add(tooltipLine("silk_of_night", 4));
    }

    private static void appendNightGlovesTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("night_gloves"));
            return;
        }

        event.getToolTip().add(tooltipLine("night_gloves", 1, gold(formatNightGlovesDamageBonus())));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("night_gloves", 2));
        event.getToolTip().add(tooltipLine("night_gloves", 3));
    }

    private static void appendTreasureHuntersGlovesTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("treasure_hunters_gloves"));
            return;
        }

        event.getToolTip().add(tooltipLine("treasure_hunters_gloves", 1));
        event.getToolTip().add(tooltipLine("treasure_hunters_gloves", 2));
        event.getToolTip().add(tooltipLine("treasure_hunters_gloves", 3,
                gold(String.valueOf(ModCommonConfig.TREASURE_HUNTERS_GLOVES_MAX_ITEMS_TAKEN_BEFORE_REFRESH.get()))));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("treasure_hunters_gloves", 4));
        event.getToolTip().add(tooltipLine("treasure_hunters_gloves", 5,
                gold(formatSignedDecimal(com.yukari.relicera.common.curio.TreasureHuntersGlovesEffects.getLuckBonus(event.getItemStack())))));
    }

    private static void appendAshenTouchTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("ashen_touch"));
            return;
        }

        event.getToolTip().add(tooltipLine("ashen_touch", 1));
        event.getToolTip().add(tooltipLine("ashen_touch", 2, gold(formatAshenTouchDamageBonus())));
    }

    private static void appendStriderSpursTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("strider_spurs"));
            return;
        }

        event.getToolTip().add(tooltipLine("strider_spurs", 1, gold(formatStriderSpursSpeedBonus())));
    }

    private static void appendWarpCrystalTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("warp_crystal"));
            return;
        }

        event.getToolTip().add(tooltipLine("warp_crystal", 1,
                gold(formatUnsignedPercent(ModCommonConfig.WARP_CRYSTAL_DODGE_CHANCE.get()))));
        event.getToolTip().add(tooltipLine("warp_crystal", 2));
    }

    private static void appendCovenantTabletTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("covenant_tablet"));
            return;
        }

        event.getToolTip().add(tooltipLine("covenant_tablet", 1));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(CovenantTabletEffects.hasFallImmunity(stack)
                ? tooltipLine("covenant_tablet", 3)
                : tooltipLine("covenant_tablet", 2, darkGray(formatUnsignedNumber(ModCommonConfig.COVENANT_TABLET_DAMAGE_TASK_THRESHOLD.get()))));
        event.getToolTip().add(CovenantTabletEffects.hasMiningSpeed(stack)
                ? tooltipLine("covenant_tablet", 5, gold(formatSignedPercent(ModCommonConfig.COVENANT_TABLET_MINING_SPEED_BONUS.get())))
                : tooltipLine("covenant_tablet", 4));
        event.getToolTip().add(CovenantTabletEffects.hasVillagerDiscount(stack)
                ? tooltipLine("covenant_tablet", 7, gold(formatNegativePercent(ModCommonConfig.COVENANT_TABLET_VILLAGER_TRADE_DISCOUNT.get())))
                : tooltipLine("covenant_tablet", 6));
        if (CovenantTabletEffects.isBlank(stack)) {
            event.getToolTip().add(Component.empty());
            event.getToolTip().add(tooltipLine("covenant_tablet", 8));
        }
    }

    private static void appendEphemeralBloomPendantTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("ephemeral_bloom_pendant"));
            return;
        }

        event.getToolTip().add(tooltipLine("ephemeral_bloom_pendant", 1));
    }

    private static void appendWarfireFragmentTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(tooltipLine("warfire_fragment", 0));
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("warfire_fragment", 1));
            return;
        }

        event.getToolTip().add(tooltipLine("warfire_fragment", 2));
        event.getToolTip().add(tooltipLine("warfire_fragment", 3));
        event.getToolTip().add(tooltipLine("warfire_fragment", 4));
        event.getToolTip().add(tooltipLine("warfire_fragment", 5));
    }

    private static void appendBrutalPlunderBadgeTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("brutal_plunder_badge"));
            return;
        }

        event.getToolTip().add(tooltipLine("brutal_plunder_badge", 1,
                gold(formatSignedNumber(ModCommonConfig.BRUTAL_PLUNDER_BADGE_LOOTING_BONUS.get()))));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("brutal_plunder_badge", 2));
        event.getToolTip().add(tooltipLine("brutal_plunder_badge", 3));
        event.getToolTip().add(tooltipLine("brutal_plunder_badge", 4));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("brutal_plunder_badge", 5));
        event.getToolTip().add(tooltipLine("brutal_plunder_badge", 6, gold(formatBrutalPlunderBadgeDamageBonus(event))));
    }

    private static void appendMasterSmithsBroochTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("master_smiths_brooch"));
            return;
        }

        event.getToolTip().add(tooltipLine(
                "master_smiths_brooch",
                1,
                gold(formatSignedNumber(ModCommonConfig.MASTER_SMITHS_BROOCH_ARMOR_TOUGHNESS_PER_TRIMMED_ARMOR.get()))
        ));
        event.getToolTip().add(tooltipLine(
                "master_smiths_brooch",
                2,
                gold(formatSignedPercent(ModCommonConfig.MASTER_SMITHS_BROOCH_DAMAGE_REDUCTION_PER_COATED_ARMOR.get()))
        ));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("master_smiths_brooch", 3));
        event.getToolTip().add(tooltipLine(
                "master_smiths_brooch",
                4,
                gold(formatSignedNumber(getMasterSmithsBroochArmorToughnessBonus(event)))
        ));
        event.getToolTip().add(tooltipLine(
                "master_smiths_brooch",
                5,
                gold(formatSignedPercent(getMasterSmithsBroochDamageReduction(event)))
        ));
    }

    private static void appendTwoHandedSwordBroochTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("two_handed_sword_brooch"));
            return;
        }

        event.getToolTip().add(tooltipLine("two_handed_sword_brooch", 1));
        event.getToolTip().add(tooltipLine(
                "two_handed_sword_brooch",
                2,
                gold(formatSignedPercent(ModCommonConfig.TWO_HANDED_SWORD_BROOCH_SWORD_DAMAGE_BONUS.get())),
                gold(formatSignedPercent(-ModCommonConfig.TWO_HANDED_SWORD_BROOCH_ATTACK_SPEED_PENALTY.get()))
        ));
        event.getToolTip().add(tooltipLine("two_handed_sword_brooch", 3));
        event.getToolTip().add(tooltipLine(
                "two_handed_sword_brooch",
                4,
                gold(formatSignedNumber(ModCommonConfig.TWO_HANDED_SWORD_BROOCH_ENTITY_REACH_BONUS.get()))
        ));
    }

    private static void appendVindicatorsMedalTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("vindicators_medal"));
            return;
        }

        event.getToolTip().add(tooltipLine(
                "vindicators_medal",
                1,
                gold(formatSignedPercent(ModCommonConfig.VINDICATORS_MEDAL_AXE_ATTACK_SPEED_BONUS.get()))
        ));
        event.getToolTip().add(tooltipLine("vindicators_medal", 2));
        event.getToolTip().add(tooltipLine("vindicators_medal", 3));
    }

    private static void appendTurncoatsMedalTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("turncoats_medal"));
            return;
        }

        event.getToolTip().add(tooltipLine(
                "turncoats_medal",
                1
        ));
        event.getToolTip().add(tooltipLine(
                "turncoats_medal",
                2,
                gold(formatSignedPercent(ModCommonConfig.TURNCOATS_MEDAL_HERO_DAMAGE_BONUS_PER_LEVEL.get()))
        ));
        event.getToolTip().add(tooltipLine(
                "turncoats_medal",
                3,
                gold(formatSignedPercent(ModCommonConfig.TURNCOATS_MEDAL_BAD_OMEN_LIFE_STEAL_PER_LEVEL.get()))
        ));
        event.getToolTip().add(tooltipLine("turncoats_medal", 4));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("turncoats_medal", 5));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("turncoats_medal", 6));

        Player player = event.getEntity();
        double damageBonus = player == null ? 0.0D : TurncoatsMedalEffects.getHeroDamageBonus(player);
        double lifeSteal = player == null ? 0.0D : TurncoatsMedalEffects.getBadOmenLifeSteal(player);
        event.getToolTip().add(tooltipLine(
                "turncoats_medal",
                7,
                gold(formatSignedDecimal(damageBonus * 100.0D) + "%")
        ));
        event.getToolTip().add(tooltipLine(
                "turncoats_medal",
                8,
                gold(formatSignedDecimal(lifeSteal * 100.0D) + "%")
        ));
    }

    private static void appendFourfoldSherdPendantTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("fourfold_sherd_pendant"));
            return;
        }

        event.getToolTip().add(tooltipLine("fourfold_sherd_pendant", 1));

        List<ItemStack> sherds = FourfoldSherdPendantEffects.getSherdsInStack(event.getItemStack());
        if (sherds.isEmpty()) {
            return;
        }

        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("fourfold_sherd_pendant", 2));
        for (ItemStack sherd : sherds) {
            event.getToolTip().add(createFourfoldSherdEffectLine(sherd));
        }
    }

    private static Component createFourfoldSherdEffectLine(ItemStack sherd) {
        FourfoldSherdPendantEffects.SherdPattern pattern = FourfoldSherdPendantEffects.getPattern(sherd);
        if (pattern == null) {
            return tooltipLine("fourfold_sherd_pendant", 3, sherd.getHoverName().copy().withStyle(ChatFormatting.GRAY));
        }

        return switch (pattern) {
            case SNORT -> fourfoldEffectLine(4, 24, "+1");
            case BREWER -> fourfoldEffectLine(5, 25, "+20%");
            case MOURNER -> fourfoldEffectLine(6, 26, "+50%");
            case EXPLORER -> fourfoldEffectLine(7, 27, "+20%");
            case HEARTBREAK -> fourfoldEffectLine(8, 28, "30%", "+25%");
            case ARMS_UP -> fourfoldEffectLine(9, 29, "+1");
            case ARCHER -> fourfoldEffectLine(10, 30, "+20%");
            case DANGER -> fourfoldEffectLine(11, 31, "-50%");
            case PLENTY -> fourfoldEffectLine(12, 32, "+1");
            case SHEAF -> fourfoldEffectLine(13, 33, "+1");
            case SHELTER -> fourfoldEffectLine(14, 34, "+4");
            case BLADE -> fourfoldEffectLine(15, 35, "+10%");
            case MINER -> fourfoldEffectLine(16, 36, "+10%");
            case SKULL -> fourfoldEffectLine(17, 37, "+15%");
            case HEART -> fourfoldEffectLine(18, 38, "+4");
            case PRIZE -> fourfoldEffectLine(19, 39, "+1");
            case FRIEND -> fourfoldEffectLine(20, 40, "+1");
            case ANGLER -> fourfoldEffectLine(21, 41, "+1");
            case HOWL -> fourfoldEffectLine(22, 42, "+20%");
            case BURN -> fourfoldEffectLine(23, 43, "+20%");
        };
    }

    private static Component fourfoldEffectLine(int patternLine, int effectLine, String... goldValues) {
        Object[] args = new Object[goldValues.length + 1];
        args[0] = tooltipLine("fourfold_sherd_pendant", patternLine).copy().withStyle(ChatFormatting.DARK_PURPLE);
        for (int index = 0; index < goldValues.length; index++) {
            args[index + 1] = gold(goldValues[index]);
        }
        return tooltipLine("fourfold_sherd_pendant", effectLine, args);
    }

    private static void appendBaromsCovenantStoneTooltip(ItemTooltipEvent event) {
        String itemId = "baroms_covenant_stone";
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine(itemId, 0));
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine(itemId, 1));
            return;
        }

        List<ItemStack> contracts = BaromsCovenantStoneItem.getContracts(event.getItemStack());
        if (contracts.stream().allMatch(ItemStack::isEmpty)) {
            event.getToolTip().add(Component.translatable("tooltip.relicera.baroms_covenant_stone.empty"));
            event.getToolTip().add(Component.translatable("tooltip.relicera.baroms_covenant_stone.neutrality"));
        }
        for (ItemStack contract : contracts) {
            appendBaromsCovenantContractTooltip(event.getToolTip(), contract);
        }
    }

    private static void appendBaromsCovenantContractTooltip(List<Component> tooltip, ItemStack contract) {
        EntityType<?> entityType = InscribedCovenantTabletItem.getContractEntity(contract);
        int effectLine;
        if (entityType == EntityType.IRON_GOLEM) {
            effectLine = 2;
        } else if (entityType == EntityType.SILVERFISH) {
            effectLine = 3;
        } else if (entityType == EntityType.VILLAGER) {
            effectLine = 5;
        } else if (entityType == EntityType.PILLAGER) {
            effectLine = 6;
        } else if (entityType == EntityType.ENDER_DRAGON) {
            effectLine = 7;
        } else if (entityType == EntityType.ZOMBIE) {
            effectLine = 8;
        } else if (entityType == EntityType.CREEPER) {
            effectLine = 9;
        } else if (entityType == EntityType.BLAZE) {
            effectLine = 10;
        } else if (entityType == EntityType.HUSK) {
            effectLine = 11;
        } else if (entityType == EntityType.SKELETON) {
            effectLine = 12;
        } else if (entityType == EntityType.WANDERING_TRADER) {
            effectLine = 13;
        } else if (entityType == EntityType.PIGLIN) {
            effectLine = 14;
        } else if (entityType == EntityType.WITCH) {
            effectLine = 15;
        } else {
            return;
        }

        tooltip.add(contract.getHoverName().copy().withStyle(ChatFormatting.LIGHT_PURPLE));
        if (entityType == EntityType.SILVERFISH) {
            double volumeMultiplier = ModCommonConfig.BAROM_VOLUME_MULTIPLIER.get();
            tooltip.add(volumeMultiplier == 1.0D
                    ? tooltipLine("baroms_covenant_stone", 3)
                    : tooltipLine("baroms_covenant_stone", 4, gold(java.math.BigDecimal.valueOf(volumeMultiplier)
                            .stripTrailingZeros().toPlainString())));
        } else if (entityType == EntityType.HUSK) {
            String chance = java.math.BigDecimal.valueOf(ModCommonConfig.BAROM_HUSK_HUNGER_CHANCE.get())
                    .multiply(java.math.BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%";
            tooltip.add(tooltipLine("baroms_covenant_stone", effectLine, gold(chance)));
        } else if (entityType == EntityType.WITCH) {
            String chance = java.math.BigDecimal.valueOf(ModCommonConfig.BAROM_WITCH_POTION_PRESERVATION_CHANCE.get())
                    .multiply(java.math.BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%";
            tooltip.add(tooltipLine("baroms_covenant_stone", effectLine, gold(chance)));
        } else {
            tooltip.add(tooltipLine("baroms_covenant_stone", effectLine));
        }
    }

    private static void appendGranbellsFurnaceTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("granbells_furnace", 0));
        event.getToolTip().add(Component.empty());

        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("granbells_furnace", 1));
            return;
        }

        event.getToolTip().add(tooltipLine("granbells_furnace", 2, gold(formatGranbellsFurnaceDamageBonus())));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("granbells_furnace", 3));
        event.getToolTip().add(tooltipLine("granbells_furnace", 4));
        event.getToolTip().add(tooltipLine("granbells_furnace", 5));
        event.getToolTip().add(tooltipLine("granbells_furnace", 6));
        event.getToolTip().add(tooltipLine("granbells_furnace", 7));
        event.getToolTip().add(tooltipLine("granbells_furnace", 8));
        event.getToolTip().add(tooltipLine("granbells_furnace", 9));
    }

    private static void appendIluthiasChaliceTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("iluthias_chalice", 0));
        event.getToolTip().add(Component.empty());

        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("iluthias_chalice", 1));
            return;
        }

        event.getToolTip().add(tooltipLine("iluthias_chalice", 2));
        event.getToolTip().add(tooltipLine("iluthias_chalice", 3));
        event.getToolTip().add(tooltipLine("iluthias_chalice", 4,
                gold(formatSignedPercent(ModCommonConfig.ILUTHIAS_CHALICE_UNDEAD_DAMAGE_BONUS.get())),
                gold(formatNegativePercent(ModCommonConfig.ILUTHIAS_CHALICE_UNDEAD_DAMAGE_REDUCTION.get()))));
        event.getToolTip().add(tooltipLine("iluthias_chalice", 5));
        event.getToolTip().add(tooltipLine("iluthias_chalice", 6));
        event.getToolTip().add(tooltipLine("iluthias_chalice", 7));
    }

    private static void appendNereiasCrownTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("nereias_crown", 0));
        event.getToolTip().add(Component.empty());

        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("nereias_crown", 1));
            return;
        }

        event.getToolTip().add(tooltipLine("nereias_crown", 2));
        event.getToolTip().add(tooltipLine("nereias_crown", 3));
        event.getToolTip().add(tooltipLine("nereias_crown", 4));
        event.getToolTip().add(tooltipLine("nereias_crown", 5,
                gold(formatNegativePercent(ModCommonConfig.NEREIAS_CROWN_ACTIVE_DAMAGE_REDUCTION.get()))));
        event.getToolTip().add(tooltipLine("nereias_crown", 6));
        event.getToolTip().add(tooltipLine("nereias_crown", 7));
        event.getToolTip().add(tooltipLine("nereias_crown", 8,
                gold(formatUnsignedPercent(ModCommonConfig.NEREIAS_CROWN_AQUATIC_ATTACK_DAMAGE_SHARE.get()))));
        event.getToolTip().add(tooltipLine("nereias_crown", 9,
                gold(formatUnsignedPercent(ModCommonConfig.NEREIAS_CROWN_AQUATIC_MAX_HEALTH_ARMOR_SHARE.get()))));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("nereias_crown", 10));
        event.getToolTip().add(tooltipLine("nereias_crown", 11, gold(formatSignedNumber(getNereiasCrownAttackDamageBonus(event)))));
        event.getToolTip().add(tooltipLine("nereias_crown", 12, gold(formatSignedNumber(getNereiasCrownArmorBonus(event)))));
    }

    private static void appendLuminasCelestialLensTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("luminas_celestial_lens", 0));
        event.getToolTip().add(Component.empty());

        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("luminas_celestial_lens", 1));
            return;
        }

        event.getToolTip().add(tooltipLine("luminas_celestial_lens", 2));
    }

    private static void appendTempestsReinsTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(tooltipLine("tempests_reins", 0));
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("tempests_reins", 1));
            return;
        }

        event.getToolTip().add(tooltipLine("tempests_reins", 2,
                gold(formatSignedPercent(TempestsReinsEffects.MOVEMENT_SPEED_BONUS)),
                gold(formatSignedPercent(TempestsReinsEffects.JUMP_STRENGTH_BONUS))));
        event.getToolTip().add(tooltipLine("tempests_reins", 3,
                gold(formatSignedNumber(TempestsReinsEffects.STEP_HEIGHT_BONUS))));
        event.getToolTip().add(tooltipLine("tempests_reins", 4));
        event.getToolTip().add(tooltipLine("tempests_reins", 5));
        event.getToolTip().add(tooltipLine("tempests_reins", 6));
    }

    private static void appendRippleheartPearlTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(tooltipLine("rippleheart_pearl", 0));
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("rippleheart_pearl", 1));
            return;
        }

        event.getToolTip().add(tooltipLine("rippleheart_pearl", 2));
        event.getToolTip().add(tooltipLine("rippleheart_pearl", 3));
    }

    private static void appendDivineSeveranceRingTooltip(ItemTooltipEvent event) {
        //event.getToolTip().add(Component.empty());
        //event.getToolTip().add(tooltipLine("divine_severance_ring", 0));
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("divine_severance_ring", 1));
            return;
        }

        event.getToolTip().add(tooltipLine("divine_severance_ring", 2));
    }

    private static void appendRingOfSatietyTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("ring_of_satiety"));
            return;
        }

        event.getToolTip().add(tooltipLine("ring_of_satiety", 1));
        event.getToolTip().add(tooltipLine("ring_of_satiety", 2));
        event.getToolTip().add(tooltipLine("ring_of_satiety", 3));
    }

    private static void appendLittleTailorsBeltTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("little_tailors_belt"));
            return;
        }

        event.getToolTip().add(tooltipLine("little_tailors_belt", 1));
        event.getToolTip().add(tooltipLine("little_tailors_belt", 2));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("little_tailors_belt", 3));
    }

    private static void appendWarriorBeltTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("warrior_belt"));
            return;
        }

        event.getToolTip().add(tooltipLine("warrior_belt", 1,
                gold(formatSignedPercent(ModCommonConfig.WARRIOR_BELT_CRITICAL_DAMAGE_BONUS.get()))));
        event.getToolTip().add(tooltipLine("warrior_belt", 2,
                gold(formatSignedPercent(ModCommonConfig.WARRIOR_BELT_KNOCKBACK_RESISTANCE.get()))));
        event.getToolTip().add(tooltipLine("warrior_belt", 3,
                gold(formatSignedPercent(ModCommonConfig.WARRIOR_BELT_MOVEMENT_SPEED_BONUS.get()))));
    }

    private static void appendBruteBeltTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("brute_belt"));
            return;
        }

        event.getToolTip().add(tooltipLine("brute_belt", 1,
                gold(formatSignedPercent(ModCommonConfig.BRUTE_BELT_MAX_HEALTH_BONUS.get()))));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("brute_belt", 2));
        event.getToolTip().add(tooltipLine("brute_belt", 3));
        event.getToolTip().add(tooltipLine("brute_belt", 4));
    }

    private static void appendVerdantScaleBeltTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("verdant_scale_belt"));
            return;
        }

        event.getToolTip().add(tooltipLine("verdant_scale_belt", 1,
                gold(formatSignedNumber(ModCommonConfig.VERDANT_SCALE_BELT_ARMOR_TOUGHNESS.get()))));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("verdant_scale_belt", 2));
        event.getToolTip().add(tooltipLine("verdant_scale_belt", 3));
        event.getToolTip().add(tooltipLine("verdant_scale_belt", 4));
    }

    private static void appendThousandweightGauntletsTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("thousandweight_gauntlets"));
            return;
        }

        event.getToolTip().add(tooltipLine("thousandweight_gauntlets", 1,
                gold(formatSignedPercent(-ModCommonConfig.THOUSANDWEIGHT_GAUNTLETS_ATTACK_SPEED_REDUCTION.get()))));

        event.getToolTip().add(Component.empty());

        event.getToolTip().add(tooltipLine("thousandweight_gauntlets", 2));
        event.getToolTip().add(tooltipLine("thousandweight_gauntlets", 3));
        event.getToolTip().add(tooltipLine("thousandweight_gauntlets", 4));
    }

    private static void appendStonewallGreatshieldTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("stonewall_greatshield"));
            return;
        }

        event.getToolTip().add(tooltipLine("stonewall_greatshield", 1));
        event.getToolTip().add(tooltipLine("stonewall_greatshield", 2));
        event.getToolTip().add(tooltipLine("stonewall_greatshield", 3));
    }

    private static void appendDevilsBearskinTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("devils_bearskin"));
            return;
        }

        ItemStack stack = event.getItemStack();
        if (DevilsBearskinEffects.isReleased(stack)) {
            event.getToolTip().add(tooltipLine("devils_bearskin", 9));
            event.getToolTip().add(Component.empty());
            event.getToolTip().add(tooltipLine("devils_bearskin", 10));
            return;
        }

        event.getToolTip().add(tooltipLine("devils_bearskin", 1));
        event.getToolTip().add(tooltipLine("devils_bearskin", 2));
        event.getToolTip().add(tooltipLine("devils_bearskin", 3));
        event.getToolTip().add(tooltipLine("devils_bearskin", 4));
        if (DevilsBearskinEffects.hasReachedTradeRefusalStage(stack)) {
            event.getToolTip().add(tooltipLine("devils_bearskin", 5));
        }
        if (DevilsBearskinEffects.hasReachedNauseaStage(stack)) {
            event.getToolTip().add(tooltipLine("devils_bearskin", 6));
        }
        event.getToolTip().add(tooltipLine(
                "devils_bearskin",
                7,
                gold(formatUnsignedPercent(DevilsBearskinEffects.MAX_HEALTH_LOSS))
        ));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("devils_bearskin", 8));
    }

    private static void appendRabbitsPocketWatchTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(tooltipLine("rabbits_pocket_watch", 0));
        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine("rabbits_pocket_watch", 1));
    }

    private static void appendRippleheartRingsTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(tooltipLine(
                "rippleheart_rings",
                0,
                gold(Component.translatable("tooltip.relicera.rippleheart_ring.side.red").getString()),
                gold(Component.translatable("tooltip.relicera.rippleheart_ring.side.blue").getString())
        ));
    }

    private static void appendImmortalSoulRingTooltip(ItemTooltipEvent event) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("immortal_soul_ring"));
            return;
        }

        event.getToolTip().add(tooltipLine("immortal_soul_ring", 1,
                gold(formatUnsignedPercent(ModCommonConfig.IMMORTAL_SOUL_RING_MAGIC_DAMAGE_RATIO.get()))));
        event.getToolTip().add(tooltipLine("immortal_soul_ring", 2));
        event.getToolTip().add(tooltipLine("immortal_soul_ring", 3));
    }

    private static void appendRippleheartRingTooltip(ItemTooltipEvent event, RingSide side) {
        event.getToolTip().add(Component.empty());
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(holdShiftLine("rippleheart_ring"));
            return;
        }

        String sideId = side == RingSide.RED ? "rippleheart_ring.red" : "rippleheart_ring.blue";
        event.getToolTip().add(tooltipLine(
                sideId,
                0,
                gold(formatUnsignedNumber(ModCommonConfig.RIPPLEHEART_RINGS_ACTIVATION_RANGE.get()))
        ));

        if (side == RingSide.RED) {
            event.getToolTip().add(tooltipLine(sideId, 1, goldEffect(MobEffects.DAMAGE_RESISTANCE, "III")));
            event.getToolTip().add(tooltipLine(sideId, 2));
            event.getToolTip().add(tooltipLine(
                    sideId,
                    3,
                    gold(formatSignedPercent(ModCommonConfig.RIPPLEHEART_RINGS_RED_DAMAGE_BONUS.get()))
            ));
            event.getToolTip().add(tooltipLine(
                    sideId,
                    4,
                    gold(formatSignedPercent(ModCommonConfig.RIPPLEHEART_RINGS_RED_ATTACK_SPEED_BONUS.get()))
            ));
        } else {
            event.getToolTip().add(tooltipLine(sideId, 1, goldEffect(MobEffects.DAMAGE_BOOST, "III")));
            event.getToolTip().add(tooltipLine(sideId, 2));
            event.getToolTip().add(tooltipLine(
                    sideId,
                    3,
                    gold(formatSignedPercent(ModCommonConfig.RIPPLEHEART_RINGS_BLUE_DAMAGE_REDUCTION.get()))
            ));
            event.getToolTip().add(tooltipLine(
                    sideId,
                    4,
                    gold(formatSignedPercent(ModCommonConfig.RIPPLEHEART_RINGS_BLUE_HEALING_BONUS.get()))
            ));
        }

        event.getToolTip().add(Component.empty());
        event.getToolTip().add(tooltipLine(sideId, 5, getRippleheartCounterpartName(event.getItemStack(), side)));
    }

    private static Component getRippleheartCounterpartName(ItemStack stack, RingSide side) {
        Optional<UUID> stackPairId = RippleheartRingItem.getPairId(stack);
        if (stackPairId.isEmpty()) {
            return Component.translatable("tooltip.relicera.rippleheart_ring.status.unbound")
                    .withStyle(ChatFormatting.DARK_GRAY);
        }

        if (!stackPairId.equals(RippleheartRingClientData.getPairId())) {
            return Component.translatable("tooltip.relicera.rippleheart_ring.status.not_detected")
                    .withStyle(ChatFormatting.DARK_GRAY);
        }

        Component wearerName = side == RingSide.RED
                ? RippleheartRingClientData.getBlueWearerName()
                : RippleheartRingClientData.getRedWearerName();
        if (wearerName.getString().isBlank()) {
            return Component.translatable("tooltip.relicera.rippleheart_ring.status.not_detected")
                    .withStyle(ChatFormatting.DARK_GRAY);
        }
        return gold(wearerName.getString());
    }

    private static Component goldEffect(MobEffect effect, String amplifier) {
        return Component.translatable(effect.getDescriptionId())
                .append(" " + amplifier)
                .withStyle(ChatFormatting.GOLD);
    }

    private static String formatSignedNumber(int value) {
        return value >= 0 ? "+" + value : String.valueOf(value);
    }

    private static String formatSignedNumber(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.0001D) {
            return formatSignedNumber((int) Math.rint(value));
        }
        return (value >= 0.0D ? "+" : "") + String.format(java.util.Locale.ROOT, "%.1f", value);
    }

    private static String formatUnsignedNumber(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.0001D) {
            return String.valueOf((int) Math.rint(value));
        }
        return String.format(java.util.Locale.ROOT, "%.1f", value);
    }

    private static String formatSignedDecimal(double value) {
        String formatted = String.format(java.util.Locale.ROOT, "%.2f", Math.abs(value))
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
        return (value >= 0.0D ? "+" : "-") + formatted;
    }

    private static String formatNightGlovesDamageBonus() {
        return "+" + Math.round(ModCommonConfig.NIGHT_GLOVES_NIGHT_ATTACK_DAMAGE_BONUS.get() * 100.0D) + "%";
    }

    private static String formatAshenTouchDamageBonus() {
        return "+" + Math.round(ModCommonConfig.ASHEN_TOUCH_BURNING_TARGET_DAMAGE_BONUS.get() * 100.0D) + "%";
    }

    private static String formatStriderSpursSpeedBonus() {
        return "+" + Math.round(ModCommonConfig.STRIDER_SPURS_SPEED_BONUS.get() * 100.0D) + "%";
    }

    private static String formatGranbellsFurnaceDamageBonus() {
        return formatSignedNumber(ModCommonConfig.GRANBELLS_FURNACE_DAMAGE_BONUS.get());
    }

    private static String formatSignedPercent(double value) {
        long percent = Math.round(value * 100.0D);
        return (percent >= 0L ? "+" : "") + percent + "%";
    }

    private static String formatUnsignedPercent(double value) {
        return Math.round(value * 100.0D) + "%";
    }

    private static String formatNegativePercent(double value) {
        long percent = Math.round(value * 100.0D);
        return "-" + percent + "%";
    }

    private static double getNereiasCrownAttackDamageBonus(ItemTooltipEvent event) {
        Player player = event.getEntity();
        return player == null ? 0.0D : NereiasCrownEffects.getAquaticAttackDamageBonus(player);
    }

    private static double getNereiasCrownArmorBonus(ItemTooltipEvent event) {
        Player player = event.getEntity();
        return player == null ? 0.0D : NereiasCrownEffects.getAquaticArmorBonus(player);
    }

    private static double getMasterSmithsBroochArmorToughnessBonus(ItemTooltipEvent event) {
        Player player = event.getEntity();
        return player == null ? 0.0D : MasterSmithsBroochEffects.getArmorToughnessBonus(player);
    }

    private static double getMasterSmithsBroochDamageReduction(ItemTooltipEvent event) {
        Player player = event.getEntity();
        return player == null ? 0.0D : MasterSmithsBroochEffects.getDamageReduction(player);
    }

    private static String formatBrutalPlunderBadgeDamageBonus(ItemTooltipEvent event) {
        int effectiveLootingLevel = getBrutalPlunderBadgeTooltipLootingLevel(event);
        return formatSignedPercent(BrutalPlunderBadgeEffects.getDamageBonus(effectiveLootingLevel));
    }

    private static int getBrutalPlunderBadgeTooltipLootingLevel(ItemTooltipEvent event) {
        Player player = event.getEntity();
        if (player == null || !BrutalPlunderBadgeEffects.isEquipped(player)) {
            return 0;
        }

        int baseLooting = EnchantmentHelper.getMobLooting(player);
        int curiosLooting = CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler -> handler.getLootingLevel(player.damageSources().playerAttack(player), player, baseLooting))
                .orElse(0);
        return baseLooting + curiosLooting;
    }

    private static Component holdShiftLine(String itemId) {
        return tooltipLine(itemId, 0, gold("Shift"));
    }

    private static Component holdShiftLine(String itemId, int line) {
        return tooltipLine(itemId, line, gold("Shift"));
    }

    private static Component tooltipLine(String itemId, int line, Object... args) {
        return Component.translatable("tooltip.relicera." + itemId + "." + line, args);
    }

    private static Component gold(String text) {
        return Component.literal(text).withStyle(ChatFormatting.GOLD);
    }

    private static Component darkGray(String text) {
        return Component.literal(text).withStyle(ChatFormatting.DARK_GRAY);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        if (!isReliceraItem(event)) {
            return;
        }

        List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
        for (int index = elements.size() - 1; index >= 0; index--) {
            Either<FormattedText, TooltipComponent> element = elements.get(index);
            if (element.left().map(FormattedText::getString).filter(ClientTooltipEvents::isReliceraModNameLine).isPresent()) {
                elements.remove(index);
                elements.add(element);
            }
        }
    }

    private static void moveReliceraModNameToEnd(List<Component> tooltip) {
        for (int index = tooltip.size() - 1; index >= 0; index--) {
            Component line = tooltip.get(index);
            if (isReliceraModNameLine(line.getString())) {
                tooltip.remove(index);
                tooltip.add(line);
            }
        }
    }

    private static void removeLibTooltipsGeneratedLines(ItemTooltipEvent event) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
        if (itemId == null) {
            return;
        }

        Set<String> generatedTexts = getLibTooltipsGeneratedLineTexts(itemId);
        List<Component> tooltip = event.getToolTip();
        for (int index = tooltip.size() - 1; index > 0; index--) {
            Component line = tooltip.get(index);
            if (isLibTooltipsGenericShiftLine(line) || isLibTooltipsGeneratedLiteralLine(line, generatedTexts)) {
                tooltip.remove(index);
            }
        }
    }

    private static Set<String> getLibTooltipsGeneratedLineTexts(ResourceLocation itemId) {
        Set<String> generatedTexts = new HashSet<>();
        String keyPrefix = "tooltip." + itemId.getNamespace() + "." + itemId.getPath() + ".";
        for (int line = 0; line < LIBTOOLTIPS_MAX_LINES; line++) {
            String key = keyPrefix + line;
            if (!I18n.exists(key)) {
                break;
            }

            String text = I18n.get(key);
            if ("hide".equals(text)) {
                break;
            }

            addWithPossibleLibTooltipsIndent(generatedTexts, text);
        }
        return generatedTexts;
    }

    private static void addWithPossibleLibTooltipsIndent(Set<String> generatedTexts, String text) {
        for (int spaces = 0; spaces <= LIBTOOLTIPS_MAX_SPACES_BEFORE_TOOLTIP; spaces++) {
            generatedTexts.add(" ".repeat(spaces) + text);
        }
    }

    private static boolean isLibTooltipsGenericShiftLine(Component line) {
        if (line.getContents() instanceof TranslatableContents translatable) {
            String key = translatable.getKey();
            return LIBTOOLTIPS_SHIFT_UP_KEY.equals(key) || LIBTOOLTIPS_SHIFT_DOWN_KEY.equals(key);
        }
        return false;
    }

    private static boolean isLibTooltipsGeneratedLiteralLine(Component line, Set<String> generatedTexts) {
        return !generatedTexts.isEmpty()
                && line.getContents() instanceof LiteralContents
                && generatedTexts.contains(line.getString());
    }

    private static boolean isReliceraModNameLine(String text) {
        return ReliceraMod.MOD_ID.equals(text)
                || "Relicera".equals(text);
    }

    private static boolean isReliceraItem(RenderTooltipEvent.GatherComponents event) {
        return isReliceraItem(event.getItemStack());
    }

    private static boolean isReliceraItem(net.minecraft.world.item.ItemStack stack) {
        return stack.getItem() != Items.AIR
                && ForgeRegistries.ITEMS.getKey(stack.getItem()) != null
                && ReliceraMod.MOD_ID.equals(ForgeRegistries.ITEMS.getKey(stack.getItem()).getNamespace());
    }
}
