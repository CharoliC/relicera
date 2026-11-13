package com.yukari.relicera.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ViewerTooltipItem {
    void appendViewerTooltip(ItemStack stack, @Nullable Player player, List<Component> tooltip);
}
