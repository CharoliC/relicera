package com.yukari.relicera.common.item.feysilver;

import java.util.Map;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;

public final class FeysilverPickaxeItem extends PickaxeItem {
    public FeysilverPickaxeItem(Properties properties) {
        super(Tiers.NETHERITE, 1, -2.8F, properties);
    }

    @Override
    public int getEnchantmentValue() {
        return 20;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE) ? 10.0F : super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(Items.NETHERITE_SCRAP);
    }

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        return FeysilverMiningContext.isSuppressed(stack, enchantment)
                ? 0 : EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack);
    }

    @Override
    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTags());
        enchantments.keySet().removeIf(enchantment -> FeysilverMiningContext.isSuppressed(stack, enchantment));
        return enchantments;
    }
}
