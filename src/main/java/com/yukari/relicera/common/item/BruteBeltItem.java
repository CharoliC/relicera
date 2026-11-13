package com.yukari.relicera.common.item;

import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public final class BruteBeltItem extends QuickEquipCurioItem {
    public BruteBeltItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("brute_belt", 0));
        tooltip.add(ItemTooltipText.curioEffect("brute_belt", 1));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioModifiersHeader("belt"));
        tooltip.add(ItemTooltipText.description("brute_belt", 2,
                ItemTooltipText.signedPercent(ModCommonConfig.BRUTE_BELT_MAX_HEALTH_BONUS.get())));
    }
}
