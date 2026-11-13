package com.yukari.relicera.common.item;

import com.yukari.relicera.common.curio.BrutalPlunderBadgeEffects;
import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class BrutalPlunderBadgeItem extends QuickEquipCurioItem implements ViewerTooltipItem {
    public BrutalPlunderBadgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendViewerTooltip(ItemStack stack, @Nullable Player player, List<Component> tooltip) {
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("brutal_plunder_badge", 0));
        tooltip.add(ItemTooltipText.curioEffect("brutal_plunder_badge", 1));
        tooltip.add(ItemTooltipText.curioEffect("brutal_plunder_badge", 2));

        int looting = ModCommonConfig.BRUTAL_PLUNDER_BADGE_LOOTING_BONUS.get();
        double damage = BrutalPlunderBadgeEffects.getDamageBonus(getTooltipLootingLevel(player));
        if (looting != 0 || damage != 0.0D) {
            tooltip.add(Component.empty());
            tooltip.add(ItemTooltipText.curioModifiersHeader("charm"));
            if (looting != 0) {
                tooltip.add(ItemTooltipText.description("brutal_plunder_badge", 3,
                        ItemTooltipText.signedNumber(looting)));
            }
            if (damage != 0.0D) {
                tooltip.add(ItemTooltipText.description("brutal_plunder_badge", 4,
                        ItemTooltipText.signedPercent(damage)));
            }
        }
    }

    private static int getTooltipLootingLevel(@Nullable Player player) {
        if (player == null || !BrutalPlunderBadgeEffects.isEquipped(player)) {
            return 0;
        }

        int baseLooting = EnchantmentHelper.getMobLooting(player);
        int curiosLooting = CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler -> handler.getLootingLevel(player.damageSources().playerAttack(player), player, baseLooting))
                .orElse(0);
        return baseLooting + curiosLooting;
    }

    @Override
    public int getLootingLevel(SlotContext slotContext, DamageSource source, LivingEntity target,
                              int baseLooting, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player) || slotContext.cosmetic()) {
            return 0;
        }
        return CuriosApi.getCuriosInventory(slotContext.entity()).resolve()
                .map(handler -> handler.findFirstCurio(this)
                        .filter(result -> result.slotContext().identifier().equals(slotContext.identifier())
                                && result.slotContext().index() == slotContext.index())
                        .map(result -> ModCommonConfig.BRUTAL_PLUNDER_BADGE_LOOTING_BONUS.get())
                        .orElse(0))
                .orElse(0);
    }
}
