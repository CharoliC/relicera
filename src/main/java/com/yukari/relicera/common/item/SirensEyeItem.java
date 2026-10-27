package com.yukari.relicera.common.item;

import com.yukari.relicera.common.curio.CurioSlotUnlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SirensEyeItem extends Item {
    public SirensEyeItem(Properties properties) {
        super(properties);
    }

    public static FoodProperties createFoodProperties() {
        return new FoodProperties.Builder()
                .nutrition(4)
                .saturationMod(1.8F)
                .alwaysEat()
                .effect(() -> new MobEffectInstance(MobEffects.CONDUIT_POWER, 210 * 20, 0), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.LUCK, 480 * 20, 3), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 20 * 20, 1), 1.0F)
                .build();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.relicera.sirens_eye.0"));
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.relicera.sirens_eye.1"));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack remaining = super.finishUsingItem(stack, level, entity);
        if (entity instanceof ServerPlayer player) {
            CurioSlotUnlocks.unlock(player, CurioSlotUnlocks.Reward.SIRENS_EYE);
        }
        return remaining;
    }
}
