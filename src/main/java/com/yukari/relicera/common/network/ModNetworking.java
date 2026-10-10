package com.yukari.relicera.common.network;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.network.packet.CameraShakePacket;
import com.yukari.relicera.common.network.packet.CovenantToastPacket;
import com.yukari.relicera.common.network.packet.OpenAstralStorybookPacket;
import com.yukari.relicera.common.network.packet.SyncAstralObservationPacket;
import com.yukari.relicera.common.network.packet.SyncAttackStrengthPacket;
import com.yukari.relicera.common.network.packet.SyncFeysilverForgingKnowledgePacket;
import com.yukari.relicera.common.network.packet.SyncLittleTailorSeenThroughPacket;
import com.yukari.relicera.common.network.packet.SyncRippleheartRingStatePacket;
import com.yukari.relicera.common.network.packet.ThousandweightGauntletsSmashPacket;
import com.yukari.relicera.common.network.packet.UseTurncoatsMedalBannerPacket;
import com.yukari.relicera.common.network.packet.SelectItemContentsSlotPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetworking {
    private static final String PROTOCOL_VERSION = "10";
    private static int messageId;

    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private ModNetworking() {
    }

    public static void register() {
        CHANNEL.messageBuilder(SyncAstralObservationPacket.class, nextMessageId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncAstralObservationPacket::encode)
                .decoder(SyncAstralObservationPacket::decode)
                .consumerMainThread(SyncAstralObservationPacket::handle)
                .add();

        CHANNEL.messageBuilder(SyncFeysilverForgingKnowledgePacket.class, nextMessageId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncFeysilverForgingKnowledgePacket::encode)
                .decoder(SyncFeysilverForgingKnowledgePacket::decode)
                .consumerMainThread(SyncFeysilverForgingKnowledgePacket::handle)
                .add();

        CHANNEL.messageBuilder(SyncRippleheartRingStatePacket.class, nextMessageId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncRippleheartRingStatePacket::encode)
                .decoder(SyncRippleheartRingStatePacket::decode)
                .consumerMainThread(SyncRippleheartRingStatePacket::handle)
                .add();

        CHANNEL.messageBuilder(SyncLittleTailorSeenThroughPacket.class, nextMessageId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncLittleTailorSeenThroughPacket::encode)
                .decoder(SyncLittleTailorSeenThroughPacket::decode)
                .consumerMainThread(SyncLittleTailorSeenThroughPacket::handle)
                .add();

        CHANNEL.messageBuilder(SyncAttackStrengthPacket.class, nextMessageId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncAttackStrengthPacket::encode)
                .decoder(SyncAttackStrengthPacket::decode)
                .consumerMainThread(SyncAttackStrengthPacket::handle)
                .add();

        CHANNEL.messageBuilder(OpenAstralStorybookPacket.class, nextMessageId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenAstralStorybookPacket::encode)
                .decoder(OpenAstralStorybookPacket::decode)
                .consumerMainThread(OpenAstralStorybookPacket::handle)
                .add();

        CHANNEL.messageBuilder(ThousandweightGauntletsSmashPacket.class, nextMessageId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ThousandweightGauntletsSmashPacket::encode)
                .decoder(ThousandweightGauntletsSmashPacket::decode)
                .consumerMainThread(ThousandweightGauntletsSmashPacket::handle)
                .add();

        CHANNEL.messageBuilder(CameraShakePacket.class, nextMessageId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CameraShakePacket::encode)
                .decoder(CameraShakePacket::decode)
                .consumerMainThread(CameraShakePacket::handle)
                .add();

        CHANNEL.messageBuilder(CovenantToastPacket.class, nextMessageId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CovenantToastPacket::encode)
                .decoder(CovenantToastPacket::decode)
                .consumerMainThread(CovenantToastPacket::handle)
                .add();

        CHANNEL.messageBuilder(UseTurncoatsMedalBannerPacket.class, nextMessageId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(UseTurncoatsMedalBannerPacket::encode)
                .decoder(UseTurncoatsMedalBannerPacket::decode)
                .consumerMainThread(UseTurncoatsMedalBannerPacket::handle)
                .add();

        CHANNEL.messageBuilder(SelectItemContentsSlotPacket.class, nextMessageId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(SelectItemContentsSlotPacket::encode)
                .decoder(SelectItemContentsSlotPacket::decode)
                .consumerMainThread(SelectItemContentsSlotPacket::handle)
                .add();
    }

    public static void sendToPlayer(Object message, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToTracking(Object message, Entity entity) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }

    public static void sendToServer(Object message) {
        CHANNEL.sendToServer(message);
    }

    private static int nextMessageId() {
        return messageId++;
    }
}
