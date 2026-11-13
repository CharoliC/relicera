package com.yukari.relicera.common.item;

import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public final class WarriorBeltItem extends QuickEquipCurioItem {
    public WarriorBeltItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("curios.modifiers.belt").withStyle(ChatFormatting.GOLD));
        tooltip.add(ItemTooltipText.description("warrior_belt", 0,
                ItemTooltipText.signedPercent(ModCommonConfig.WARRIOR_BELT_CRITICAL_DAMAGE_BONUS.get())));
        tooltip.add(ItemTooltipText.description("warrior_belt", 1,
                ItemTooltipText.signedPercent(ModCommonConfig.WARRIOR_BELT_KNOCKBACK_RESISTANCE.get())));
        tooltip.add(ItemTooltipText.description("warrior_belt", 2,
                ItemTooltipText.signedPercent(ModCommonConfig.WARRIOR_BELT_MOVEMENT_SPEED_BONUS.get())));
    }
}
