package com.yukari.relicera.common.event;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.astral.AstralLensDamageProtection;
import com.yukari.relicera.common.astral.AstralObservationData;
import com.yukari.relicera.common.astral.AstralObservationTracker;
import com.yukari.relicera.common.block.DreamcatcherBoxSleepRewards;
import com.yukari.relicera.common.curio.AshenTouchEffects;
import com.yukari.relicera.common.curio.BrutalPlunderBadgeEffects;
import com.yukari.relicera.common.curio.BruteBeltEffects;
import com.yukari.relicera.common.curio.CovenantTabletEffects;
import com.yukari.relicera.common.curio.DivineSeveranceRingEffects;
import com.yukari.relicera.common.curio.FourfoldSherdPendantEffects;
import com.yukari.relicera.common.curio.GranbellsFurnaceEffects;
import com.yukari.relicera.common.curio.IluthiasChaliceEffects;
import com.yukari.relicera.common.curio.IllagerMedalAxeEffects;
import com.yukari.relicera.common.curio.LuminasCelestialLensEffects;
import com.yukari.relicera.common.curio.LittleTailorsBeltEffects;
import com.yukari.relicera.common.curio.MasterSmithsBroochEffects;
import com.yukari.relicera.common.curio.NightGlovesEffects;
import com.yukari.relicera.common.curio.NereiasCrownEffects;
import com.yukari.relicera.common.curio.RippleheartRingEffects;
import com.yukari.relicera.common.curio.StriderSpursEffects;
import com.yukari.relicera.common.curio.ThousandweightGauntletsEffects;
import com.yukari.relicera.common.curio.TreasureHuntersGlovesEffects;
import com.yukari.relicera.common.curio.TurncoatsMedalEffects;
import com.yukari.relicera.common.curio.TwoHandedSwordBroochEffects;
import com.yukari.relicera.common.curio.VindicatorsMedalEffects;
import com.yukari.relicera.common.curio.VerdantScaleBeltEffects;
import com.yukari.relicera.common.curio.WarpCrystalEffects;
import com.yukari.relicera.common.curio.WarriorBeltEffects;
import com.yukari.relicera.common.effect.furnaceofwar.FurnaceOfWarEffects;
import com.yukari.relicera.common.effect.iluthiasblessing.IluthiasBlessingEffects;
import com.yukari.relicera.common.effect.tempestsprint.TempestSprintEffects;
import com.yukari.relicera.common.entity.forgeling.ForgelingWorksiteManager;
import com.yukari.relicera.common.item.AstralStorybookItem;
import com.yukari.relicera.common.item.feysilver.FeysilverForgingKnowledge;
import com.yukari.relicera.common.item.PastoralMelodyEffects;
import com.yukari.relicera.common.item.RottenTuskEffects;
import com.yukari.relicera.common.item.RippleheartPearlEffects;
import com.yukari.relicera.common.item.SolarEmberEffects;
import com.yukari.relicera.common.item.SoulknotStoneEffects;
import com.yukari.relicera.common.item.StonewallGreatshieldItem;
import com.yukari.relicera.common.item.StonewallGreatshieldEffects;
import com.yukari.relicera.common.loot.StormscaleDrops;
import com.yukari.relicera.common.item.TempestsReinsEffects;
import com.yukari.relicera.common.item.armor.devilsbearskin.DevilsBearskinEffects;
import com.yukari.relicera.common.curio.IllagerMedalAdvancements;
import com.yukari.relicera.common.curio.TurncoatPatrolSpawner;
import com.yukari.relicera.common.item.WarfireFragmentAllayEffects;
import com.yukari.relicera.common.loot.WarfireFragmentDrops;
import com.yukari.relicera.common.item.silkofnight.SilkOfNightDecoration;
import com.yukari.relicera.common.item.silkofnight.SilkOfNightEffects;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.TradeWithVillagerEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.DropRulesEvent;

