package com.yukari.relicera.common.item;

import com.yukari.relicera.common.compat.irons_spellbooks.ShadowManastoneCharmCompat;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public final class ShadowManastoneCharmItem extends QuickEquipCurioItem implements ViewerTooltipItem {
    public ShadowManastoneCharmItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return "necklace".equals(context.identifier()) && super.canEquip(context, stack);
    }

    @Override
    public void appendViewerTooltip(ItemStack stack, @Nullable Player player, List<Component> tooltip) {
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("shadow_manastone_charm", 0));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioModifiersHeader("necklace"));
        tooltip.add(ItemTooltipText.description("shadow_manastone_charm", 1));
        double power = player == null ? 0 : ShadowManastoneCharmCompat.getSpellPowerBonus(player);
        if (power > 0) {
            tooltip.add(ItemTooltipText.description("shadow_manastone_charm", 2, ItemTooltipText.signedPercent(power)));
        }
    }
}
