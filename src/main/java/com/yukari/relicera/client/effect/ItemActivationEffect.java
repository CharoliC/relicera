package com.yukari.relicera.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public final class ItemActivationEffect {
    private ItemActivationEffect() {
    }

    public static void show(ItemStack stack) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && !stack.isEmpty()) {
            minecraft.gameRenderer.displayItemActivation(stack);
        }
    }
}