@Mod.EventBusSubscriber(modid = ReliceraMod.MOD_ID)
public final class CommonGameEvents {
    private CommonGameEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (event.player instanceof ServerPlayer serverPlayer) {
            AstralStorybookItem.tickDreamBubbles(serverPlayer);
            DevilsBearskinEffects.tickPlayer(serverPlayer);
            AstralObservationTracker.tick(serverPlayer);
            RottenTuskEffects.repelPiglins(serverPlayer);
            SolarEmberEffects.tick(serverPlayer);
            TreasureHuntersGlovesEffects.tickPlayerLuck(serverPlayer);
            ThousandweightGauntletsEffects.tickAttackSpeed(serverPlayer);
            TwoHandedSwordBroochEffects.tickPlayer(serverPlayer);
            RippleheartRingEffects.tickPlayer(serverPlayer);
        }
        StonewallGreatshieldEffects.clearCooldownWhileHeld(event.player);
        GranbellsFurnaceEffects.tickPlayer(event.player);
        LuminasCelestialLensEffects.tickPlayerFlight(event.player);
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel serverLevel) {
            AshenTouchEffects.clearQueuedFires(serverLevel);
            DreamcatcherBoxSleepRewards.tickLevel(serverLevel);
            PastoralMelodyEffects.tickLevel(serverLevel);
            if (serverLevel.getGameTime() % 20L == 0L) {
                ForgelingWorksiteManager.cleanupInvalidBindings(serverLevel);
            }
            TurncoatPatrolSpawner.tickLevel(serverLevel);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        FurnaceOfWarEffects.processPendingWeaponBurns(event);
        LittleTailorsBeltEffects.processPendingReveals(event);
        DivineSeveranceRingEffects.processPendingJudgments(event);
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        DreamcatcherBoxSleepRewards.clear();
        DevilsBearskinEffects.onServerStopped();
        FurnaceOfWarEffects.clearPendingWeaponBurns();
        LittleTailorsBeltEffects.clearPendingReveals();
        DivineSeveranceRingEffects.clearPendingJudgments();
        TurncoatPatrolSpawner.clear();
    }

    @SubscribeEvent
    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        DreamcatcherBoxSleepRewards.onSleepFinished(event);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        AstralObservationData.copy(event.getOriginal(), event.getEntity());
        FeysilverForgingKnowledge.copy(event.getOriginal(), event.getEntity());
        GranbellsFurnaceEffects.restoreKeptInventory(event);
        DevilsBearskinEffects.handlePlayerClone(event);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            AstralObservationData.sync(serverPlayer);
            FeysilverForgingKnowledge.sync(serverPlayer);
            RippleheartRingEffects.forceSync(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            AstralObservationData.sync(serverPlayer);
            FeysilverForgingKnowledge.sync(serverPlayer);
            RippleheartRingEffects.forceSync(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            AstralObservationData.sync(serverPlayer);
            FeysilverForgingKnowledge.sync(serverPlayer);
            RippleheartRingEffects.forceSync(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            DevilsBearskinEffects.onPlayerLoggedOut(serverPlayer);
            RippleheartRingEffects.forgetPlayer(serverPlayer);
            TreasureHuntersGlovesEffects.forgetPlayer(serverPlayer);
            TurncoatPatrolSpawner.forgetPlayer(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        SoulknotStoneEffects.onEntityJoinLevel(event);
        if (event.getEntity() instanceof ItemEntity itemEntity && itemEntity.getItem().is(ModItems.ASTRAL_LENS.get())) {
            itemEntity.setGlowingTag(true);
        }
    }

    @SubscribeEvent
    public static void onItemExpire(ItemExpireEvent event) {
        if (event.getEntity().getItem().is(ModItems.ASTRAL_LENS.get())) {
            event.setExtraLife(6000);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        StonewallGreatshieldEffects.protectBlocksFromExplosion(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCriticalHit(CriticalHitEvent event) {
        WarriorBeltEffects.increaseVanillaCriticalDamage(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtHighest(LivingHurtEvent event) {
        CovenantTabletEffects.rememberHighDamageCandidate(event);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (TempestsReinsEffects.preventDamage(event)) {
            return;
        }
        if (GranbellsFurnaceEffects.preventFireDamage(event)) {
            return;
        }
        if (IluthiasBlessingEffects.preventDamage(event)) {
            return;
        }
        if (TempestSprintEffects.reduceDamage(event)) {
            return;
        }
        if (NereiasCrownEffects.preventDamage(event)) {
            return;
        }
        if (NereiasCrownEffects.preventAquaticAllyDamage(event)) {
            return;
        }
        NereiasCrownEffects.reduceActiveDamage(event);
        MasterSmithsBroochEffects.reduceDamage(event);
        AstralLensDamageProtection.apply(event);
        NightGlovesEffects.applyNightMeleeDamageBonus(event);
        BrutalPlunderBadgeEffects.applyDamageBonus(event);
        TurncoatsMedalEffects.applyHeroDamageBonus(event);
        AshenTouchEffects.applyMeleeFireEffects(event);
        RippleheartRingEffects.modifyCombatDamage(event);
        FourfoldSherdPendantEffects.applyDamageEffects(event);
        IluthiasChaliceEffects.applyDamageEffects(event);
        NereiasCrownEffects.rememberWearerTarget(event);
        WarfireFragmentAllayEffects.preventAllayDamage(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtLowest(LivingHurtEvent event) {
        BruteBeltEffects.reduceUnarmoredDamage(event);
        RippleheartRingEffects.applyCombatTriggers(event);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LittleTailorsBeltEffects.rememberFailedOneShotCandidate(event);
        CovenantTabletEffects.completeHighDamageTask(event);
        IluthiasChaliceEffects.applyRegeneration(event);
        TurncoatsMedalEffects.applyBadOmenLifeSteal(event);
        IllagerMedalAxeEffects.applyAxeHitEffects(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamageLowest(LivingDamageEvent event) {
        WarpCrystalEffects.preventLethalDamage(event);
        BruteBeltEffects.rechargeAttackAfterDamage(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHeal(LivingHealEvent event) {
        RippleheartRingEffects.applyHealingEffects(event);
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            TwoHandedSwordBroochEffects.tickPlayer(serverPlayer);
        }
        AshenTouchEffects.rememberPreAttackFireState(event);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (LittleTailorsBeltEffects.preventIntimidatedAttack(event)) {
            return;
        }
        FurnaceOfWarEffects.scheduleWeaponBurn(event);
        if (CovenantTabletEffects.preventFallDamage(event)) {
            return;
        }
        if (TempestsReinsEffects.preventDamage(event)) {
            return;
        }
        GranbellsFurnaceEffects.igniteAttacker(event);
        if (GranbellsFurnaceEffects.preventFireDamage(event)) {
            return;
        }
        if (NereiasCrownEffects.preventDamage(event)) {
            return;
        }
        if (NereiasCrownEffects.preventAquaticAllyDamage(event)) {
            return;
        }
        RottenTuskEffects.preventPiglinDamage(event);
        WarfireFragmentAllayEffects.preventAllayAttack(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onLivingAttackLowest(LivingAttackEvent event) {
        WarpCrystalEffects.tryRandomDodge(event);
    }

    @SubscribeEvent
    public static void onLivingSwapHands(LivingSwapItemsEvent.Hands event) {
        TwoHandedSwordBroochEffects.preventHandSwap(event);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        DevilsBearskinEffects.rememberDoomedBearskin(event);
        GranbellsFurnaceEffects.rememberInventoryForFireOrLavaDeath(event);
        IllagerMedalAdvancements.onLivingDeath(event);
        WarfireFragmentDrops.onLivingDeath(event);
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (GranbellsFurnaceEffects.cancelKeptInventoryDrops(event)) {
            return;
        }
        DevilsBearskinEffects.removeDoomedBearskinDrop(event);
        RottenTuskEffects.addZoglinDrop(event);
        BrutalPlunderBadgeEffects.addPiglinBarterDrop(event);
        DivineSeveranceRingEffects.addGlowingUndeadHeadDrop(event);
        StormscaleDrops.addElderGuardianThunderstormDrop(event);
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        FourfoldSherdPendantEffects.applyExperienceBonus(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onSoulknotDrops(LivingDropsEvent event) {
        SoulknotStoneEffects.suppressDrops(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onSoulknotExperienceDrop(LivingExperienceDropEvent event) {
        SoulknotStoneEffects.suppressExperience(event);
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        FourfoldSherdPendantEffects.applyBreakSpeedBonus(event);
        CovenantTabletEffects.applyMiningSpeed(event);
    }

    @SubscribeEvent
    public static void onLivingUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        FourfoldSherdPendantEffects.applyConsumptionBonuses(event);
        DevilsBearskinEffects.applyPostConsumptionNausea(event);
    }

    @SubscribeEvent
    public static void onLivingUseItemStart(LivingEntityUseItemEvent.Start event) {
        PastoralMelodyEffects.shareCooldownWithGoatHorn(event);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        DevilsBearskinEffects.refuseVillagerTrade(event);
        if (event.isCanceled()) {
            return;
        }
        NereiasCrownEffects.takeDrownedHeldItems(event);
        if (event.isCanceled()) {
            return;
        }
        TreasureHuntersGlovesEffects.handleMinecartInteraction(event);
        if (event.isCanceled()) {
            return;
        }
        RippleheartPearlEffects.feedEntity(event);
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        TreasureHuntersGlovesEffects.onContainerOpen(event);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        TreasureHuntersGlovesEffects.refreshLootContainer(event);
    }

    @SubscribeEvent
    public static void onTradeWithVillager(TradeWithVillagerEvent event) {
        CovenantTabletEffects.completeToolsmithTradeTask(event);
    }

    @SubscribeEvent
    public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
        NereiasCrownEffects.redirectAquaticAllyTarget(event);
        LittleTailorsBeltEffects.preventIntimidatedTarget(event);
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        GranbellsFurnaceEffects.preventAnvilDamage(event);
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        FeysilverForgingKnowledge.updateAnvilResult(event);
        SilkOfNightDecoration.updateAnvilResult(event);
        StonewallGreatshieldItem.updateAnvilRepair(event);
    }

    @SubscribeEvent
    public static void onCurioDropRules(DropRulesEvent event) {
        GranbellsFurnaceEffects.keepCuriosForFireOrLavaDeath(event);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LittleTailorsBeltEffects.clearIntimidatedTarget(event.getEntity());
        WarriorBeltEffects.tickAttributes(event.getEntity());
        BruteBeltEffects.tickAttributes(event.getEntity());
        VerdantScaleBeltEffects.tick(event.getEntity());
        FurnaceOfWarEffects.updateGrantedEffect(event.getEntity());
        WarfireFragmentAllayEffects.tickAllayAura(event);
        StriderSpursEffects.tickStrider(event);
        GranbellsFurnaceEffects.tickLavaStanding(event.getEntity());
        FourfoldSherdPendantEffects.tickAttributes(event.getEntity());
        MasterSmithsBroochEffects.tickAttributes(event.getEntity());
        IllagerMedalAxeEffects.tickAttackSpeed(event.getEntity());
        IluthiasChaliceEffects.tickImmunities(event.getEntity());
        LuminasCelestialLensEffects.tickImmunities(event.getEntity());
        NereiasCrownEffects.tick(event);
        TempestsReinsEffects.tickHorse(event);
        TempestSprintEffects.tickHorse(event);
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        FurnaceOfWarEffects.handleProjectileImpact(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingTickLowest(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            DreamcatcherBoxSleepRewards.allowEnigmaticCursedSleep(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onVanillaGameEvent(VanillaGameEvent event) {
        NightGlovesEffects.suppressContainerVibrations(event);
        SilkOfNightEffects.suppressMovementVibrations(event);
    }

    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        LuminasCelestialLensEffects.preventDarknessAndBlindness(event);
        RippleheartRingEffects.transferHarmfulEffect(event);
    }

    @SubscribeEvent
    public static void onLivingConversion(LivingConversionEvent.Post event) {
        DevilsBearskinEffects.rewardVillagerCure(event);
        LittleTailorsBeltEffects.copySeenThrough(event);
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        LittleTailorsBeltEffects.syncSeenThrough(event);
    }

}
