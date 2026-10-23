package com.yukari.relicera.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class AultiasBloodItem extends EnvironmentallyIndestructibleItem {
    public AultiasBloodItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(ChatFormatting.OBFUSCATED);
    }
}
