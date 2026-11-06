package com.yukari.relicera.common.item.feysilver;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.util.thread.EffectiveSide;

import java.util.EnumSet;

public final class FeysilverForgingClientData {
    private static final EnumSet<ForgingVolume> learned = EnumSet.noneOf(ForgingVolume.class);

    private FeysilverForgingClientData() {
    }

    public static void update(EnumSet<ForgingVolume> volumes) {
        learned.clear();
        learned.addAll(volumes);
    }

    public static void clear() {
        learned.clear();
    }

    public static boolean hasLearned(ForgingVolume volume) {
        return learned.contains(volume);
    }

    public static Component materialName(Component name) {
        return EffectiveSide.get().isClient() && !hasLearned(ForgingVolume.TWO)
                ? name.copy().withStyle(ChatFormatting.OBFUSCATED)
                : name;
    }
}
