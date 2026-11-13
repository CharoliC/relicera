package com.yukari.relicera.common.item;

import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class NightGlovesItem extends QuickEquipCurioItem {
    public NightGlovesItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("night_gloves", 0));
        tooltip.add(ItemTooltipText.curioEffect("night_gloves", 1));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioModifiersHeader("hands"));
        tooltip.add(ItemTooltipText.description("night_gloves", 2,
                ItemTooltipText.signedPercent(ModCommonConfig.NIGHT_GLOVES_NIGHT_ATTACK_DAMAGE_BONUS.get())));
    }
}
