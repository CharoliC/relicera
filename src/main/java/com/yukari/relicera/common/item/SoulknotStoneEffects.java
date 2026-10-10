package com.yukari.relicera.common.item;

import com.yukari.relicera.common.effect.souloverload.SoulOverloadState;
import com.yukari.relicera.registry.ModEffects;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;

public final class SoulknotStoneEffects {
    private static final String OVERLOAD_TAG = "ReliceraSoulOverload";
    private static final String DEATH_TAG = "ReliceraSoulknotDeath";
    private static final int EFFECT_TICKS = 90 * 20;
    private static final int FEED_COOLDOWN_TICKS = 12 * 20;
    private static final int UNSTABLE_TICKS = 15 * 20;
    private static final int SHOT_INTERVAL = 60;
    private static final int CHARGE_TICKS = 34;
    private static final int BURST_SHOTS = 3;
    private static final int BURST_INTERVAL_TICKS = 5;
    private static final double RANGE = 20.0D;

    private SoulknotStoneEffects() {
    }

    public static InteractionResult feed(ItemStack stack, Player player, LivingEntity target) {
        if (!(target instanceof Warden warden) || !warden.isAlive()) {
            return InteractionResult.PASS;
        }
        if (player.getCooldowns().isOnCooldown(stack.getItem()) || isUnstable(warden)
                || getLevel(warden) >= 3) {
            return InteractionResult.CONSUME;
        }
        if (!(warden.level() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }
        int nextLevel = getLevel(warden) + 1;
        if (!warden.addEffect(new MobEffectInstance(ModEffects.SOUL_OVERLOAD.get(), EFFECT_TICKS,
                nextLevel - 1, false, false, true), player)) {
            return InteractionResult.CONSUME;
        }
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        player.getCooldowns().addCooldown(ModItems.SCULK_FRUIT.get(), FEED_COOLDOWN_TICKS);
        level.playSound(null, warden.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.HOSTILE, 1.0F, 1.0F);
        ((SoulOverloadState) warden).relicera$setSoulOverloadLevel(nextLevel);
        if (nextLevel >= 3) {
            begin(warden);
        } else if (!warden.isNoAi() && warden.canTargetEntity(player)) {
            warden.increaseAngerAt(player, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
            if (warden.getTarget() != player) {
                warden.setAttackTarget(player);
            }
        }
        return InteractionResult.CONSUME;
    }

    public static int getLevel(Warden warden) {
        MobEffectInstance effect = warden.getEffect(ModEffects.SOUL_OVERLOAD.get());
        return effect == null ? 0 : effect.getAmplifier() + 1;
    }

    public static float damageBonus(Warden warden) {
        return 4.0F * getLevel(warden);
    }

    public static void tick(Warden warden) {
        if (!(warden.level() instanceof ServerLevel level)) {
            return;
        }
        int effectLevel = warden.isAlive() ? getLevel(warden) : 0;
        ((SoulOverloadState) warden).relicera$setSoulOverloadLevel(effectLevel);
        if (effectLevel < 3) {
            release(warden);
            return;
        }
        if (!isUnstable(warden)) {
            begin(warden);
        }
        CompoundTag state = warden.getPersistentData().getCompound(OVERLOAD_TAG);
        holdPosition(warden, state);
        int elapsed = state.getInt("ElapsedTicks");
        if (elapsed >= UNSTABLE_TICKS) {
            finish(level, warden);
            return;
        }
        int cycleTick = elapsed % SHOT_INTERVAL;
        int burstTick = cycleTick - CHARGE_TICKS;
        if (cycleTick == 0) {
            state.putFloat("Yaw", warden.getRandom().nextFloat() * 360.0F);
            state.putFloat("Pitch", warden.getRandom().nextFloat() * 30.0F - 15.0F);
            faceShot(warden, state);
            level.broadcastEntityEvent(warden, (byte) 62);
            warden.playSound(SoundEvents.WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
        } else if (burstTick >= 0 && burstTick < BURST_SHOTS * BURST_INTERVAL_TICKS
                && burstTick % BURST_INTERVAL_TICKS == 0) {
            fire(level, warden, state);
        }
        faceShot(warden, state);
        state.putInt("ElapsedTicks", elapsed + 1);
    }

    public static boolean isUnstable(Warden warden) {
        return warden.getPersistentData().contains(OVERLOAD_TAG, Tag.TAG_COMPOUND);
    }

    private static void begin(Warden warden) {
        if (isUnstable(warden)) {
            return;
        }
        CompoundTag state = new CompoundTag();
        state.putBoolean("OldNoAi", warden.isNoAi());
        state.putBoolean("OldNoGravity", warden.isNoGravity());
        state.putDouble("X", warden.getX());
        state.putDouble("Y", warden.getY());
        state.putDouble("Z", warden.getZ());
        state.putFloat("Yaw", warden.getYRot());
        state.putFloat("Pitch", warden.getXRot());
        warden.getPersistentData().put(OVERLOAD_TAG, state);
        holdPosition(warden, state);
    }

    private static void holdPosition(Warden warden, CompoundTag state) {
        warden.setNoAi(true);
        warden.setNoGravity(true);
        warden.stopRiding();
        warden.getNavigation().stop();
        warden.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        warden.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
        warden.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        warden.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        warden.setPose(Pose.STANDING);
        warden.setPos(state.getDouble("X"), state.getDouble("Y"), state.getDouble("Z"));
        warden.setDeltaMovement(Vec3.ZERO);
        warden.xxa = 0.0F;
        warden.yya = 0.0F;
        warden.zza = 0.0F;
    }

    private static void faceShot(Warden warden, CompoundTag state) {
        float yaw = state.getFloat("Yaw");
        warden.setYRot(yaw);
        warden.setYHeadRot(yaw);
        warden.setYBodyRot(yaw);
        warden.setXRot(state.getFloat("Pitch"));
    }

    private static void fire(ServerLevel level, Warden warden, CompoundTag state) {
        Vec3 start = warden.position().add(0.0D, 1.6D, 0.0D);
        Vec3 direction = Vec3.directionFromRotation(state.getFloat("Pitch"), state.getFloat("Yaw"));
        Vec3 end = start.add(direction.scale(RANGE));
        for (int distance = 1; distance <= RANGE; distance++) {
            Vec3 point = start.add(direction.scale(distance));
            level.sendParticles(ParticleTypes.SONIC_BOOM, point.x, point.y, point.z,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        warden.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
        DamageSource source = new OverloadedSonicDamageSource(warden);
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(start, end).inflate(0.5D), warden::canTargetEntity)) {
            AABB box = victim.getBoundingBox().inflate(0.5D);
            if (!box.contains(start) && box.clip(start, end).isEmpty()) {
                continue;
            }
            victim.hurt(source, 10.0F + damageBonus(warden));
            double resistance = 1.0D - victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            victim.push(direction.x * 2.5D * resistance, direction.y * 0.5D * resistance,
                    direction.z * 2.5D * resistance);
        }
    }

    private static void release(Warden warden) {
        if (isUnstable(warden)) {
            CompoundTag state = warden.getPersistentData().getCompound(OVERLOAD_TAG);
            warden.setNoAi(state.getBoolean("OldNoAi"));
            warden.setNoGravity(state.getBoolean("OldNoGravity"));
            warden.getPersistentData().remove(OVERLOAD_TAG);
        }
    }

    private static void finish(ServerLevel level, Warden warden) {
        release(warden);
        warden.getPersistentData().putBoolean(DEATH_TAG, true);
        float previousHealth = warden.getHealth();
        warden.kill();
        if (warden.isDeadOrDying() && warden.hasPose(Pose.DYING)) {
            Vec3 center = warden.getBoundingBox().getCenter();
            level.sendParticles(ParticleTypes.SCULK_SOUL, center.x, center.y, center.z,
                    80, 0.9D, 1.0D, 0.9D, 0.08D);
            ItemEntity stone = new ItemEntity(level, warden.getX(), warden.getY() + 0.5D, warden.getZ(),
                    new ItemStack(ModItems.SOULKNOT_STONE.get()));
            stone.setDefaultPickUpDelay();
            level.addFreshEntity(stone);
        } else {
            if (warden.isDeadOrDying()) {
                warden.setHealth(previousHealth);
            }
            warden.getPersistentData().remove(DEATH_TAG);
            warden.removeEffect(ModEffects.SOUL_OVERLOAD.get());
        }
        ((SoulOverloadState) warden).relicera$setSoulOverloadLevel(0);
    }

    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel) || !(event.getEntity() instanceof Warden warden)) {
            return;
        }
        CompoundTag data = warden.getPersistentData();
        if (data.contains("ReliceraSoulknotRitual", Tag.TAG_COMPOUND)) {
            CompoundTag old = data.getCompound("ReliceraSoulknotRitual");
            warden.setNoAi(old.getBoolean("OldNoAi"));
            warden.setNoGravity(old.getBoolean("OldNoGravity"));
            warden.setInvulnerable(old.getBoolean("OldInvulnerable"));
            warden.setSilent(old.getBoolean("OldSilent"));
            warden.canUpdate(!warden.isAlive() || old.getBoolean("OldCanUpdate"));
            data.remove("ReliceraSoulknotRitual");
        }
        ((SoulOverloadState) warden).relicera$setSoulOverloadLevel(warden.isAlive() ? getLevel(warden) : 0);
    }

    public static void suppressDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Warden && event.getEntity().getPersistentData().getBoolean(DEATH_TAG)) {
            event.getDrops().clear();
            event.setCanceled(true);
        }
    }

    public static void suppressExperience(LivingExperienceDropEvent event) {
        if (event.getEntity() instanceof Warden && event.getEntity().getPersistentData().getBoolean(DEATH_TAG)) {
            event.setDroppedExperience(0);
            event.setCanceled(true);
        }
    }

    private static final class OverloadedSonicDamageSource extends DamageSource {
        private OverloadedSonicDamageSource(Warden warden) {
            super(warden.damageSources().sonicBoom(warden).typeHolder(), warden, warden);
        }

        @Override
        public boolean is(TagKey<DamageType> tag) {
            return DamageTypeTags.BYPASSES_COOLDOWN.equals(tag) || super.is(tag);
        }
    }
}
