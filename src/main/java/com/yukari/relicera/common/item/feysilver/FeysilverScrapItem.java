package com.yukari.relicera.common.item.feysilver;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class FeysilverScrapItem extends Item {
    public FeysilverScrapItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return FeysilverForgingClientData.materialName(super.getName(stack));
    }
}
