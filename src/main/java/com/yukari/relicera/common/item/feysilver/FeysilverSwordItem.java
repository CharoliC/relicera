package com.yukari.relicera.common.item.feysilver;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public final class FeysilverSwordItem extends SwordItem {
    public FeysilverSwordItem(Properties properties) {
        super(Tiers.NETHERITE, 2, -2.4F, properties);
    }

    @Override
    public int getEnchantmentValue() {
        return 20;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(Items.NETHERITE_SCRAP);
    }

    public static ItemStack createRepairedCopy(ItemStack stack, int materialCount) {
        ItemStack output = stack.copy();
        output.setDamageValue(Math.max(0, stack.getDamageValue() - stack.getMaxDamage() / 4 * materialCount));
        output.removeTagKey("RepairCost");
        return output;
    }
}
