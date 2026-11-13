package com.yukari.relicera.common.item;

import com.yukari.relicera.common.effect.ephemeralbloom.EphemeralBloomAbsorption;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class EphemeralBloomPendantItem extends QuickEquipCurioItem {
    public EphemeralBloomPendantItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("ephemeral_bloom_pendant", 0));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide || entity.tickCount % EphemeralBloomAbsorption.intervalTicks() != 0) {
            return;
        }
        EphemeralBloomAbsorption.grant(entity);
    }
}
