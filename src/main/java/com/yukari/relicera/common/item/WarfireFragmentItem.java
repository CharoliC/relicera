package com.yukari.relicera.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import net.minecraft.world.item.Item;

public final class WarfireFragmentItem extends Item {
    public WarfireFragmentItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(ItemTooltipText.flavor("warfire_fragment", 0));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.heading("warfire_fragment", 1));
        tooltip.add(ItemTooltipText.description("warfire_fragment", 2));
    }
}
