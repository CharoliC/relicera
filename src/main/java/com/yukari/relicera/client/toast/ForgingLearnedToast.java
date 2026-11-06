package com.yukari.relicera.client.toast;

import com.yukari.relicera.common.item.feysilver.ForgingVolume;
import net.minecraft.network.chat.Component;

public final class ForgingLearnedToast {
    private ForgingLearnedToast() {
    }

    public static void show(ForgingVolume volume) {
        ReliceraToast.show(Component.translatable("toast.relicera.forging_learned.title"),
                Component.translatable(volume.toastDescriptionKey()), volume.book().getDefaultInstance());
    }
}
