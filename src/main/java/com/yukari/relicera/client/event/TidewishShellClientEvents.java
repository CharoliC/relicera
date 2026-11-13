package com.yukari.relicera.client.event;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.curio.TidewishShellEffects;
import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.common.network.packet.UseTidewishShellPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ReliceraMod.MOD_ID, value = Dist.CLIENT)
public final class TidewishShellClientEvents {
    private static final int DOUBLE_TAP_TICKS = 7;
    private static LocalPlayer observedPlayer;
    private static boolean jumpWasDown;
    private static int firstPressTick = -1;

    private TidewishShellClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        boolean jumpDown = minecraft.options.keyJump.isDown();
        if (player != observedPlayer) {
            observedPlayer = player;
            firstPressTick = -1;
            jumpWasDown = jumpDown;
        }
        if (player == null || minecraft.screen != null || minecraft.isPaused()
                || !TidewishShellEffects.canActivate(player)) {
            firstPressTick = -1;
            jumpWasDown = jumpDown;
            return;
        }
        if (jumpDown && !jumpWasDown) {
            int elapsed = player.tickCount - firstPressTick;
            if (firstPressTick >= 0 && elapsed > 0 && elapsed <= DOUBLE_TAP_TICKS) {
                firstPressTick = -1;
                ModNetworking.sendToServer(new UseTidewishShellPacket());
            } else {
                firstPressTick = player.tickCount;
            }
        }
        jumpWasDown = jumpDown;
    }

    public static void activate(Vec3 impulse, boolean lift) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            TidewishShellEffects.applyRiptide(player, impulse, lift);
        }
    }
}
