package com.yukari.relicera.common.item.feysilver;

import com.yukari.relicera.ReliceraMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ReliceraMod.MOD_ID)
public final class FeysilverTrimEffects {
    private static final String MATERIAL_ID = ReliceraMod.MOD_ID + ":feysilver";
    private static final Map<EquipmentSlot, UUID> TOUGHNESS_MODIFIER_IDS = Map.of(
            EquipmentSlot.HEAD, UUID.fromString("62977a7e-e6c2-45e4-a9ee-d4bd49477ee4"),
            EquipmentSlot.CHEST, UUID.fromString("33c1f75a-3c48-4513-a657-d29a1967f2a1"),
            EquipmentSlot.LEGS, UUID.fromString("36c3e298-e004-46fb-ae83-44b04e41ba93"),
            EquipmentSlot.FEET, UUID.fromString("63049a19-d2c9-467f-86e5-2c5a3d388803"));

    private FeysilverTrimEffects() {
    }

    @SubscribeEvent
    public static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        EquipmentSlot slot = event.getSlotType();
        if (!(stack.getItem() instanceof ArmorItem armor)
                || slot.getType() != EquipmentSlot.Type.ARMOR || slot != armor.getEquipmentSlot()
                || !stack.is(ItemTags.TRIMMABLE_ARMOR)) {
            return;
        }

        CompoundTag trim = stack.getTagElement(ArmorTrim.TAG_TRIM_ID);
        if (trim == null || !MATERIAL_ID.equals(trim.getString("material"))) {
            return;
        }

        event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(
                TOUGHNESS_MODIFIER_IDS.get(slot), "Relicera Feysilver trim armor toughness",
                1.0D, AttributeModifier.Operation.ADDITION));
    }
}
