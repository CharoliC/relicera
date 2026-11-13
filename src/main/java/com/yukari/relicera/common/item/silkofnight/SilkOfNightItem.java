package com.yukari.relicera.common.item.silkofnight;

import com.yukari.relicera.common.item.ItemTooltipText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import net.minecraft.world.item.Item;

public final class SilkOfNightItem extends Item {
    public SilkOfNightItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(ItemTooltipText.flavor("silk_of_night", 0));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.heading("silk_of_night", 1));
        tooltip.add(ItemTooltipText.description("silk_of_night", 2));
        tooltip.add(ItemTooltipText.description("silk_of_night", 3));
    }
}
