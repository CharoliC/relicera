package com.yukari.relicera.common.item;

import com.yukari.relicera.registry.ModBlocks;
import com.yukari.relicera.registry.ModParticleTypes;
import com.yukari.relicera.common.entity.forgeling.Forgeling;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SilveringSolutionEffects {
    public static final int DURATION_TICKS = 60;
    public static final int PARTICLE_INTERVAL_TICKS = 4;
    private static final Map<ResourceKey<Level>, Map<BlockPos, Long>> PENDING = new HashMap<>();

    private SilveringSolutionEffects() {
    }

    public static boolean start(ServerLevel level, BlockPos pos) {
        if (!level.isLoaded(pos) || !level.getBlockState(pos).is(Blocks.ANCIENT_DEBRIS)) {
            return false;
        }
        Map<BlockPos, Long> pending = PENDING.computeIfAbsent(level.dimension(), ignored -> new HashMap<>());
        if (pending.putIfAbsent(pos.immutable(), level.getGameTime() + DURATION_TICKS) != null) {
            return false;
        }
        playStartSound(level, Vec3.atCenterOf(pos));
        spawnParticles(level, pos);
        return true;
    }

    public static void tickLevel(ServerLevel level) {
        Map<BlockPos, Long> pending = PENDING.get(level.dimension());
        if (pending == null) {
            return;
        }
        long gameTime = level.getGameTime();
        Iterator<Map.Entry<BlockPos, Long>> iterator = pending.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Long> entry = iterator.next();
            BlockPos pos = entry.getKey();
            if (!level.isLoaded(pos) || !level.getBlockState(pos).is(Blocks.ANCIENT_DEBRIS)) {
                iterator.remove();
                continue;
            }
            long remaining = entry.getValue() - gameTime;
            if (remaining <= 0) {
                iterator.remove();
                if (level.setBlock(pos, ModBlocks.SILVERED_DEBRIS.get().defaultBlockState(), Block.UPDATE_ALL)) {
                    playCompletionEffects(level, Vec3.atCenterOf(pos));
                }
            } else if (remaining < DURATION_TICKS && remaining % PARTICLE_INTERVAL_TICKS == 0) {
                spawnParticles(level, pos);
            }
        }
        if (pending.isEmpty()) {
            PENDING.remove(level.dimension());
        }
    }

    private static void spawnParticles(ServerLevel level, BlockPos pos) {
        List<Direction> exposedFaces = new ArrayList<>();
        for (Direction face : Direction.values()) {
            BlockPos neighbor = pos.relative(face);
            if (level.isLoaded(neighbor) && !level.getBlockState(neighbor).isSolidRender(level, neighbor)) {
                exposedFaces.add(face);
            }
        }
        if (exposedFaces.isEmpty()) {
            return;
        }
        int count = 1 + level.random.nextInt(2);
        for (int i = 0; i < count; i++) {
            Direction face = exposedFaces.get(level.random.nextInt(exposedFaces.size()));
            double x = pos.getX() + (face.getAxis() == Direction.Axis.X
                    ? 0.5D + face.getStepX() * 0.55D : level.random.nextDouble());
            double y = pos.getY() + (face.getAxis() == Direction.Axis.Y
                    ? 0.5D + face.getStepY() * 0.55D : level.random.nextDouble());
            double z = pos.getZ() + (face.getAxis() == Direction.Axis.Z
                    ? 0.5D + face.getStepZ() * 0.55D : level.random.nextDouble());
            level.sendParticles(ModParticleTypes.FEYSILVER_SOLUTION_POP.get(), x, y, z, 0,
                    face.getStepX() * 0.015D, face.getStepY() * 0.015D, face.getStepZ() * 0.015D, 1.0D);
        }
    }

    public static void playStartSound(ServerLevel level, Vec3 position) {
        level.playSound(null, position.x, position.y, position.z,
                SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static void playCompletionEffects(ServerLevel level, Vec3 position) {
        level.sendParticles(ParticleTypes.POOF, position.x, position.y, position.z,
                12, 0.55D, 0.55D, 0.55D, 0.02D);
        level.playSound(null, position.x, position.y, position.z,
                SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static void spawnParticles(ServerLevel level, Forgeling forgeling) {
        AABB bounds = forgeling.getBoundingBox().inflate(0.05D);
        int count = 1 + level.random.nextInt(2);
        for (int i = 0; i < count; i++) {
            Direction face = Direction.getRandom(level.random);
            double x = face.getAxis() == Direction.Axis.X
                    ? (face.getStepX() < 0 ? bounds.minX : bounds.maxX)
                    : bounds.minX + level.random.nextDouble() * bounds.getXsize();
            double y = face.getAxis() == Direction.Axis.Y
                    ? (face.getStepY() < 0 ? bounds.minY : bounds.maxY)
                    : bounds.minY + level.random.nextDouble() * bounds.getYsize();
            double z = face.getAxis() == Direction.Axis.Z
                    ? (face.getStepZ() < 0 ? bounds.minZ : bounds.maxZ)
                    : bounds.minZ + level.random.nextDouble() * bounds.getZsize();
            level.sendParticles(ModParticleTypes.FEYSILVER_SOLUTION_POP.get(), x, y, z, 0,
                    face.getStepX() * 0.015D, face.getStepY() * 0.015D, face.getStepZ() * 0.015D, 1.0D);
        }
    }

    public static void cancelChunk(ServerLevel level, ChunkPos chunkPos) {
        Map<BlockPos, Long> pending = PENDING.get(level.dimension());
        if (pending != null) {
            pending.keySet().removeIf(pos -> pos.getX() >> 4 == chunkPos.x && pos.getZ() >> 4 == chunkPos.z);
            if (pending.isEmpty()) {
                PENDING.remove(level.dimension());
            }
        }
    }

    public static void clearLevel(ServerLevel level) {
        PENDING.remove(level.dimension());
    }

    public static void clear() {
        PENDING.clear();
    }
}
