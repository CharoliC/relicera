package com.yukari.relicera.common.item;

import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class StriderSpursItem extends QuickEquipCurioItem {
    public StriderSpursItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioModifiersHeader("charm"));
        tooltip.add(ItemTooltipText.description("strider_spurs", 0,
                ItemTooltipText.signedPercent(ModCommonConfig.STRIDER_SPURS_SPEED_BONUS.get())));
    }
}
