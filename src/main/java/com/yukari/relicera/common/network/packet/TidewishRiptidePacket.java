package com.yukari.relicera.common.network.packet;

import com.yukari.relicera.client.event.TidewishShellClientEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record TidewishRiptidePacket(Vec3 impulse, boolean lift) {
    public static void encode(TidewishRiptidePacket packet, FriendlyByteBuf buffer) {
        buffer.writeDouble(packet.impulse.x);
        buffer.writeDouble(packet.impulse.y);
        buffer.writeDouble(packet.impulse.z);
        buffer.writeBoolean(packet.lift);
    }

    public static TidewishRiptidePacket decode(FriendlyByteBuf buffer) {
        return new TidewishRiptidePacket(
                new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readBoolean());
    }

    public static void handle(TidewishRiptidePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> TidewishShellClientEvents.activate(packet.impulse, packet.lift));
        contextSupplier.get().setPacketHandled(true);
    }
}
