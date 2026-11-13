package com.yukari.relicera.common.item;

import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public final class TwoHandedSwordBroochItem extends QuickEquipCurioItem {
    public TwoHandedSwordBroochItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("two_handed_sword_brooch", 0));
        tooltip.add(ItemTooltipText.curioEffect("two_handed_sword_brooch", 1,
                ItemTooltipText.signedPercent(ModCommonConfig.TWO_HANDED_SWORD_BROOCH_SWORD_DAMAGE_BONUS.get()),
                ItemTooltipText.signedPercent(-ModCommonConfig.TWO_HANDED_SWORD_BROOCH_ATTACK_SPEED_PENALTY.get())
        ));
        tooltip.add(ItemTooltipText.curioEffect("two_handed_sword_brooch", 2));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioModifiersHeader("relicera_brooch"));
        tooltip.add(ItemTooltipText.description("two_handed_sword_brooch", 3,
                ItemTooltipText.signedNumber(ModCommonConfig.TWO_HANDED_SWORD_BROOCH_ENTITY_REACH_BONUS.get())
        ));
    }
}
