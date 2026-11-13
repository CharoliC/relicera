package com.yukari.relicera.common.item;

import com.yukari.relicera.common.curio.NereiasCrownEffects;
import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NereiasCrownItem extends RelicCurioItem implements ViewerTooltipItem {
    public NereiasCrownItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendViewerTooltip(ItemStack stack, @Nullable Player player, List<Component> tooltip) {
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.relic("nereias_crown", 0));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("nereias_crown", 1));
        tooltip.add(ItemTooltipText.curioEffect("nereias_crown", 2));
        tooltip.add(ItemTooltipText.curioEffect("nereias_crown", 3));
        tooltip.add(ItemTooltipText.curioEffect("nereias_crown", 4,
                "-" + ItemTooltipText.unsignedPercent(ModCommonConfig.NEREIAS_CROWN_ACTIVE_DAMAGE_REDUCTION.get())));
        tooltip.add(ItemTooltipText.curioEffect("nereias_crown", 5));
        tooltip.add(ItemTooltipText.curioEffect("nereias_crown", 6));
        tooltip.add(ItemTooltipText.curioEffect("nereias_crown", 7,
                ItemTooltipText.unsignedPercent(ModCommonConfig.NEREIAS_CROWN_AQUATIC_ATTACK_DAMAGE_SHARE.get())));
        tooltip.add(ItemTooltipText.curioEffect("nereias_crown", 8,
                ItemTooltipText.unsignedPercent(ModCommonConfig.NEREIAS_CROWN_AQUATIC_MAX_HEALTH_ARMOR_SHARE.get())));
        double damageBonus = player == null ? 0.0D : NereiasCrownEffects.getAquaticAttackDamageBonus(player);
        double armorBonus = player == null ? 0.0D : NereiasCrownEffects.getAquaticArmorBonus(player);
        if (damageBonus != 0.0D || armorBonus != 0.0D) {
            tooltip.add(Component.empty());
            tooltip.add(ItemTooltipText.curioModifiersHeader("relicera_relic"));
            if (damageBonus != 0.0D) {
                tooltip.add(ItemTooltipText.description("nereias_crown", 9, ItemTooltipText.signedNumber(damageBonus)));
            }
            if (armorBonus != 0.0D) {
                tooltip.add(ItemTooltipText.description("nereias_crown", 10, ItemTooltipText.signedNumber(armorBonus)));
            }
        }
    }
}
