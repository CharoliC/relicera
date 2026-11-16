package com.yukari.relicera.common.enchantment;

import com.yukari.relicera.common.item.LumenItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public final class StarcrownEnchantment extends Enchantment {
    public StarcrownEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof LumenItem;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }
}
