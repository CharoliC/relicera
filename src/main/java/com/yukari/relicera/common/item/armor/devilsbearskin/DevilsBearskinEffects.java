package com.yukari.relicera.common.item.armor.devilsbearskin;

import com.yukari.relicera.registry.ModEffects;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class DevilsBearskinEffects {
    public static final double MAX_HEALTH_LOSS = 0.66D;
    private static final String DATA_TAG = "ReliceraDevilsBearskin";
    private static final String WEAR_TIME_TAG = "WearTime";
    private static final String PRAYERS_TAG = "Prayers";
    private static final String PRAYER_COOLDOWN_UNTIL_TAG = "PrayerCooldownUntil";
    private static final String SOUL_LOST_TAG = "ReliceraDevilsBearskinSoulLost";
    private static final String CURED_BY_PLAYER_TAG = "ReliceraDevilsBearskinCuredBy";
    private static final String VANILLA_CONVERSION_PLAYER_TAG = "ConversionPlayer";

    private static final UUID SOUL_LOSS_MODIFIER_ID = UUID.fromString("7847a121-3f2d-4b29-b66d-ea897593da28");
    private static final String SOUL_LOSS_MODIFIER_NAME = "Devil's Bearskin soul loss";

    private static final long TICKS_PER_DAY = 24_000L;
    private static final long DAYS_PER_LUNAR_CYCLE = 8L;
    private static final long TICKS_PER_LUNAR_CYCLE = TICKS_PER_DAY * DAYS_PER_LUNAR_CYCLE;
    private static final long TRADE_REFUSAL_TIME = TICKS_PER_LUNAR_CYCLE * 3L;
    private static final long NAUSEA_TIME = TICKS_PER_LUNAR_CYCLE * 5L;
    private static final long RELEASE_TIME = TICKS_PER_LUNAR_CYCLE * 7L;

    private static final int PROGRESS_WRITE_INTERVAL = 20;
    private static final int WATER_DAMAGE_INTERVAL = 40;
    private static final float WATER_DAMAGE = 8.0F;
    private static final int NAUSEA_DURATION = 10 * 20;
    private static final int PRAYER_BLESSING_DURATION = 10 * 20;
    private static final float PRAYER_HEALTH_THRESHOLD = 0.30F;

    private static final Map<UUID, WearTracker> WEAR_TRACKERS = new HashMap<>();
    private static final Map<UUID, ItemStack> DOOMED_ON_DEATH = new HashMap<>();

    private DevilsBearskinEffects() {
    }

    public static void tickPlayer(ServerPlayer player) {
        applyPermanentSoulLoss(player);

        UUID playerId = player.getUUID();
        if (player.isAlive()) {
            DOOMED_ON_DEATH.remove(playerId);
        }

        ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!isBearskin(worn)) {
            flushAndRemoveTracker(playerId);
            return;
        }

        long gameTime = player.level().getGameTime();
        long dayTime = player.level().getDayTime();
        WearTracker tracker = WEAR_TRACKERS.get(playerId);
        if (tracker == null || tracker.stack != worn) {
            flushAndRemoveTracker(playerId);
            tracker = new WearTracker(worn, gameTime, dayTime);
            WEAR_TRACKERS.put(playerId, tracker);
        } else if (!isReleased(worn)) {
            tracker.recordTime(gameTime, dayTime);
        } else {
            tracker.updateSample(gameTime, dayTime);
        }

        if (!isReleased(worn)
                && (tracker.pendingWearTime >= PROGRESS_WRITE_INTERVAL
                || getWearTime(worn) + tracker.pendingWearTime >= RELEASE_TIME)) {
            tracker.flushProgress();
        }

        if (isReleased(worn)) {
            tracker.wetTicks = 0;
            return;
        }

        tryConsumePrayer(player, worn, gameTime);

        if (player.isInWaterRainOrBubble()) {
            tracker.wetTicks++;
            if (tracker.wetTicks >= WATER_DAMAGE_INTERVAL) {
                tracker.wetTicks = 0;
                player.hurt(player.damageSources().magic(), WATER_DAMAGE);
            }
        } else {
            tracker.wetTicks = 0;
        }
    }

    public static boolean isLocked(ItemStack stack) {
        return isBearskin(stack) && !isReleased(stack);
    }

    public static boolean canExtractGold(ItemStack stack, Player player) {
        if (!isBearskin(stack)) {
            return false;
        }
        return isReleased(stack) || player.getItemBySlot(EquipmentSlot.CHEST) == stack;
    }

    public static boolean canExchangeWealth(ItemStack stack, Player player) {
        return canExtractGold(stack, player);
    }

    public static boolean shouldEmitFlies(Player player) {
        return hasActiveStage(player, TRADE_REFUSAL_TIME);
    }

    public static boolean isReleased(ItemStack stack) {
        return isBearskin(stack) && getWearTime(stack) >= RELEASE_TIME;
    }

    public static boolean hasReachedTradeRefusalStage(ItemStack stack) {
        return isBearskin(stack) && getWearTime(stack) >= TRADE_REFUSAL_TIME;
    }

    public static boolean hasReachedNauseaStage(ItemStack stack) {
        return isBearskin(stack) && getWearTime(stack) >= NAUSEA_TIME;
    }

    public static void refuseVillagerTrade(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Villager villager)
                || !hasActiveStage(event.getEntity(), TRADE_REFUSAL_TIME)) {
            return;
        }

        if (wasCuredBy(villager, event.getEntity())) {
            return;
        }

        VillagerProfession profession = villager.getVillagerData().getProfession();
        if (profession == VillagerProfession.NONE || profession == VillagerProfession.NITWIT) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
        if (!event.getLevel().isClientSide()) {
            villager.setUnhappyCounter(40);
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.ANGRY_VILLAGER,
                        villager.getX(),
                        villager.getY() + villager.getBbHeight() + 0.25D,
                        villager.getZ(),
                        3,
                        0.22D,
                        0.12D,
                        0.22D,
                        0.0D
                );
            }
            event.getLevel().playSound(null, villager.getX(), villager.getY(), villager.getZ(),
                    SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }

    public static void applyPostConsumptionNausea(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide()
                || !hasActiveStage(player, NAUSEA_TIME)) {
            return;
        }

        UseAnim animation = event.getItem().getUseAnimation();
        if (animation == UseAnim.EAT || animation == UseAnim.DRINK) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, NAUSEA_DURATION, 0));
        }
    }

    public static void rewardVillagerCure(LivingConversionEvent.Post event) {
        if (!(event.getEntity() instanceof ZombieVillager zombieVillager)
                || !(event.getOutcome() instanceof Villager villager)
                || !(villager.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        CompoundTag conversionData = zombieVillager.saveWithoutId(new CompoundTag());
        if (!conversionData.hasUUID(VANILLA_CONVERSION_PLAYER_TAG)) {
            return;
        }

        UUID healerId = conversionData.getUUID(VANILLA_CONVERSION_PLAYER_TAG);
        Player healer = serverLevel.getPlayerByUUID(healerId);
        if (!(healer instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ItemStack worn = serverPlayer.getItemBySlot(EquipmentSlot.CHEST);
        if (!isLocked(worn)) {
            return;
        }

        addPrayer(worn);
        villager.getPersistentData().putUUID(CURED_BY_PLAYER_TAG, healerId);
    }

    public static void rememberDoomedBearskin(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        flushAndRemoveTracker(player.getUUID());
        ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);
        if (isLocked(worn)) {
            DOOMED_ON_DEATH.put(player.getUUID(), worn.copy());
        }
    }

    public static void removeDoomedBearskinDrop(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack doomed = DOOMED_ON_DEATH.get(player.getUUID());
        if (doomed == null) {
            return;
        }

        Iterator<ItemEntity> drops = event.getDrops().iterator();
        while (drops.hasNext()) {
            ItemEntity drop = drops.next();
            if (ItemStack.isSameItemSameTags(drop.getItem(), doomed)) {
                drops.remove();
                return;
            }
        }
    }

    public static void handlePlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        boolean soulAlreadyLost = event.getOriginal().getPersistentData().getBoolean(SOUL_LOST_TAG)
                || hasSoulLossModifier(event.getOriginal());
        if (soulAlreadyLost) {
            player.getPersistentData().putBoolean(SOUL_LOST_TAG, true);
        }

        if (event.isWasDeath()) {
            ItemStack doomed = DOOMED_ON_DEATH.remove(event.getOriginal().getUUID());
            if (doomed != null) {
                removeMatchingBearskin(player, doomed);
                player.getPersistentData().putBoolean(SOUL_LOST_TAG, true);
            }
        }

        applyPermanentSoulLoss(player);
    }

    public static void onPlayerLoggedOut(ServerPlayer player) {
        flushAndRemoveTracker(player.getUUID());
    }

    public static void onServerStopped() {
        WEAR_TRACKERS.values().forEach(WearTracker::flushProgress);
        WEAR_TRACKERS.clear();
        DOOMED_ON_DEATH.clear();
    }

    private static void removeMatchingBearskin(ServerPlayer player, ItemStack doomed) {
        ItemStack currentChest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (ItemStack.isSameItemSameTags(currentChest, doomed)) {
            player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
            return;
        }

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack candidate = player.getInventory().getItem(slot);
            if (ItemStack.isSameItemSameTags(candidate, doomed)) {
                player.getInventory().setItem(slot, ItemStack.EMPTY);
                return;
            }
        }
    }

    private static boolean wasCuredBy(Villager villager, Player player) {
        CompoundTag data = villager.getPersistentData();
        return data.hasUUID(CURED_BY_PLAYER_TAG)
                && data.getUUID(CURED_BY_PLAYER_TAG).equals(player.getUUID());
    }

    private static void addPrayer(ItemStack stack) {
        CompoundTag data = getOrCreateBearskinData(stack);
        int prayers = Math.max(0, data.getInt(PRAYERS_TAG));
        if (prayers < Integer.MAX_VALUE) {
            data.putInt(PRAYERS_TAG, prayers + 1);
        }
    }

    private static void tryConsumePrayer(ServerPlayer player, ItemStack worn, long gameTime) {
        if (!player.isAlive()
                || player.getHealth() <= 0.0F
                || player.getHealth() >= player.getMaxHealth() * PRAYER_HEALTH_THRESHOLD) {
            return;
        }

        CompoundTag root = worn.getTag();
        if (root == null || !root.contains(DATA_TAG, Tag.TAG_COMPOUND)) {
            return;
        }

        CompoundTag data = root.getCompound(DATA_TAG);
        int prayers = Math.max(0, data.getInt(PRAYERS_TAG));
        if (prayers <= 0 || gameTime < data.getLong(PRAYER_COOLDOWN_UNTIL_TAG)) {
            return;
        }

        data.putInt(PRAYERS_TAG, prayers - 1);
        data.putLong(PRAYER_COOLDOWN_UNTIL_TAG, gameTime + PRAYER_BLESSING_DURATION);
        root.put(DATA_TAG, data);
        player.addEffect(new MobEffectInstance(
                ModEffects.ILUTHIAS_BLESSING.get(),
                PRAYER_BLESSING_DURATION,
                0
        ));
    }

    public static void restoreSoul(ServerPlayer player) {
        player.getPersistentData().remove(SOUL_LOST_TAG);
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.removeModifier(SOUL_LOSS_MODIFIER_ID);
        }
    }

    private static boolean hasSoulLossModifier(Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        return maxHealth != null && maxHealth.getModifier(SOUL_LOSS_MODIFIER_ID) != null;
    }

    private static void applyPermanentSoulLoss(ServerPlayer player) {
        if (!player.getPersistentData().getBoolean(SOUL_LOST_TAG)) {
            return;
        }

        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }

        double amount = -MAX_HEALTH_LOSS;
        AttributeModifier existing = maxHealth.getModifier(SOUL_LOSS_MODIFIER_ID);
        if (existing != null
                && (existing.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL
                || Math.abs(existing.getAmount() - amount) > 0.0001D)) {
            maxHealth.removeModifier(SOUL_LOSS_MODIFIER_ID);
            existing = null;
        }

        if (existing == null) {
            maxHealth.addPermanentModifier(new AttributeModifier(
                    SOUL_LOSS_MODIFIER_ID,
                    SOUL_LOSS_MODIFIER_NAME,
                    amount,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private static boolean hasActiveStage(Player player, long requiredWearTime) {
        ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!isBearskin(worn) || isReleased(worn)) {
            return false;
        }

        long wearTime = getWearTime(worn);
        WearTracker tracker = player.level().isClientSide() ? null : WEAR_TRACKERS.get(player.getUUID());
        if (tracker != null && tracker.stack == worn) {
            wearTime = Math.min(RELEASE_TIME, wearTime + tracker.pendingWearTime);
        }
        return wearTime >= requiredWearTime;
    }

    private static boolean isBearskin(ItemStack stack) {
        return !stack.isEmpty()
                && ModItems.DEVILS_BEARSKIN.isPresent()
                && stack.is(ModItems.DEVILS_BEARSKIN.get());
    }

    private static long getWearTime(ItemStack stack) {
        CompoundTag root = stack.getTag();
        if (root == null || !root.contains(DATA_TAG, Tag.TAG_COMPOUND)) {
            return 0L;
        }
        return Math.min(RELEASE_TIME, Math.max(0L, root.getCompound(DATA_TAG).getLong(WEAR_TIME_TAG)));
    }

    private static void setWearTime(ItemStack stack, long wearTime) {
        CompoundTag root = stack.getOrCreateTag();
        CompoundTag data = getOrCreateBearskinData(stack);
        data.putLong(WEAR_TIME_TAG, Math.min(RELEASE_TIME, Math.max(0L, wearTime)));
        root.put(DATA_TAG, data);
    }

    private static CompoundTag getOrCreateBearskinData(ItemStack stack) {
        CompoundTag root = stack.getOrCreateTag();
        CompoundTag data = root.contains(DATA_TAG, Tag.TAG_COMPOUND)
                ? root.getCompound(DATA_TAG)
                : new CompoundTag();
        root.put(DATA_TAG, data);
        return data;
    }

    private static void flushAndRemoveTracker(UUID playerId) {
        WearTracker tracker = WEAR_TRACKERS.remove(playerId);
        if (tracker != null) {
            tracker.flushProgress();
        }
    }

    private static final class WearTracker {
        private final ItemStack stack;
        private long lastGameTime;
        private long lastDayTime;
        private long pendingWearTime;
        private int wetTicks;

        private WearTracker(ItemStack stack, long gameTime, long dayTime) {
            this.stack = stack;
            this.lastGameTime = gameTime;
            this.lastDayTime = dayTime;
        }

        private void recordTime(long gameTime, long dayTime) {
            if (gameTime == lastGameTime + 1L && dayTime > lastDayTime) {
                pendingWearTime += Math.min(RELEASE_TIME - pendingWearTime, 1L);
            }
            updateSample(gameTime, dayTime);
        }

        private void updateSample(long gameTime, long dayTime) {
            lastGameTime = gameTime;
            lastDayTime = dayTime;
        }

        private void flushProgress() {
            if (pendingWearTime <= 0L || !isBearskin(stack)) {
                pendingWearTime = 0L;
                return;
            }
            setWearTime(stack, getWearTime(stack) + pendingWearTime);
            pendingWearTime = 0L;
        }
    }
}
