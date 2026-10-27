package com.yukari.relicera.common.item.feysilver;

import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.common.network.packet.ForgingLearnedToastPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FeysilverForgingArtVolumeOneItem extends Item {
    public FeysilverForgingArtVolumeOneItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        if (FeysilverForgingKnowledge.hasVolumeOne(player)) {
            return InteractionResultHolder.fail(stack);
        }

        FeysilverForgingKnowledge.learnVolumeOne(player);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.playNotifySound(SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.0F);
            serverPlayer.playNotifySound(SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            spawnLearnParticles(serverPlayer);
            ModNetworking.sendToPlayer(new ForgingLearnedToastPacket(), serverPlayer);
        }
        return InteractionResultHolder.consume(stack);
    }

    private static void spawnLearnParticles(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        level.sendParticles(
                ParticleTypes.ENCHANT,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                36,
                0.75D,
                0.8D,
                0.75D,
                0.1D
        );
    }
}
