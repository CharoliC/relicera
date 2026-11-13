package com.yukari.relicera.common.item;

import com.yukari.relicera.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class RippleheartRingsItem extends Item {
    public RippleheartRingsItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(ItemTooltipText.flavor("rippleheart_rings", 0,
                Component.translatable("tooltip.relicera.rippleheart_ring.0"),
                Component.translatable("tooltip.relicera.rippleheart_ring.1")
        ));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack result = stack;
        if (!level.isClientSide()) {
            UUID pairId = RippleheartRingItem.getPairId(stack).orElseGet(UUID::randomUUID);
            ItemStack redRing = ModItems.RIPPLEHEART_RING_RED.get().createBoundStack(pairId);
            ItemStack blueRing = ModItems.RIPPLEHEART_RING_BLUE.get().createBoundStack(pairId);

            if (player.getAbilities().instabuild) {
                giveOrDrop(player, redRing);
            } else {
                result = redRing;
                player.setItemInHand(hand, result);
            }
            giveOrDrop(player, blueRing);
            player.awardStat(Stats.ITEM_USED.get(this));
        }

        return InteractionResultHolder.sidedSuccess(result, level.isClientSide());
    }

    private static void giveOrDrop(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
