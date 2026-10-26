package com.yukari.relicera.common.item.feysilver;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public final class FeysilverMiningContext implements AutoCloseable {
    private static final ThreadLocal<FeysilverMiningContext> CURRENT = new ThreadLocal<>();
    private final FeysilverMiningContext previous;
    private final ItemStack tool;
    private final ItemStack snapshot;
    private final boolean silkTouch;

    private FeysilverMiningContext(ItemStack tool, boolean silkTouch) {
        this.previous = CURRENT.get();
        this.tool = tool;
        this.snapshot = tool.copy();
        this.silkTouch = silkTouch;
        CURRENT.set(this);
    }

    public static FeysilverMiningContext begin(ServerPlayer player) {
        return new FeysilverMiningContext(player.getMainHandItem(), player.isShiftKeyDown());
    }

    public static boolean isSuppressed(ItemStack stack, Enchantment enchantment) {
        if (enchantment != Enchantments.BLOCK_FORTUNE && enchantment != Enchantments.SILK_TOUCH) {
            return false;
        }
        FeysilverMiningContext context = CURRENT.get();
        return context != null && stack.getItem() instanceof FeysilverPickaxeItem
                && (stack == context.tool || ItemStack.isSameItemSameTags(stack, context.snapshot))
                && EnchantmentHelper.getTagEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack) > 0
                && EnchantmentHelper.getTagEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0
                && enchantment == (context.silkTouch ? Enchantments.BLOCK_FORTUNE : Enchantments.SILK_TOUCH);
    }

    @Override
    public void close() {
        if (previous == null) {
            CURRENT.remove();
        } else {
            CURRENT.set(previous);
        }
    }
}
