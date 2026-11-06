package com.yukari.relicera.common.network.packet;

import com.yukari.relicera.client.toast.ForgingLearnedToast;
import com.yukari.relicera.common.item.feysilver.ForgingVolume;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ForgingLearnedToastPacket(ForgingVolume volume) {
    public static void encode(ForgingLearnedToastPacket packet, FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.volume);
    }

    public static ForgingLearnedToastPacket decode(FriendlyByteBuf buffer) {
        return new ForgingLearnedToastPacket(buffer.readEnum(ForgingVolume.class));
    }

    public static void handle(ForgingLearnedToastPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ForgingLearnedToast.show(packet.volume));
        contextSupplier.get().setPacketHandled(true);
    }
}
