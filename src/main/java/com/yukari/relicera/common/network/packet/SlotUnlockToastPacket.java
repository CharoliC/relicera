package com.yukari.relicera.common.network.packet;

import com.yukari.relicera.client.toast.SlotUnlockToast;
import com.yukari.relicera.common.curio.CurioSlotUnlocks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SlotUnlockToastPacket(CurioSlotUnlocks.Reward reward) {
    public static void encode(SlotUnlockToastPacket packet, FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.reward());
    }

    public static SlotUnlockToastPacket decode(FriendlyByteBuf buffer) {
        return new SlotUnlockToastPacket(buffer.readEnum(CurioSlotUnlocks.Reward.class));
    }

    public static void handle(SlotUnlockToastPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SlotUnlockToast.show(packet.reward()));
        contextSupplier.get().setPacketHandled(true);
    }
}
