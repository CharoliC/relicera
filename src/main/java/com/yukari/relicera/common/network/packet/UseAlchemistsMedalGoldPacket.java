package com.yukari.relicera.common.network.packet;

import com.yukari.relicera.common.item.AlchemistsMedalItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record UseAlchemistsMedalGoldPacket(int inventorySlot, int count, boolean goldOnCursor) {
    public static void encode(UseAlchemistsMedalGoldPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.inventorySlot);
        buffer.writeVarInt(packet.count);
        buffer.writeBoolean(packet.goldOnCursor);
    }

    public static UseAlchemistsMedalGoldPacket decode(FriendlyByteBuf buffer) {
        return new UseAlchemistsMedalGoldPacket(buffer.readVarInt(), buffer.readVarInt(), buffer.readBoolean());
    }

    public static void handle(UseAlchemistsMedalGoldPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();
        if (player != null) {
            AlchemistsMedalItem.handleCreativeInteraction(player, packet.inventorySlot, packet.count, packet.goldOnCursor);
        }
        context.setPacketHandled(true);
    }
}
