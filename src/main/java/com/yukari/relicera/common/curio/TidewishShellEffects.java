package com.yukari.relicera.common.curio;

import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.common.network.packet.TidewishRiptidePacket;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.CuriosApi;

public final class TidewishShellEffects {
    private static final int WET_COOLDOWN_TICKS = 30;
    private static final int DRY_COOLDOWN_TICKS = 200;
    private static final int SPIN_TICKS = 20;
    private static final double RIPTIDE_SPEED = 3.0D;
    private static final double GROUND_LIFT = 1.1999999D;

    private TidewishShellEffects() {
    }

    public static boolean canActivate(Player player) {
        return player.isAlive() && !player.isSpectator() && !player.isPassenger()
                && !player.getAbilities().mayfly && !player.isAutoSpinAttack()
                && !player.getCooldowns().isOnCooldown(ModItems.TIDEWISH_SHELL.get())
                && CuriosApi.getCuriosInventory(player).resolve()
                        .map(handler -> handler.isEquipped(ModItems.TIDEWISH_SHELL.get())).orElse(false);
    }

    public static void tryActivate(ServerPlayer player) {
        if (!canActivate(player)) {
            return;
        }
        int cooldownTicks = player.isInWaterOrRain() ? WET_COOLDOWN_TICKS : DRY_COOLDOWN_TICKS;
        Vec3 impulse = player.getLookAngle().scale(RIPTIDE_SPEED);
        boolean lift = player.onGround();
        applyRiptide(player, impulse, lift);
        player.getCooldowns().addCooldown(ModItems.TIDEWISH_SHELL.get(), cooldownTicks);
        player.awardStat(Stats.ITEM_USED.get(ModItems.TIDEWISH_SHELL.get()));
        ModNetworking.sendToPlayer(new TidewishRiptidePacket(impulse, lift), player);
        player.level().playSound(null, player, SoundEvents.TRIDENT_RIPTIDE_3, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    public static void applyRiptide(Player player, Vec3 impulse, boolean lift) {
        player.push(impulse.x, impulse.y, impulse.z);
        player.startAutoSpinAttack(SPIN_TICKS);
        if (lift) {
            player.move(MoverType.SELF, new Vec3(0.0D, GROUND_LIFT, 0.0D));
        }
    }
}
