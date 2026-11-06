package com.yukari.relicera.common.network.packet;

import com.yukari.relicera.common.item.feysilver.FeysilverForgingClientData;
import com.yukari.relicera.common.item.feysilver.ForgingVolume;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.EnumSet;
import java.util.function.Supplier;

public record SyncFeysilverForgingKnowledgePacket(EnumSet<ForgingVolume> learned) {
    public static void encode(SyncFeysilverForgingKnowledgePacket packet, FriendlyByteBuf buffer) {
        buffer.writeEnumSet(packet.learned, ForgingVolume.class);
    }

    public static SyncFeysilverForgingKnowledgePacket decode(FriendlyByteBuf buffer) {
        return new SyncFeysilverForgingKnowledgePacket(buffer.readEnumSet(ForgingVolume.class));
    }

    public static void handle(SyncFeysilverForgingKnowledgePacket packet, Supplier<NetworkEvent.Context> context) {
        FeysilverForgingClientData.update(packet.learned);
        context.get().setPacketHandled(true);
    }
}
