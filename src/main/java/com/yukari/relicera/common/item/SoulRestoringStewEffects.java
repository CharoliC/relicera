package com.yukari.relicera.common.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class SoulRestoringStewEffects {
    private static final int GROUP_INTERVAL_TICKS = 20;
    private static final int PAIR_INTERVAL_TICKS = 4;
    private static final int LAST_HEARTBEAT_TICK = 2 * GROUP_INTERVAL_TICKS + PAIR_INTERVAL_TICKS;
    private static final Map<UUID, List<Integer>> HEARTBEAT_START_TICKS = new HashMap<>();

    private SoulRestoringStewEffects() {
    }

    public static void startHeartbeat(ServerPlayer player) {
        HEARTBEAT_START_TICKS.computeIfAbsent(player.getUUID(), id -> new ArrayList<>()).add(player.tickCount);
        playHeartbeat(player);
    }

    public static void tickPlayer(ServerPlayer player) {
        List<Integer> starts = HEARTBEAT_START_TICKS.get(player.getUUID());
        if (starts == null) {
            return;
        }
        if (!player.isAlive()) {
            clearPlayer(player.getUUID());
            return;
        }
        starts.removeIf(start -> {
            int elapsed = player.tickCount - start;
            if (elapsed > 0 && elapsed <= LAST_HEARTBEAT_TICK
                    && (elapsed % GROUP_INTERVAL_TICKS == 0
                    || elapsed % GROUP_INTERVAL_TICKS == PAIR_INTERVAL_TICKS)) {
                playHeartbeat(player);
            }
            return elapsed < 0 || elapsed >= LAST_HEARTBEAT_TICK;
        });
        if (starts.isEmpty()) {
            clearPlayer(player.getUUID());
        }
    }

    public static void clearPlayer(UUID playerId) {
        HEARTBEAT_START_TICKS.remove(playerId);
    }

    public static void onServerStopped() {
        HEARTBEAT_START_TICKS.clear();
    }

    private static void playHeartbeat(ServerPlayer player) {
        player.playNotifySound(SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
