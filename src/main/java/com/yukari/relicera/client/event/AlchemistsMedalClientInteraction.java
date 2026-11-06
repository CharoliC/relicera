package com.yukari.relicera.client.event;

import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.common.network.packet.UseAlchemistsMedalGoldPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public final class AlchemistsMedalClientInteraction {
    private AlchemistsMedalClientInteraction() {
    }

    public static void send(Player player, Slot slot, int count, boolean goldOnCursor) {
        if (Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen
                && slot.container == player.getInventory()) {
            ModNetworking.sendToServer(new UseAlchemistsMedalGoldPacket(slot.getSlotIndex(), count, goldOnCursor));
        }
    }
}
