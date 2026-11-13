package com.yukari.relicera.common.item;

import com.yukari.relicera.common.curio.MasterSmithsBroochEffects;
import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MasterSmithsBroochItem extends QuickEquipCurioItem implements ViewerTooltipItem {
    public MasterSmithsBroochItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendViewerTooltip(ItemStack stack, @Nullable Player player, List<Component> tooltip) {
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("master_smiths_brooch", 0,
                ItemTooltipText.signedNumber(ModCommonConfig.MASTER_SMITHS_BROOCH_ARMOR_TOUGHNESS_PER_TRIMMED_ARMOR.get())));
        tooltip.add(ItemTooltipText.curioEffect("master_smiths_brooch", 1,
                ItemTooltipText.signedPercent(ModCommonConfig.MASTER_SMITHS_BROOCH_DAMAGE_REDUCTION_PER_COATED_ARMOR.get())));

        double toughness = player == null ? 0.0D : MasterSmithsBroochEffects.getArmorToughnessBonus(player);
        double reduction = player == null ? 0.0D : MasterSmithsBroochEffects.getDamageReduction(player);
        if (toughness != 0.0D || reduction != 0.0D) {
            tooltip.add(Component.empty());
            tooltip.add(ItemTooltipText.curioModifiersHeader("relicera_brooch"));
            if (toughness != 0.0D) {
                tooltip.add(ItemTooltipText.description("master_smiths_brooch", 2,
                        ItemTooltipText.signedNumber(toughness)));
            }
            if (reduction != 0.0D) {
                tooltip.add(ItemTooltipText.description("master_smiths_brooch", 3,
                        ItemTooltipText.signedPercent(reduction)));
            }
        }
    }
}
