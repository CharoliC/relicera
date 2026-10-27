package com.yukari.relicera.common.network.packet;

import com.yukari.relicera.client.toast.ForgingLearnedToast;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ForgingLearnedToastPacket() {
    public static void encode(ForgingLearnedToastPacket packet, FriendlyByteBuf buffer) {
    }

    public static ForgingLearnedToastPacket decode(FriendlyByteBuf buffer) {
        return new ForgingLearnedToastPacket();
    }

    public static void handle(ForgingLearnedToastPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ForgingLearnedToast::show);
        contextSupplier.get().setPacketHandled(true);
    }
}
