package com.yukari.relicera.client.toast;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class ReliceraToast implements Toast {
    private static final long DISPLAY_TIME_MILLIS = 5000L;
    private final Component title;
    private final Component description;
    private final ItemStack icon;
    private final int width;

    private ReliceraToast(Component title, Component description, ItemStack icon) {
        this.title = title.copy().withStyle(ChatFormatting.LIGHT_PURPLE);
        this.description = description.copy().withStyle(ChatFormatting.WHITE);
        this.icon = icon.copy();
        var font = Minecraft.getInstance().font;
        width = Math.max(160, 38 + Math.max(font.width(this.title), font.width(this.description)));
    }

    public static void show(Component title, Component description, ItemStack icon) {
        Minecraft.getInstance().getToasts().addToast(new ReliceraToast(title, description, icon));
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long timeSinceVisible) {
        graphics.blit(TEXTURE, 0, 0, width, height(), 0.0F, 0.0F, 160, 32, 256, 256);
        graphics.renderFakeItem(icon, 8, 8);
        graphics.drawString(toastComponent.getMinecraft().font, title, 30, 7, 0xFFFFFF, false);
        graphics.drawString(toastComponent.getMinecraft().font, description, 30, 18, 0xFFFFFF, false);
        return timeSinceVisible < DISPLAY_TIME_MILLIS * toastComponent.getNotificationDisplayTimeMultiplier()
                ? Visibility.SHOW : Visibility.HIDE;
    }
}
