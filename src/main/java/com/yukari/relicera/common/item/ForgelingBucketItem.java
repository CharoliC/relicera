package com.yukari.relicera.common.item;

import com.yukari.relicera.common.entity.forgeling.Forgeling;
import com.yukari.relicera.registry.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public final class ForgelingBucketItem extends Item {
    private static final double SIDE_CLEARANCE = 0.03D;

    public ForgelingBucketItem(Properties properties) {
        super(properties);
    }

    public static boolean isSilvered(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(Forgeling.TAG_SILVERED);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }

        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        BlockState clickedState = level.getBlockState(clickedPos);
        boolean useClickedPosition = clickedState.getCollisionShape(level, clickedPos).isEmpty();
        BlockPos spawnPos = useClickedPosition ? clickedPos : clickedPos.relative(clickedFace);

        Forgeling forgeling = ModEntityTypes.FORGELING.get().create(serverLevel);
        if (forgeling == null) {
            return InteractionResult.FAIL;
        }

        double horizontalClearance = useClickedPosition ? 0.0D : SIDE_CLEARANCE;
        double spawnX = spawnPos.getX() + 0.5D + clickedFace.getStepX() * horizontalClearance;
        double spawnZ = spawnPos.getZ() + 0.5D + clickedFace.getStepZ() * horizontalClearance;
        float yaw = serverLevel.random.nextFloat() * 360.0F;
        forgeling.moveTo(spawnX, spawnPos.getY(), spawnZ, yaw, 0.0F);
        forgeling.yHeadRot = yaw;
        forgeling.yBodyRot = yaw;
        if (!serverLevel.noCollision(forgeling)) {
            return InteractionResult.FAIL;
        }

        forgeling.finalizeSpawn(
                serverLevel,
                serverLevel.getCurrentDifficultyAt(forgeling.blockPosition()),
                MobSpawnType.BUCKET,
                null,
                null
        );
        ItemStack bucketStack = context.getItemInHand();
        CompoundTag bucketTag = bucketStack.getTag();
        if (bucketTag != null) {
            forgeling.loadFromBucketTag(bucketTag);
        }
        if (bucketStack.hasCustomHoverName()) {
            forgeling.setCustomName(bucketStack.getHoverName());
        }
        forgeling.setFromBucket(true);

        if (!serverLevel.addFreshEntity(forgeling)) {
            return InteractionResult.FAIL;
        }

        serverLevel.playSound(
                null,
                forgeling.blockPosition(),
                SoundEvents.BUCKET_EMPTY_LAVA,
                SoundSource.NEUTRAL,
                1.0F,
                1.0F
        );
        serverLevel.gameEvent(player, GameEvent.ENTITY_PLACE, forgeling.position());
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            player.setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
        }
        return InteractionResult.CONSUME;
    }
}
