package com.yukari.relicera.common.item;

import com.yukari.relicera.common.item.armor.devilsbearskin.DevilsBearskinEffects;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class SoulRestoringStewItem extends BowlFoodItem {
    private static final int EFFECT_DURATION_TICKS = 10 * 20;

    public SoulRestoringStewItem(Properties properties) {
        super(properties);
    }

    public static FoodProperties createFoodProperties() {
        return new FoodProperties.Builder()
                .nutrition(7)
                .saturationMod(0.8F)
                .alwaysEat()
                .effect(SoulRestoringStewItem::createRegenerationEffect, 1.0F)
                .effect(SoulRestoringStewItem::createDarknessEffect, 1.0F)
                .build();
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack remaining = super.finishUsingItem(stack, level, entity);
        if (entity instanceof ServerPlayer player) {
            DevilsBearskinEffects.restoreSoul(player);
            SoulRestoringStewEffects.startHeartbeat(player);
        }
        return remaining;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        PotionUtils.addPotionTooltip(List.of(createRegenerationEffect(), createDarknessEffect()), tooltip, 1.0F);
    }

    private static MobEffectInstance createRegenerationEffect() {
        return new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION_TICKS, 1);
    }

    private static MobEffectInstance createDarknessEffect() {
        return new MobEffectInstance(MobEffects.DARKNESS, EFFECT_DURATION_TICKS);
    }
}
