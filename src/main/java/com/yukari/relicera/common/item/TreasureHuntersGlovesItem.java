package com.yukari.relicera.common.item;

import com.yukari.relicera.common.curio.TreasureHuntersGlovesEffects;
import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public final class TreasureHuntersGlovesItem extends QuickEquipCurioItem {
    public TreasureHuntersGlovesItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("treasure_hunters_gloves", 0));
        tooltip.add(ItemTooltipText.curioEffect("treasure_hunters_gloves", 1));
        tooltip.add(ItemTooltipText.curioEffect("treasure_hunters_gloves", 2,
                String.valueOf(ModCommonConfig.TREASURE_HUNTERS_GLOVES_MAX_ITEMS_TAKEN_BEFORE_REFRESH.get())));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioModifiersHeader("hands"));
        tooltip.add(ItemTooltipText.description("treasure_hunters_gloves", 3,
                ItemTooltipText.signedDecimal(TreasureHuntersGlovesEffects.getLuckBonus(stack))));
    }
}
