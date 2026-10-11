package com.yukari.relicera.common.curio;

import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.registry.ModItems;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import top.theillusivec4.curios.api.CuriosApi;

public final class AshenTouchEffects {
    private static final int SNAPSHOT_TTL_TICKS = 5;

    private static final Map<UUID, AttackSnapshot> ATTACK_SNAPSHOTS = new HashMap<>();
    private static final Map<ResourceKey<Level>, Set<UUID>> PENDING_FIRE_CLEAR = new HashMap<>();

    private AshenTouchEffects() {
    }

    public static boolean isEquipped(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler -> handler.isEquipped(ModItems.ASHEN_TOUCH.get()))
                .orElse(false);
    }

    public static void rememberPreAttackFireState(AttackEntityEvent event) {
        if (event.getEntity().level().isClientSide()
                || !(event.getTarget() instanceof LivingEntity target)
                || !isEquipped(event.getEntity())) {
            return;
        }

        ATTACK_SNAPSHOTS.put(target.getUUID(), new AttackSnapshot(
                event.getEntity().getUUID(),
                event.getEntity().level().dimension(),
                event.getEntity().level().getGameTime(),
                target.isOnFire()
        ));
    }

    public static void applyMeleeFireEffects(LivingHurtEvent event) {
        Player player = getEquippedMeleeAttacker(event.getSource().getEntity(), event.getSource().getDirectEntity());
        if (player == null) {
            return;
        }

        LivingEntity target = event.getEntity();
        queueFireClear(target);

        AttackSnapshot snapshot = ATTACK_SNAPSHOTS.get(target.getUUID());
        if (!(target instanceof Blaze) && (snapshot == null
                || !snapshot.attackerId().equals(player.getUUID())
                || !snapshot.dimension().equals(player.level().dimension())
                || player.level().getGameTime() - snapshot.gameTime() > SNAPSHOT_TTL_TICKS
                || !snapshot.wasOnFire())) {
            return;
        }

        spawnExtinguishFeedback(target);
        float multiplier = 1.0F + ModCommonConfig.ASHEN_TOUCH_BURNING_TARGET_DAMAGE_BONUS.get().floatValue();
        event.setAmount(event.getAmount() * multiplier);
    }

    public static void clearQueuedFires(ServerLevel level) {
        long gameTime = level.getGameTime();
        ATTACK_SNAPSHOTS.entrySet().removeIf(entry -> gameTime - entry.getValue().gameTime() > SNAPSHOT_TTL_TICKS);

        Set<UUID> entityIds = PENDING_FIRE_CLEAR.remove(level.dimension());
        if (entityIds == null) {
            return;
        }

        for (UUID entityId : entityIds) {
            Entity entity = level.getEntity(entityId);
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.clearFire();
            }
        }
    }

    private static void queueFireClear(LivingEntity target) {
        target.clearFire();
        PENDING_FIRE_CLEAR
                .computeIfAbsent(target.level().dimension(), dimension -> new HashSet<>())
                .add(target.getUUID());
    }

    private static void spawnExtinguishFeedback(LivingEntity target) {
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        RandomSource random = serverLevel.getRandom();
        double x = target.getX();
        double y = target.getY(0.55D);
        double z = target.getZ();
        double width = Math.max(0.25D, target.getBbWidth() * 0.35D);
        double height = Math.max(0.3D, target.getBbHeight() * 0.25D);
        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 18, width, height, width, 0.04D);
        serverLevel.sendParticles(ParticleTypes.SMOKE, x, y + target.getBbHeight() * 0.2D, z, 12, width * 0.7D, height, width * 0.7D, 0.02D);
        for (int i = 0; i < 8; i++) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    x + (random.nextDouble() - 0.5D) * width * 2.0D,
                    target.getY(0.25D + random.nextDouble() * 0.55D),
                    z + (random.nextDouble() - 0.5D) * width * 2.0D,
                    0,
                    (random.nextDouble() - 0.5D) * 0.05D,
                    0.04D + random.nextDouble() * 0.04D,
                    (random.nextDouble() - 0.5D) * 0.05D,
                    1.0D);
        }
        serverLevel.playSound(null, x, y, z, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 0.75F, 0.9F + random.nextFloat() * 0.18F);
    }

    private static Player getEquippedMeleeAttacker(Entity sourceEntity, Entity directEntity) {
        if (sourceEntity instanceof Player player && directEntity == player && isEquipped(player)) {
            return player;
        }
        return null;
    }

    private record AttackSnapshot(UUID attackerId, ResourceKey<Level> dimension, long gameTime, boolean wasOnFire) {
    }
}
