package com.yukari.relicera.client.toast;

import com.yukari.relicera.common.curio.CurioSlotUnlocks;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class SlotUnlockToast {
    private SlotUnlockToast() {
    }

    public static void show(CurioSlotUnlocks.Reward reward) {
        String slot = switch (reward) {
            case SIRENS_EYE -> "ring";
            case ASPHODEL_BLOOM -> "charm";
        };
        ItemStack icon = switch (reward) {
            case SIRENS_EYE -> ModItems.SIRENS_EYE.get().getDefaultInstance();
            case ASPHODEL_BLOOM -> ModItems.ASPHODEL_BLOOM.get().getDefaultInstance();
        };
        ReliceraToast.show(Component.translatable("toast.relicera.slot_unlocked.title"),
                Component.translatable("toast.relicera.slot_unlocked." + slot), icon);
    }
}
