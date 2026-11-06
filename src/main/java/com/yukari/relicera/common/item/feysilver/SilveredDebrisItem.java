package com.yukari.relicera.common.item.feysilver;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class SilveredDebrisItem extends BlockItem {
    public SilveredDebrisItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return FeysilverForgingClientData.materialName(super.getName(stack));
    }
}
