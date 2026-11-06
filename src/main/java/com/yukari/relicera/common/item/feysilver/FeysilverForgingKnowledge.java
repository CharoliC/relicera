package com.yukari.relicera.common.item.feysilver;

import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.common.network.packet.SyncFeysilverForgingKnowledgePacket;
import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.EnumSet;

public final class FeysilverForgingKnowledge {
    private static final String DATA_KEY = "ReliceraFeysilverForging";
    private static final int EXPERIENCE_LEVEL_COST = 39;

    private FeysilverForgingKnowledge() {
    }

    public static boolean hasLearned(Player player, ForgingVolume volume) {
        return player.getPersistentData().getCompound(DATA_KEY).getBoolean(volume.saveKey());
    }

    public static boolean canUseSilveringSolution(Player player) {
        return player.level().isClientSide() ? FeysilverForgingClientData.hasLearned(ForgingVolume.TWO)
                : hasLearned(player, ForgingVolume.TWO);
    }

    public static void learn(Player player, ForgingVolume volume) {
        getOrCreateData(player).putBoolean(volume.saveKey(), true);
        if (player instanceof ServerPlayer serverPlayer) {
            sync(serverPlayer);
        }
    }

    public static void copy(Player original, Player replacement) {
        CompoundTag root = original.getPersistentData();
        if (root.contains(DATA_KEY, Tag.TAG_COMPOUND)) {
            replacement.getPersistentData().put(DATA_KEY, root.getCompound(DATA_KEY).copy());
        }
    }

    public static void sync(ServerPlayer player) {
        EnumSet<ForgingVolume> learned = EnumSet.noneOf(ForgingVolume.class);
        for (ForgingVolume volume : ForgingVolume.values()) {
            if (hasLearned(player, volume)) {
                learned.add(volume);
            }
        }
        ModNetworking.sendToPlayer(new SyncFeysilverForgingKnowledgePacket(learned), player);
    }

    public static void updateAnvilResult(AnvilUpdateEvent event) {
        Player player = event.getPlayer();
        if (player == null || !hasLearned(player, ForgingVolume.ONE)) {
            return;
        }

        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (!right.is(ModItems.FEYSILVER_INGOT.get()) || !canForge(left)) {
            return;
        }

        ItemStack output = left.copy();
        output.setDamageValue(0);
        FeysilverCoating.apply(output);
        event.setOutput(output);
        event.setCost(EXPERIENCE_LEVEL_COST);
        event.setMaterialCost(1);
    }

    private static boolean canForge(ItemStack stack) {
        return !stack.isEmpty()
                && stack.isDamageableItem()
                && !FeysilverCoating.isUnbreakable(stack)
                && !isBlacklisted(stack);
    }

    private static boolean isBlacklisted(ItemStack stack) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return itemId != null
                && ModCommonConfig.FEYSILVER_FORGING_ART_VOLUME_ONE_ITEM_BLACKLIST.get().contains(itemId.toString());
    }

    private static CompoundTag getOrCreateData(Player player) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(DATA_KEY, Tag.TAG_COMPOUND)) {
            root.put(DATA_KEY, new CompoundTag());
        }
        return root.getCompound(DATA_KEY);
    }
}
