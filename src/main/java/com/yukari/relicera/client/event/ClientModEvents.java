package com.yukari.relicera.client.event;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.client.model.BeltModel;
import com.yukari.relicera.client.model.WarpedFungusBottleModel;
import com.yukari.relicera.client.model.BruteBeltModel;
import com.yukari.relicera.client.model.DevilsBearskinModel;
import com.yukari.relicera.client.model.SurfaceGlovesModel;
import com.yukari.relicera.client.model.TreasureHuntersGlovesModel;
import com.yukari.relicera.client.renderer.LuminasMoonRenderer;
import com.yukari.relicera.client.renderer.BruteBeltRenderer;
import com.yukari.relicera.client.renderer.BeltRenderer;
import com.yukari.relicera.client.renderer.WarpedFungusBottleRenderer;
import com.yukari.relicera.client.renderer.ForgelingRenderer;
import com.yukari.relicera.client.particle.ElectricSparkParticle;
import com.yukari.relicera.client.particle.DreamBubbleParticle;
import com.yukari.relicera.client.particle.FlyParticle;
import com.yukari.relicera.client.particle.GoldHeartParticle;
import com.yukari.relicera.client.particle.HolyLightParticle;
import com.yukari.relicera.client.particle.LichFireParticle;
import com.yukari.relicera.client.renderer.TempestSprintHorseLayer;
import com.yukari.relicera.client.renderer.SurfaceGlovesRenderer;
import com.yukari.relicera.client.renderer.TreasureHuntersGlovesRenderer;
import com.yukari.relicera.client.screen.FourfoldSherdPendantScreen;
import com.yukari.relicera.client.renderer.RelicRepairTableRenderer;
import com.yukari.relicera.client.screen.RelicRepairTableScreen;
import com.yukari.relicera.client.tooltip.ClientFiveSlotContentsTooltip;
import com.yukari.relicera.common.curio.CovenantTabletEffects;
import com.yukari.relicera.common.item.AstralStorybookItem;
import com.yukari.relicera.common.item.InscribedCovenantTabletItem;
import com.yukari.relicera.common.item.IluthiasChaliceItem.TotemContentsTooltip;
import com.yukari.relicera.common.item.BaromsCovenantStoneItem.ContractContentsTooltip;
import com.yukari.relicera.registry.ModBlockEntities;
import com.yukari.relicera.registry.ModItems;
import com.yukari.relicera.registry.ModEntityTypes;
import com.yukari.relicera.registry.ModMenuTypes;
import com.yukari.relicera.registry.ModParticleTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod.EventBusSubscriber(modid = ReliceraMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.RELIC_REPAIR_TABLE.get(), RelicRepairTableScreen::new);
            MenuScreens.register(ModMenuTypes.FOURFOLD_SHERD_PENDANT.get(), FourfoldSherdPendantScreen::new);
            ItemProperties.register(ModItems.PASTORAL_MELODY.get(), ResourceLocation.fromNamespaceAndPath("minecraft", "tooting"),
                    (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.COVENANT_TABLET.get(), ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "completed"),
                    (stack, level, entity, seed) -> CovenantTabletEffects.isFullyUnlocked(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.INSCRIBED_COVENANT_TABLET.get(),
                    ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "contract_entity"),
                    (stack, level, entity, seed) -> InscribedCovenantTabletItem.getModelIndex(stack));
            ItemProperties.register(ModItems.STONEWALL_GREATSHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"),
                    (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.ASTRAL_STORYBOOK.get(), ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "story"),
                    (stack, level, entity, seed) -> {
                        int storyId = AstralStorybookItem.getStoryId(stack);
                        return AstralStorybookItem.isKnownStory(storyId) ? storyId : 0.0F;
                    });
            CuriosRendererRegistry.register(
                    ModItems.TREASURE_HUNTERS_GLOVES.get(), TreasureHuntersGlovesRenderer::new);
            CuriosRendererRegistry.register(
                    ModItems.LEATHER_GLOVES.get(), SurfaceGlovesRenderer::leatherGloves);
            CuriosRendererRegistry.register(
                    ModItems.NIGHT_GLOVES.get(), SurfaceGlovesRenderer::nightGloves);
            CuriosRendererRegistry.register(
                    ModItems.THOUSANDWEIGHT_GAUNTLETS.get(), SurfaceGlovesRenderer::thousandweightGauntlets);
            CuriosRendererRegistry.register(ModItems.BRUTE_BELT.get(), BruteBeltRenderer::new);
            CuriosRendererRegistry.register(ModItems.WARPED_FUNGUS_BOTTLE.get(), WarpedFungusBottleRenderer::new);
            CuriosRendererRegistry.register(ModItems.WARRIOR_BELT.get(), BeltRenderer::warriorBelt);
            CuriosRendererRegistry.register(ModItems.LITTLE_TAILORS_BELT.get(), BeltRenderer::littleTailorsBelt);
            if (ModItems.VERDANT_SCALE_BELT.isPresent()) {
                CuriosRendererRegistry.register(ModItems.VERDANT_SCALE_BELT.get(), BeltRenderer::verdantScaleBelt);
            }
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.RELIC_REPAIR_TABLE.get(), RelicRepairTableRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.FORGELING.get(), ForgelingRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BruteBeltModel.LAYER_LOCATION, BruteBeltModel::createBodyLayer);
        event.registerLayerDefinition(WarpedFungusBottleModel.LAYER_LOCATION, WarpedFungusBottleModel::createBodyLayer);
        event.registerLayerDefinition(BeltModel.WARRIOR_LAYER_LOCATION, BeltModel::createWarriorBodyLayer);
        event.registerLayerDefinition(BeltModel.LITTLE_TAILORS_LAYER_LOCATION, BeltModel::createLittleTailorsBodyLayer);
        if (ModItems.VERDANT_SCALE_BELT.isPresent()) {
            event.registerLayerDefinition(BeltModel.VERDANT_SCALE_LAYER_LOCATION, BeltModel::createVerdantScaleBodyLayer);
        }
        event.registerLayerDefinition(DevilsBearskinModel.LAYER_LOCATION, DevilsBearskinModel::createBodyLayer);
        event.registerLayerDefinition(
                TreasureHuntersGlovesModel.WIDE_LAYER_LOCATION, TreasureHuntersGlovesModel::createWideBodyLayer);
        event.registerLayerDefinition(
                TreasureHuntersGlovesModel.SLIM_LAYER_LOCATION, TreasureHuntersGlovesModel::createSlimBodyLayer);
        event.registerLayerDefinition(
                SurfaceGlovesModel.LEATHER_WIDE_LAYER_LOCATION, SurfaceGlovesModel::createLeatherWideBodyLayer);
        event.registerLayerDefinition(
                SurfaceGlovesModel.LEATHER_SLIM_LAYER_LOCATION, SurfaceGlovesModel::createLeatherSlimBodyLayer);
        event.registerLayerDefinition(
                SurfaceGlovesModel.NIGHT_WIDE_LAYER_LOCATION, SurfaceGlovesModel::createNightWideBodyLayer);
        event.registerLayerDefinition(
                SurfaceGlovesModel.NIGHT_SLIM_LAYER_LOCATION, SurfaceGlovesModel::createNightSlimBodyLayer);
        event.registerLayerDefinition(
                SurfaceGlovesModel.THOUSANDWEIGHT_WIDE_LAYER_LOCATION,
                SurfaceGlovesModel::createThousandweightWideBodyLayer);
        event.registerLayerDefinition(
                SurfaceGlovesModel.THOUSANDWEIGHT_SLIM_LAYER_LOCATION,
                SurfaceGlovesModel::createThousandweightSlimBodyLayer);
    }

    @SubscribeEvent
    public static void addEntityLayers(EntityRenderersEvent.AddLayers event) {
        if (event.getRenderer(EntityType.HORSE) instanceof HorseRenderer renderer) {
            renderer.addLayer(new TempestSprintHorseLayer(renderer, event.getEntityModels()));
        }
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(LuminasMoonRenderer.MOON_MODEL);
    }

    @SubscribeEvent
    public static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(TotemContentsTooltip.class, tooltip -> new ClientFiveSlotContentsTooltip(
                tooltip.totems(), tooltip.selectedSlot(), ResourceLocation.fromNamespaceAndPath(
                        ReliceraMod.MOD_ID, "textures/gui/iluthias_chalice.png")));
        event.register(ContractContentsTooltip.class, tooltip -> new ClientFiveSlotContentsTooltip(
                tooltip.contracts(), tooltip.selectedSlot(), ResourceLocation.fromNamespaceAndPath(
                        ReliceraMod.MOD_ID, "textures/gui/baroms_covenant_stone.png")));
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.ELECTRIC_SPARK.get(), ElectricSparkParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.GOLDHEART_0.get(), sprites -> new GoldHeartParticle.Provider(sprites, 1.1F));
        event.registerSpriteSet(ModParticleTypes.GOLDHEART_1.get(), sprites -> new GoldHeartParticle.Provider(sprites, 1.35F));
        event.registerSpriteSet(ModParticleTypes.GOLDHEART_2.get(), sprites -> new GoldHeartParticle.Provider(sprites, 1.65F));
        event.registerSpriteSet(ModParticleTypes.FLY.get(), FlyParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.DREAM_BUBBLE.get(), DreamBubbleParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.HOLY_LIGHT.get(), HolyLightParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.LICH_FIRE.get(), LichFireParticle.Provider::new);
    }
}
