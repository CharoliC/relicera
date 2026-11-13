package com.yukari.relicera.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import com.yukari.relicera.common.curio.LittleTailorsBeltEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class LittleTailorsBeltItem extends QuickEquipCurioItem {
    public LittleTailorsBeltItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("little_tailors_belt", 0));
        tooltip.add(ItemTooltipText.curioEffect("little_tailors_belt", 1));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.quote("little_tailors_belt", 2));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity wearer = slotContext.entity();
        if (!wearer.level().isClientSide && wearer.tickCount % 5 == 0) {
            LittleTailorsBeltEffects.attackNearbyPests(wearer);
        }
    }
}
