package com.yukari.relicera.common.item.feysilver;

import com.yukari.relicera.common.item.ItemTooltipText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FeysilverIngotItem extends Item {
    public FeysilverIngotItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.heading("feysilver_ingot", 0));
        tooltip.add(ItemTooltipText.description("feysilver_ingot", 1));
    }
}
