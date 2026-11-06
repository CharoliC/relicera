package com.yukari.relicera.common.item.feysilver;

import com.yukari.relicera.registry.ModItems;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public enum ForgingVolume {
    ONE("volume_one", "VolumeOne", () -> ModItems.FEYSILVER_FORGING_ART_VOLUME_ONE.get()),
    TWO("volume_two", "VolumeTwo", () -> ModItems.FEYSILVER_FORGING_ART_VOLUME_TWO.get());

    private final String id;
    private final String saveKey;
    private final Supplier<Item> book;

    ForgingVolume(String id, String saveKey, Supplier<Item> book) {
        this.id = id;
        this.saveKey = saveKey;
        this.book = book;
    }

    public String toastDescriptionKey() {
        return "toast.relicera.forging_learned." + id + ".description";
    }

    public String saveKey() {
        return saveKey;
    }

    public Item book() {
        return book.get();
    }
}
