package com.yukari.relicera.common.item;

import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public final class VerdantScaleBeltItem extends QuickEquipCurioItem {
    public VerdantScaleBeltItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("verdant_scale_belt", 0));
        tooltip.add(ItemTooltipText.curioEffect("verdant_scale_belt", 1));
        tooltip.add(ItemTooltipText.curioEffect("verdant_scale_belt", 2));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioModifiersHeader("belt"));
        tooltip.add(ItemTooltipText.description("verdant_scale_belt", 3,
                ItemTooltipText.signedNumber(ModCommonConfig.VERDANT_SCALE_BELT_ARMOR_TOUGHNESS.get())));
    }
}
