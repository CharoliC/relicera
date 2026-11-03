package com.yukari.relicera.common.entity.forgeling;

import com.yukari.relicera.registry.ModItems;
import com.yukari.relicera.common.item.SilveringSolutionEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;

import java.util.EnumSet;

import javax.annotation.Nullable;

public final class Forgeling extends PathfinderMob implements Bucketable {
    private static final int IDLE_ANIMATION_NONE = 0;
    private static final int IDLE_ANIMATION_STRETCH = 1;
    private static final int STRETCH_ANIMATION_TICKS = 20;
    private static final int IDLE_SWAY_PERIOD_TICKS = 80;
    private static final EntityDataAccessor<Boolean> WORK_IDLE = SynchedEntityData.defineId(
            Forgeling.class,
            EntityDataSerializers.BOOLEAN
    );
    private static final EntityDataAccessor<Integer> IDLE_ANIMATION = SynchedEntityData.defineId(
            Forgeling.class,
            EntityDataSerializers.INT
    );
    private static final EntityDataAccessor<Integer> IDLE_ANIMATION_START_TIME = SynchedEntityData.defineId(
            Forgeling.class,
            EntityDataSerializers.INT
    );
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(
            Forgeling.class,
            EntityDataSerializers.BOOLEAN
    );
    private static final EntityDataAccessor<Boolean> SILVERED = SynchedEntityData.defineId(
            Forgeling.class,
            EntityDataSerializers.BOOLEAN
    );
    public static final String TAG_SILVERED = "Silvered";
    private static final String TAG_BOUND_TABLE = "BoundSmithingTable";
    private static final String TAG_BOUND_WORK_POSITION = "BoundWorkPosition";
    private static final String TAG_FROM_BUCKET = "FromBucket";
    private static final String TAG_BUCKET_HEALTH = "Health";
    private static final float LANDING_PARTICLE_DIAMETER = 2.0F;

    private float targetSquish;
    private float squish;
    private float oldSquish;
    private boolean wasOnGround;
    private boolean wasWorkIdleForAnimation;
    private int idleAnimationCooldown;
    private long silveringCompletionTime = -1L;
    @Nullable
    private BlockPos boundTablePos;
    @Nullable
    private BlockPos boundWorkPos;

