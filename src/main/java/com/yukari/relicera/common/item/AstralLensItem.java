package com.yukari.relicera.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import com.yukari.relicera.common.astral.AstralLensEntityBehavior;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class AstralLensItem extends EnvironmentallyIndestructibleItem {
    public AstralLensItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(ItemTooltipText.flavor("astral_lens", 0));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.heading("astral_lens", 1));
        tooltip.add(ItemTooltipText.description("astral_lens", 2, "-85%"));
        tooltip.add(ItemTooltipText.description("astral_lens", 3, "-30%"));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity itemEntity) {
        return AstralLensEntityBehavior.tick(itemEntity);
    }
}
