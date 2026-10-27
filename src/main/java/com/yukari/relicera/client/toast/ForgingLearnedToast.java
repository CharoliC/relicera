package com.yukari.relicera.client.toast;

import com.yukari.relicera.registry.ModItems;
import net.minecraft.network.chat.Component;

public final class ForgingLearnedToast {
    private ForgingLearnedToast() {
    }

    public static void show() {
        ReliceraToast.show(Component.translatable("toast.relicera.forging_learned.title"),
                Component.translatable("toast.relicera.forging_learned.description"),
                ModItems.FEYSILVER_FORGING_ART_VOLUME_ONE.get().getDefaultInstance());
    }
}
