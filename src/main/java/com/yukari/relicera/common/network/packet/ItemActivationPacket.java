package com.yukari.relicera.common.network.packet;

import com.yukari.relicera.client.effect.ItemActivationEffect;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record ItemActivationPacket(ItemStack stack) {
    public static void encode(ItemActivationPacket packet, FriendlyByteBuf buffer) {
        buffer.writeItem(packet.stack());
    }

    public static ItemActivationPacket decode(FriendlyByteBuf buffer) {
        return new ItemActivationPacket(buffer.readItem());
    }

    public static void handle(ItemActivationPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ItemActivationEffect.show(packet.stack()));
        contextSupplier.get().setPacketHandled(true);
    }
}
