package com.yukari.relicera.common.item;

import com.yukari.relicera.common.block.SculkFruitCropBlock;
import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SculkFruitItem extends Item {
    private static final int DARKNESS_DURATION_TICKS = 10 * 20;

    public SculkFruitItem(Properties properties) {
        super(properties);
    }

    public static FoodProperties createFoodProperties() {
        return new FoodProperties.Builder()
                .nutrition(2)
                .saturationMod(0.9F)
                .alwaysEat()
                .build();
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack remaining = super.finishUsingItem(stack, level, entity);
        if (level instanceof ServerLevel serverLevel && entity instanceof ServerPlayer player) {
            player.addEffect(createDarknessEffect());
            Vec3 center = player.getBoundingBox().getCenter();
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, center.x, center.y, center.z,
                    12, 1.2D, player.getBbHeight() * 0.4D, 1.2D, 0.02D);
            int experience = ModCommonConfig.SCULK_FRUIT_EXPERIENCE_PER_STAGE.get()
                    * SculkFruitCropBlock.MAX_AGE + 100;
            ExperienceOrb.award(serverLevel, center, experience);
        }
        return remaining;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        PotionUtils.addPotionTooltip(List.of(createDarknessEffect()), tooltip, 1.0F);
    }

    private static MobEffectInstance createDarknessEffect() {
        return new MobEffectInstance(MobEffects.DARKNESS, DARKNESS_DURATION_TICKS);
    }
}
