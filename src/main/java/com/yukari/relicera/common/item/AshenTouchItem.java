package com.yukari.relicera.common.item;

import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class AshenTouchItem extends QuickEquipCurioItem {
    public AshenTouchItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("ashen_touch", 0));
        tooltip.add(ItemTooltipText.curioEffect("ashen_touch", 1,
                ItemTooltipText.signedPercent(ModCommonConfig.ASHEN_TOUCH_BURNING_TARGET_DAMAGE_BONUS.get())));
    }
}
