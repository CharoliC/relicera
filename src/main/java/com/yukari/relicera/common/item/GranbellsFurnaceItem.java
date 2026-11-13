package com.yukari.relicera.common.item;

import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public final class GranbellsFurnaceItem extends RelicCurioItem {
    public GranbellsFurnaceItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.relic("granbells_furnace", 0));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("granbells_furnace", 1));
        tooltip.add(ItemTooltipText.curioEffect("granbells_furnace", 2));
        tooltip.add(ItemTooltipText.curioEffect("granbells_furnace", 3));
        tooltip.add(ItemTooltipText.curioEffect("granbells_furnace", 4));
        tooltip.add(ItemTooltipText.curioEffect("granbells_furnace", 5));
        tooltip.add(ItemTooltipText.curioEffect("granbells_furnace", 6));
        tooltip.add(ItemTooltipText.curioEffect("granbells_furnace", 7));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioModifiersHeader("relicera_relic"));
        tooltip.add(ItemTooltipText.description("granbells_furnace", 8,
                ItemTooltipText.signedNumber(ModCommonConfig.GRANBELLS_FURNACE_DAMAGE_BONUS.get())));
    }
}