    public Forgeling(EntityType<? extends Forgeling> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new ForgelingMoveControl(this);
        this.xpReward = 4;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new ForgelingFloatGoal(this));
        this.goalSelector.addGoal(2, new ForgelingPanicGoal(this));
        this.goalSelector.addGoal(3, new ForgelingSmithingGoal(this));
        this.goalSelector.addGoal(4, new ForgelingRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new ForgelingKeepOnJumpingGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WORK_IDLE, false);
        this.entityData.define(IDLE_ANIMATION, IDLE_ANIMATION_NONE);
        this.entityData.define(IDLE_ANIMATION_START_TIME, 0);
        this.entityData.define(FROM_BUCKET, false);
        this.entityData.define(SILVERED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.boundTablePos != null && this.boundWorkPos != null) {
            tag.putLong(TAG_BOUND_TABLE, this.boundTablePos.asLong());
            tag.putLong(TAG_BOUND_WORK_POSITION, this.boundWorkPos.asLong());
        }
        tag.putBoolean(TAG_FROM_BUCKET, this.fromBucket());
        tag.putBoolean(TAG_SILVERED, this.isSilvered());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_BOUND_TABLE, Tag.TAG_LONG)
                && tag.contains(TAG_BOUND_WORK_POSITION, Tag.TAG_LONG)) {
            this.boundTablePos = BlockPos.of(tag.getLong(TAG_BOUND_TABLE));
            this.boundWorkPos = BlockPos.of(tag.getLong(TAG_BOUND_WORK_POSITION));
        } else {
            this.clearWorksite();
        }
        this.setFromBucket(tag.getBoolean(TAG_FROM_BUCKET));
        this.entityData.set(SILVERED, tag.getBoolean(TAG_SILVERED));
        this.silveringCompletionTime = -1L;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return this.boundTablePos != null || this.fromBucket() || super.requiresCustomPersistence();
    }

    @Override
    public void remove(RemovalReason reason) {
        this.silveringCompletionTime = -1L;
        if (reason != RemovalReason.UNLOADED_TO_CHUNK && reason != RemovalReason.UNLOADED_WITH_PLAYER) {
            ForgelingWorksiteManager.releaseBinding(this);
        }
        super.remove(reason);
    }

    @Nullable
    BlockPos getBoundTablePos() {
        return this.boundTablePos;
    }

    @Nullable
    BlockPos getBoundWorkPos() {
        return this.boundWorkPos;
    }

    void setWorksite(BlockPos tablePos, BlockPos workPos) {
        this.boundTablePos = tablePos.immutable();
        this.boundWorkPos = workPos.immutable();
    }

    void clearWorksite() {
        this.boundTablePos = null;
        this.boundWorkPos = null;
        this.setWorkIdle(false);
    }

    void setWorkIdle(boolean workIdle) {
        this.entityData.set(WORK_IDLE, workIdle);
    }

    private boolean isWorkIdle() {
        return this.entityData.get(WORK_IDLE);
    }

    public float getIdleSway(float partialTick) {
        if (!this.isWorkIdle() || !this.onGround()) {
            return 0.0F;
        }

        float damping = 1.0F;
        if (this.entityData.get(IDLE_ANIMATION) == IDLE_ANIMATION_STRETCH) {
            float stretchProgress = getIdleAnimationProgress(partialTick, STRETCH_ANIMATION_TICKS);
            damping -= Mth.sin(stretchProgress * Mth.PI) * 0.75F;
        }

        float phaseOffset = Math.floorMod(this.getId() * 17, IDLE_SWAY_PERIOD_TICKS);
        float swayTime = (this.level().getGameTime() + partialTick + phaseOffset) / IDLE_SWAY_PERIOD_TICKS;
        return Mth.sin(swayTime * Mth.TWO_PI) * damping;
    }

    @Override
    public void tick() {
        this.squish += (this.targetSquish - this.squish) * 0.5F;
        this.oldSquish = this.squish;
        super.tick();
        tickSilvering();

        if (this.onGround() && !this.wasOnGround) {
            spawnLandingParticles();
            this.playSound(getSquishSound(), getSoundVolume(), getSoundPitch());
            this.targetSquish = -0.5F;
        } else if (!this.onGround() && this.wasOnGround) {
            this.targetSquish = 1.0F;
        }

        this.wasOnGround = this.onGround();
        tickIdleAnimation();
        tickIdleBlockParticles();
        if (this.isWorkIdle() && this.onGround()
                && this.entityData.get(IDLE_ANIMATION) == IDLE_ANIMATION_STRETCH) {
            float progress = getIdleAnimationProgress(0.0F, STRETCH_ANIMATION_TICKS);
            float envelope = Mth.sin(progress * Mth.PI);
            this.targetSquish = -Mth.sin(progress * Mth.TWO_PI * 1.5F) * 0.3F * envelope;
        } else {
            this.targetSquish *= 0.9F;
        }
    }

    public boolean isSilvered() {
        return this.entityData.get(SILVERED);
    }

    public boolean startSilvering() {
        if (!(this.level() instanceof ServerLevel level) || !this.isAlive() || this.isRemoved()
                || this.isSilvered() || this.silveringCompletionTime >= 0L) {
            return false;
        }
        this.silveringCompletionTime = level.getGameTime() + SilveringSolutionEffects.DURATION_TICKS;
        SilveringSolutionEffects.playStartSound(level, this.getBoundingBox().getCenter());
        SilveringSolutionEffects.spawnParticles(level, this);
        return true;
    }

    private void tickSilvering() {
        if (!(this.level() instanceof ServerLevel level) || this.silveringCompletionTime < 0L) {
            return;
        }
        if (!this.isAlive() || this.isRemoved() || this.isSilvered()) {
            this.silveringCompletionTime = -1L;
            return;
        }
        long remaining = this.silveringCompletionTime - level.getGameTime();
        if (remaining <= 0L) {
            this.silveringCompletionTime = -1L;
            this.entityData.set(SILVERED, true);
            SilveringSolutionEffects.playCompletionEffects(level, this.getBoundingBox().getCenter());
        } else if (remaining < SilveringSolutionEffects.DURATION_TICKS
                && remaining % SilveringSolutionEffects.PARTICLE_INTERVAL_TICKS == 0L) {
            SilveringSolutionEffects.spawnParticles(level, this);
        }
    }

    private void tickIdleAnimation() {
        if (this.level().isClientSide) {
            return;
        }

        if (!this.isWorkIdle() || !this.onGround()) {
            this.wasWorkIdleForAnimation = false;
            this.idleAnimationCooldown = 0;
            stopIdleAnimation();
            return;
        }

        if (!this.wasWorkIdleForAnimation) {
            this.wasWorkIdleForAnimation = true;
            this.idleAnimationCooldown = 100 + this.random.nextInt(81);
            stopIdleAnimation();
            return;
        }

        int animation = this.entityData.get(IDLE_ANIMATION);
        if (animation != IDLE_ANIMATION_NONE) {
            if (getIdleAnimationElapsedTicks() >= STRETCH_ANIMATION_TICKS) {
                stopIdleAnimation();
                this.idleAnimationCooldown = 100 + this.random.nextInt(81);
            }
            return;
        }

        if (--this.idleAnimationCooldown <= 0) {
            this.entityData.set(IDLE_ANIMATION, IDLE_ANIMATION_STRETCH);
            this.entityData.set(IDLE_ANIMATION_START_TIME, (int) this.level().getGameTime());
        }
    }

    private void tickIdleBlockParticles() {
        if (!this.level().isClientSide || !this.isWorkIdle() || !this.onGround()
                || this.random.nextInt(4) != 0) {
            return;
        }

        BlockPos floorPos = BlockPos.containing(this.getX(), this.getY() - 0.1D, this.getZ());
        BlockState floorState = this.level().getBlockState(floorPos);
        if (floorState.isAir()) {
            return;
        }

        double angle = this.random.nextDouble() * Mth.TWO_PI;
        double radius = 0.28D + this.random.nextDouble() * 0.2D;
        double xOffset = Math.cos(angle) * radius;
        double zOffset = Math.sin(angle) * radius;
        this.level().addParticle(
                new BlockParticleOption(ParticleTypes.BLOCK, floorState),
                this.getX() + xOffset, this.getY() + 0.03D, this.getZ() + zOffset,
                xOffset * 0.025D, 0.015D + this.random.nextDouble() * 0.02D, zOffset * 0.025D
        );
    }

    private void stopIdleAnimation() {
        if (this.entityData.get(IDLE_ANIMATION) != IDLE_ANIMATION_NONE) {
            this.entityData.set(IDLE_ANIMATION, IDLE_ANIMATION_NONE);
        }
    }

    private int getIdleAnimationElapsedTicks() {
        return (int) this.level().getGameTime() - this.entityData.get(IDLE_ANIMATION_START_TIME);
    }

    private float getIdleAnimationProgress(float partialTick, int duration) {
        return Mth.clamp((getIdleAnimationElapsedTicks() + partialTick) / duration, 0.0F, 1.0F);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean wasHurt = super.hurt(source, amount);
        if (wasHurt && source.getEntity() != null && this.level() instanceof ServerLevel level) {
            level.sendParticles(
                    ParticleTypes.ANGRY_VILLAGER,
                    this.getX(), this.getY() + 0.8D, this.getZ(),
                    4, 0.25D, 0.2D, 0.25D, 0.01D
            );
        }
        return wasHurt;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!heldStack.is(Items.BUCKET) || !this.isAlive()) {
            return super.mobInteract(player, hand);
        }

        this.playSound(this.getPickupSound(), 1.0F, 1.0F);
        ItemStack bucketStack = this.getBucketItemStack();
        this.saveToBucketTag(bucketStack);
        player.setItemInHand(hand, ItemUtils.createFilledResult(heldStack, player, bucketStack, false));
        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, bucketStack);
        }
        this.discard();
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        this.entityData.set(FROM_BUCKET, fromBucket);
    }

    @Override
    public void saveToBucketTag(ItemStack bucketStack) {
        if (this.hasCustomName()) {
            bucketStack.setHoverName(this.getCustomName());
        }
        bucketStack.getOrCreateTag().putFloat(TAG_BUCKET_HEALTH, this.getHealth());
        bucketStack.getOrCreateTag().putBoolean(TAG_SILVERED, this.isSilvered());
    }

    @Override
    public void loadFromBucketTag(CompoundTag tag) {
        this.entityData.set(SILVERED, tag.getBoolean(TAG_SILVERED));
        this.silveringCompletionTime = -1L;
        if (tag.contains(TAG_BUCKET_HEALTH, Tag.TAG_ANY_NUMERIC)) {
            this.setHealth(tag.getFloat(TAG_BUCKET_HEALTH));
        }
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ModItems.FORGELING_BUCKET.get());
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_LAVA;
    }

    private void spawnLandingParticles() {
        ParticleOptions particle = ParticleTypes.FLAME;
        for (int index = 0; index < 16; index++) {
            float angle = this.random.nextFloat() * Mth.TWO_PI;
            float distance = this.random.nextFloat() * 0.5F + 0.5F;
            float xOffset = Mth.sin(angle) * LANDING_PARTICLE_DIAMETER * 0.5F * distance;
            float zOffset = Mth.cos(angle) * LANDING_PARTICLE_DIAMETER * 0.5F * distance;
            this.level().addParticle(particle, this.getX() + xOffset, this.getY(), this.getZ() + zOffset,
                    0.0D, 0.0D, 0.0D);
        }
    }

    public float getSquish(float partialTick) {
        return Mth.lerp(partialTick, this.oldSquish, this.squish);
    }

    private int getJumpDelay() {
        return (this.random.nextInt(20) + 10) * 4;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void jumpFromGround() {
        Vec3 movement = this.getDeltaMovement();
        this.setDeltaMovement(movement.x, this.getJumpPower() + 0.2F, movement.z);
        this.hasImpulse = true;
        ForgeHooks.onLivingJump(this);
    }

    void launchSmithingJump(Vec3 target) {
        if (this.moveControl instanceof ForgelingMoveControl control) {
            control.stopMoving();
        }

        Vec3 offset = target.subtract(this.position());
        this.setDeltaMovement(offset.x / 8.0D, this.getJumpPower() + 0.2F, offset.z / 8.0D);
        this.hasImpulse = true;
        this.playSound(getJumpSound(), getSoundVolume(), getSoundPitch());
        ForgeHooks.onLivingJump(this);
    }

    void setSmithingDirection(float targetYaw) {
        if (this.moveControl instanceof ForgelingMoveControl control) {
            control.setDirection(targetYaw, false);
        }
    }

    void setSlowSmithingDirection(float targetYaw) {
        if (this.moveControl instanceof ForgelingMoveControl control) {
            control.setTurningDirection(targetYaw);
        }
    }

    void setQuickSmithingDirection(float targetYaw) {
        if (this.moveControl instanceof ForgelingMoveControl control) {
            control.setQuickDirection(targetYaw);
        }
    }

    void holdSmithingDirection() {
        if (this.moveControl instanceof ForgelingMoveControl control) {
            control.holdDirection();
        }
    }

    void setSmithingMovement(double speed) {
        if (this.moveControl instanceof ForgelingMoveControl control) {
            control.setWantedMovement(speed);
        }
    }

    void stopSmithingMovement() {
        if (this.moveControl instanceof ForgelingMoveControl control) {
            control.stopMoving();
        }
    }

    void anticipateSmithingJump() {
        this.targetSquish = -0.16F;
    }

    @Override
    public void jumpInFluid(net.minecraftforge.fluids.FluidType type) {
        if (type == ForgeMod.LAVA_TYPE.get()) {
            Vec3 movement = this.getDeltaMovement();
            this.setDeltaMovement(movement.x, 0.32D, movement.z);
            this.hasImpulse = true;
        } else {
            super.jumpInFluid(type);
        }
    }

    @Deprecated
    @Override
    protected void jumpInLiquid(TagKey<Fluid> fluidTag) {
        if (fluidTag == FluidTags.LAVA) {
            Vec3 movement = this.getDeltaMovement();
            this.setDeltaMovement(movement.x, 0.32D, movement.z);
            this.hasImpulse = true;
        } else {
            super.jumpInLiquid(fluidTag);
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, net.minecraft.world.entity.EntityDimensions dimensions) {
        return dimensions.height * 0.625F;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.MAGMA_CUBE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.MAGMA_CUBE_DEATH;
    }

    private SoundEvent getSquishSound() {
        return SoundEvents.MAGMA_CUBE_SQUISH;
    }

    private SoundEvent getJumpSound() {
        return SoundEvents.MAGMA_CUBE_JUMP;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    private float getSoundPitch() {
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F;
    }

    private static final class ForgelingPanicGoal extends Goal {
        private static final int PANIC_DURATION_TICKS = 60;

        private final Forgeling forgeling;
        private int handledHurtTimestamp = Integer.MIN_VALUE;
        private int remainingTicks;
        private float escapeYaw;

        private ForgelingPanicGoal(Forgeling forgeling) {
            this.forgeling = forgeling;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.forgeling.getLastHurtByMob() != null
                    && this.forgeling.getLastHurtByMobTimestamp() != this.handledHurtTimestamp
                    && this.forgeling.getMoveControl() instanceof ForgelingMoveControl;
        }

        @Override
        public void start() {
            refreshEscapeDirection();
        }

        @Override
        public boolean canContinueToUse() {
            return this.remainingTicks > 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.forgeling.getLastHurtByMob() != null
                    && this.forgeling.getLastHurtByMobTimestamp() != this.handledHurtTimestamp) {
                refreshEscapeDirection();
            }
            this.remainingTicks--;

            if (this.forgeling.getMoveControl() instanceof ForgelingMoveControl control) {
                control.setQuickDirection(this.escapeYaw);
                control.setWantedMovement(1.25D);
            }
        }

        private void refreshEscapeDirection() {
            LivingEntity attacker = this.forgeling.getLastHurtByMob();
            this.handledHurtTimestamp = this.forgeling.getLastHurtByMobTimestamp();
            this.remainingTicks = PANIC_DURATION_TICKS;

            if (attacker == null) {
                this.escapeYaw = this.forgeling.getRandom().nextFloat() * 360.0F;
                return;
            }

            double awayX = this.forgeling.getX() - attacker.getX();
            double awayZ = this.forgeling.getZ() - attacker.getZ();
            if (awayX * awayX + awayZ * awayZ < 0.01D) {
                this.escapeYaw = this.forgeling.getRandom().nextFloat() * 360.0F;
            } else {
                this.escapeYaw = (float) (Mth.atan2(awayZ, awayX) * Mth.RAD_TO_DEG) - 90.0F
                        + this.forgeling.getRandom().nextFloat() * 40.0F - 20.0F;
            }
        }
    }

    private static final class ForgelingFloatGoal extends Goal {
        private final Forgeling forgeling;

        private ForgelingFloatGoal(Forgeling forgeling) {
            this.forgeling = forgeling;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
            forgeling.getNavigation().setCanFloat(true);
        }

        @Override
        public boolean canUse() {
            return (this.forgeling.isInWater() || this.forgeling.isInLava())
                    && this.forgeling.getMoveControl() instanceof ForgelingMoveControl;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.forgeling.getRandom().nextFloat() < 0.8F) {
                this.forgeling.getJumpControl().jump();
            }

            if (this.forgeling.getMoveControl() instanceof ForgelingMoveControl control) {
                control.setWantedMovement(1.2D);
            }
        }
    }

    private static final class ForgelingKeepOnJumpingGoal extends Goal {
        private final Forgeling forgeling;

        private ForgelingKeepOnJumpingGoal(Forgeling forgeling) {
            this.forgeling = forgeling;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.forgeling.isPassenger();
        }

        @Override
        public void tick() {
            if (this.forgeling.getMoveControl() instanceof ForgelingMoveControl control) {
                control.setWantedMovement(1.0D);
            }
        }
    }

    private static final class ForgelingRandomDirectionGoal extends Goal {
        private final Forgeling forgeling;
        private float chosenDegrees;
        private int nextRandomizeTime;

        private ForgelingRandomDirectionGoal(Forgeling forgeling) {
            this.forgeling = forgeling;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return (this.forgeling.onGround()
                    || this.forgeling.isInWater()
                    || this.forgeling.isInLava()
                    || this.forgeling.hasEffect(MobEffects.LEVITATION))
                    && this.forgeling.getMoveControl() instanceof ForgelingMoveControl;
        }

        @Override
        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.forgeling.getRandom().nextInt(60));
                this.chosenDegrees = this.forgeling.getRandom().nextInt(360);
            }

            if (this.forgeling.getMoveControl() instanceof ForgelingMoveControl control) {
                control.setDirection(this.chosenDegrees, false);
            }
        }
    }

    private static final class ForgelingMoveControl extends MoveControl {
        private final Forgeling forgeling;
        private float targetYaw;
        private float maxTurn = 90.0F;
        private int jumpDelay;
        private boolean quickJumping;

        private ForgelingMoveControl(Forgeling forgeling) {
            super(forgeling);
            this.forgeling = forgeling;
            this.targetYaw = 180.0F * forgeling.getYRot() / Mth.PI;
        }

        private void setDirection(float targetYaw, boolean quickJumping) {
            this.targetYaw = targetYaw;
            this.quickJumping = quickJumping;
            this.maxTurn = 90.0F;
        }

        private void setQuickDirection(float targetYaw) {
            this.targetYaw = targetYaw;
            this.quickJumping = true;
            this.maxTurn = 90.0F;
            this.jumpDelay = Math.min(this.jumpDelay, 5);
        }

        private void setTurningDirection(float targetYaw) {
            this.targetYaw = targetYaw;
            this.quickJumping = false;
            this.maxTurn = 18.0F;
        }

        private void holdDirection() {
            this.targetYaw = this.forgeling.getYRot();
            this.quickJumping = false;
            this.maxTurn = 90.0F;
        }

        private void setWantedMovement(double speed) {
            this.speedModifier = speed;
            this.operation = Operation.MOVE_TO;
        }

        private void stopMoving() {
            this.operation = Operation.WAIT;
            this.mob.setSpeed(0.0F);
            this.mob.setZza(0.0F);
            this.forgeling.xxa = 0.0F;
            this.forgeling.zza = 0.0F;
        }

        @Override
        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.targetYaw, this.maxTurn));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
                return;
            }

            this.operation = Operation.WAIT;
            if (this.mob.onGround()) {
                this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                if (this.jumpDelay-- <= 0) {
                    this.jumpDelay = this.forgeling.getJumpDelay();
                    if (this.quickJumping) {
                        this.jumpDelay /= 3;
                    }

                    this.forgeling.getJumpControl().jump();
                    this.forgeling.playSound(this.forgeling.getJumpSound(),
                            this.forgeling.getSoundVolume(), this.forgeling.getSoundPitch());
                } else {
                    this.forgeling.xxa = 0.0F;
                    this.forgeling.zza = 0.0F;
                    this.mob.setSpeed(0.0F);
                }
            } else {
                this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            }
        }
    }
}
