package com.yukari.relicera.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public final class ItemTooltipText {
    private static final int EFFECT_COLOR = 0x888888;

    private ItemTooltipText() {
    }

    public static Component curioEffectsHeader() {
        return curioEffectsHeader(0);
    }

    public static Component curioEffectsHeader(int line, Object... args) {
        return Component.translatable("tooltip.relicera.curio." + line, args).withStyle(ChatFormatting.GOLD);
    }

    public static Component armorEffectsHeader() {
        return Component.translatable("tooltip.relicera.armor.0").withStyle(ChatFormatting.GOLD);
    }

    public static Component negativeStat(String itemId, int line, Object... args) {
        return Component.translatable("tooltip.relicera." + itemId + "." + line, args).withStyle(ChatFormatting.RED);
    }

    public static Component curioModifiersHeader(String slot) {
        return Component.translatable("curios.modifiers." + slot).withStyle(ChatFormatting.GOLD);
    }

    public static Component curioEffect(String itemId, int line, Object... args) {
        return Component.literal("- ").withStyle(ChatFormatting.DARK_PURPLE)
                .append(Component.translatable("tooltip.relicera." + itemId + "." + line, args));
    }

    public static Component effect(String itemId, int line, Object... args) {
        return bullet(itemId, line, Style.EMPTY.withColor(EFFECT_COLOR), args);
    }

    public static Component stat(String itemId, int line, Object... args) {
        return Component.translatable("tooltip.relicera." + itemId + "." + line, args)
                .withStyle(Style.EMPTY.withColor(EFFECT_COLOR));
    }

    public static Component description(String itemId, int line, Object... args) {
        return Component.translatable("tooltip.relicera." + itemId + "." + line, args).withStyle(ChatFormatting.BLUE);
    }

    public static Component flavor(String itemId, int line, Object... args) {
        return Component.translatable("tooltip.relicera." + itemId + "." + line, args).withStyle(ChatFormatting.DARK_GREEN);
    }

    public static Component heading(String itemId, int line) {
        return Component.translatable("tooltip.relicera." + itemId + "." + line).withStyle(ChatFormatting.GRAY);
    }

    public static Component quote(String itemId, int line, Object... args) {
        return Component.translatable("tooltip.relicera." + itemId + "." + line, args).withStyle(ChatFormatting.DARK_GRAY);
    }

    public static Component relic(String itemId, int line) {
        return Component.translatable("tooltip.relicera." + itemId + "." + line).withStyle(ChatFormatting.DARK_RED);
    }

    public static Component locked(String itemId, int line, Object... args) {
        return bullet(itemId, line, Style.EMPTY.withColor(ChatFormatting.DARK_GRAY), args);
    }

    private static Component bullet(String itemId, int line, Style style, Object... args) {
        return Component.literal("- ").setStyle(style)
                .append(Component.translatable("tooltip.relicera." + itemId + "." + line, args));
    }

    public static String signedPercent(double value) {
        long percent = Math.round(value * 100.0D);
        return (percent >= 0L ? "+" : "") + percent + "%";
    }

    public static String unsignedPercent(double value) {
        return Math.round(value * 100.0D) + "%";
    }

    public static String unsignedNumber(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.0001D) {
            return String.valueOf((int) Math.rint(value));
        }
        return String.format(java.util.Locale.ROOT, "%.1f", value);
    }

    public static String signedNumber(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.0001D) {
            int rounded = (int) Math.rint(value);
            return (rounded >= 0 ? "+" : "") + rounded;
        }
        return (value >= 0.0D ? "+" : "") + String.format(java.util.Locale.ROOT, "%.1f", value);
    }

    public static String signedDecimal(double value) {
        String formatted = String.format(java.util.Locale.ROOT, "%.2f", Math.abs(value))
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
        return (value >= 0.0D ? "+" : "-") + formatted;
    }
}
