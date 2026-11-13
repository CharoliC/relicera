package com.yukari.relicera.common.item;

import com.yukari.relicera.common.curio.CurativeTalismanEffects;
import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class SufferersExultationItem extends QuickEquipCurioItem implements ViewerTooltipItem {
    public SufferersExultationItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendViewerTooltip(ItemStack stack, @Nullable Player player, List<Component> tooltip) {
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("sufferers_exultation", 0));
        tooltip.add(ItemTooltipText.curioEffect("sufferers_exultation", 1));
        tooltip.add(ItemTooltipText.curioEffect("sufferers_exultation", 2));

        int count = player == null ? 0 : CurativeTalismanEffects.countHarmfulEffects(player);
        double damage = count * ModCommonConfig.SUFFERERS_EXULTATION_DAMAGE_BONUS_PER_EFFECT.get();
        double critical = count * ModCommonConfig.SUFFERERS_EXULTATION_CRITICAL_BONUS_PER_EFFECT.get();
        double taken = count * ModCommonConfig.SUFFERERS_EXULTATION_DAMAGE_TAKEN_PER_EFFECT.get();
        if (damage != 0.0D || critical != 0.0D || taken != 0.0D) {
            tooltip.add(Component.empty());
            tooltip.add(ItemTooltipText.curioModifiersHeader("charm"));
            if (damage != 0.0D) {
                tooltip.add(ItemTooltipText.description("sufferers_exultation", 3,
                        ItemTooltipText.signedPercent(damage)));
            }
            if (critical != 0.0D) {
                tooltip.add(ItemTooltipText.description("sufferers_exultation", 4,
                        ItemTooltipText.signedPercent(critical)));
            }
            if (taken != 0.0D) {
                tooltip.add(ItemTooltipText.negativeStat("sufferers_exultation", 5,
                        ItemTooltipText.signedPercent(taken)));
            }
        }
    }
}
