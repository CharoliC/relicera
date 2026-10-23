package com.yukari.relicera.common.item.feysilver;

import com.yukari.relicera.registry.ModItems;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;

public final class FeysilverSwordAnvil {
    private FeysilverSwordAnvil() {
    }

    public static void updateResult(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        if (event.isCanceled() || !left.is(ModItems.FEYSILVER_SWORD.get())) {
            return;
        }
        if (!event.getOutput().isEmpty()) {
            if (event.getOutput().is(ModItems.FEYSILVER_SWORD.get())) {
                event.getOutput().removeTagKey("RepairCost");
            }
            return;
        }

        ItemStack right = event.getRight();
        ItemStack output = left.copy();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(left);
        int operationCost = 0;
        int materialCost = 0;
        if (!right.isEmpty()) {
            boolean enchantedBook = right.is(Items.ENCHANTED_BOOK)
                    && !EnchantedBookItem.getEnchantments(right).isEmpty();
            if (output.isDamageableItem() && output.getItem().isValidRepairItem(left, right)) {
                int repair = output.getMaxDamage() / 4;
                if (!output.isDamaged() || repair <= 0) {
                    invalidate(event, 0);
                    return;
                }
                materialCost = Math.min(right.getCount(), (output.getDamageValue() + repair - 1) / repair);
                output = FeysilverSwordItem.createRepairedCopy(left, materialCost);
                operationCost += materialCost;
            } else {
                if (!enchantedBook && (!right.is(left.getItem()) || !output.isDamageableItem())) {
                    invalidate(event, 0);
                    return;
                }
                if (enchantedBook && !output.isBookEnchantable(right)) {
                    invalidate(event, 0);
                    return;
                }
                if (!enchantedBook) {
                    int remaining = left.getMaxDamage() - left.getDamageValue()
                            + right.getMaxDamage() - right.getDamageValue() + output.getMaxDamage() * 12 / 100;
                    int damage = Math.max(0, output.getMaxDamage() - remaining);
                    if (damage < output.getDamageValue()) {
                        output.setDamageValue(damage);
                        operationCost += 2;
                    }
                }

                boolean applied = false;
                boolean rejected = false;
                for (var entry : EnchantmentHelper.getEnchantments(right).entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    if (enchantment == null) {
                        continue;
                    }
                    if (!enchantment.canEnchant(left)) {
                        rejected = true;
                        continue;
                    }
                    int previousLevel = enchantments.getOrDefault(enchantment, 0);
                    int incomingLevel = entry.getValue();
                    int level = Math.min(enchantment.getMaxLevel(), previousLevel == incomingLevel
                            ? incomingLevel + 1 : Math.max(previousLevel, incomingLevel));
                    enchantments.put(enchantment, level);
                    int multiplier = switch (enchantment.getRarity()) {
                        case COMMON -> 1;
                        case UNCOMMON -> 2;
                        case RARE -> 4;
                        case VERY_RARE -> 8;
                    };
                    if (enchantedBook) {
                        multiplier = Math.max(1, multiplier / 2);
                    }
                    operationCost += multiplier * level;
                    applied = true;
                }
                if (rejected && !applied) {
                    invalidate(event, 0);
                    return;
                }
            }
        }

        int renameCost = applyName(event.getName(), left, output);
        operationCost += renameCost;
        int priorWorkCost = right.isEmpty() || right.is(ModItems.FEYSILVER_SWORD.get())
                ? 0 : right.getBaseRepairCost();
        int totalCost = priorWorkCost + operationCost;
        if (renameCost == operationCost && renameCost > 0 && totalCost >= 40) {
            totalCost = 39;
        }
        if (operationCost <= 0 || totalCost >= 40 && !event.getPlayer().getAbilities().instabuild) {
            invalidate(event, operationCost <= 0 ? 0 : totalCost);
            return;
        }

        output.removeTagKey("RepairCost");
        EnchantmentHelper.setEnchantments(enchantments, output);
        event.setOutput(output);
        event.setCost(totalCost);
        event.setMaterialCost(materialCost);
    }

    private static int applyName(String name, ItemStack input, ItemStack output) {
        if (name == null) {
            return 0;
        }
        if (Util.isBlank(name)) {
            if (input.hasCustomHoverName()) {
                output.resetHoverName();
                return 1;
            }
        } else if (!name.equals(input.getHoverName().getString())) {
            output.setHoverName(Component.literal(name));
            return 1;
        }
        return 0;
    }

    private static void invalidate(AnvilUpdateEvent event, int cost) {
        event.setCanceled(true);
        if (event.getPlayer().containerMenu instanceof AnvilMenu menu) {
            menu.getSlot(AnvilMenu.RESULT_SLOT).set(ItemStack.EMPTY);
            menu.setMaximumCost(cost);
            menu.repairItemCountCost = 0;
        }
    }
}
