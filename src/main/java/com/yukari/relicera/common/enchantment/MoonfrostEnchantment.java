package com.yukari.relicera.common.enchantment;

import com.yukari.relicera.common.item.LumenItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public final class MoonfrostEnchantment extends Enchantment {
    public MoonfrostEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinCost(int level) {
        return 10 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 15;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof LumenItem;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof LumenItem;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }
}
