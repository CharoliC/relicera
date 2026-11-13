package com.yukari.relicera.common.network.packet;

import com.yukari.relicera.common.curio.TidewishShellEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record UseTidewishShellPacket() {
    public static void encode(UseTidewishShellPacket packet, FriendlyByteBuf buffer) {
    }

    public static UseTidewishShellPacket decode(FriendlyByteBuf buffer) {
        return new UseTidewishShellPacket();
    }

    public static void handle(UseTidewishShellPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();
        if (player != null) {
            TidewishShellEffects.tryActivate(player);
        }
        context.setPacketHandled(true);
    }
}
