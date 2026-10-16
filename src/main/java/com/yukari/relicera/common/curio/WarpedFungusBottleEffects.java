package com.yukari.relicera.common.curio;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.hoglin.HoglinAi;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingMakeBrainEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = ReliceraMod.MOD_ID)
public final class WarpedFungusBottleEffects {
    private static final double REPEL_RANGE = 8.0D;
    private static final double TEMPT_RANGE = 10.0D;
    private static final int SEARCH_INTERVAL = 10;

    private WarpedFungusBottleEffects() {
    }

    public static boolean isEquipped(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).resolve()
                .map(handler -> handler.isEquipped(ModItems.WARPED_FUNGUS_BOTTLE.get()))
                .orElse(false);
    }

    private static boolean isValidWearer(LivingEntity mob, LivingEntity wearer, double range) {
        return wearer != null && wearer != mob && wearer.isAlive() && !wearer.isSpectator()
                && wearer.level() == mob.level() && mob.distanceToSqr(wearer) <= range * range
                && isEquipped(wearer);
    }

    private static LivingEntity findNearestWearer(LivingEntity mob, double range) {
        return mob.level().getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(range),
                        wearer -> isValidWearer(mob, wearer, range))
                .stream().min(Comparator.comparingDouble(mob::distanceToSqr)).orElse(null);
    }

    @SubscribeEvent
    public static void onMakeBrain(LivingMakeBrainEvent event) {
        if (event.getEntity() instanceof Hoglin hoglin) {
            event.getTypedBrainBuilder(hoglin).addBehaviorToActivityByPriority(
                    -1, Activity.CORE, new BottleRepellentBehavior());
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof Strider strider
                && strider.goalSelector.getAvailableGoals().stream()
                .noneMatch(goal -> goal.getGoal() instanceof FollowBottleGoal)) {
            strider.goalSelector.addGoal(3, new FollowBottleGoal(strider));
        }
    }

    private static final class BottleRepellentBehavior extends OneShot<Hoglin> {
        private LivingEntity wearer;
        private BlockPos injectedRepellent;
        private BlockPos blockRepellent;
        private long nextSearch;

        @Override
        public boolean trigger(ServerLevel level, Hoglin hoglin, long gameTime) {
            BlockPos current = hoglin.getBrain().getMemory(MemoryModuleType.NEAREST_REPELLENT).orElse(null);
            boolean ownsMemory = injectedRepellent != null && current == injectedRepellent;
            if (!ownsMemory) {
                blockRepellent = current;
            }
            if (gameTime >= nextSearch) {
                nextSearch = gameTime + SEARCH_INTERVAL;
                wearer = findNearestWearer(hoglin, REPEL_RANGE);
            }
            if (!isValidWearer(hoglin, wearer, REPEL_RANGE)) {
                wearer = null;
            }
            if (wearer == null && !ownsMemory) {
                injectedRepellent = null;
                return false;
            }
            if (!isValidBlockRepellent(level, hoglin, blockRepellent)) {
                blockRepellent = null;
            }
            BlockPos bottlePos = wearer == null ? null : wearer.blockPosition();
            if (bottlePos != null && (blockRepellent == null
                    || hoglin.blockPosition().distSqr(bottlePos) < hoglin.blockPosition().distSqr(blockRepellent))) {
                injectedRepellent = bottlePos;
                hoglin.getBrain().setMemory(MemoryModuleType.NEAREST_REPELLENT, injectedRepellent);
            } else {
                injectedRepellent = null;
                hoglin.getBrain().setMemory(MemoryModuleType.NEAREST_REPELLENT, Optional.ofNullable(blockRepellent));
            }
            return true;
        }

        private static boolean isValidBlockRepellent(ServerLevel level, Hoglin hoglin, BlockPos pos) {
            if (pos == null) {
                return false;
            }
            BlockPos origin = hoglin.blockPosition();
            return Math.abs(pos.getX() - origin.getX()) <= HoglinAi.REPELLENT_DETECTION_RANGE_HORIZONTAL
                    && Math.abs(pos.getY() - origin.getY()) <= HoglinAi.REPELLENT_DETECTION_RANGE_VERTICAL
                    && Math.abs(pos.getZ() - origin.getZ()) <= HoglinAi.REPELLENT_DETECTION_RANGE_HORIZONTAL
                    && level.hasChunkAt(pos) && level.getBlockState(pos).is(BlockTags.HOGLIN_REPELLENTS);
        }
    }

    private static final class FollowBottleGoal extends Goal {
        private final Strider strider;
        private LivingEntity wearer;
        private long nextSearch;
        private long nextPathUpdate;

        private FollowBottleGoal(Strider strider) {
            this.strider = strider;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            long gameTime = strider.level().getGameTime();
            if (strider.isVehicle() || gameTime < nextSearch) {
                return false;
            }
            nextSearch = gameTime + SEARCH_INTERVAL;
            wearer = findNearestWearer(strider, TEMPT_RANGE);
            return wearer != null;
        }

        @Override
        public boolean canContinueToUse() {
            return !strider.isVehicle() && isValidWearer(strider, wearer, TEMPT_RANGE);
        }

        @Override
        public void start() {
            nextPathUpdate = 0L;
        }

        @Override
        public void stop() {
            wearer = null;
            strider.getNavigation().stop();
            nextSearch = strider.level().getGameTime() + 100L;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            strider.getLookControl().setLookAt(wearer,
                    strider.getMaxHeadYRot() + 20.0F, strider.getMaxHeadXRot());
            if (strider.distanceToSqr(wearer) < 6.25D) {
                strider.getNavigation().stop();
                nextPathUpdate = 0L;
            } else if (strider.level().getGameTime() >= nextPathUpdate) {
                nextPathUpdate = strider.level().getGameTime() + SEARCH_INTERVAL;
                strider.getNavigation().moveTo(wearer, 1.4D);
            }
        }
    }
}
