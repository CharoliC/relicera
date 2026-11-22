package com.yukari.relicera.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.UUID;

public final class PrismaticLampItem extends QuickEquipCurioItem {
    private static final String BELT_SLOT = "belt";
    private static final UUID SLOT_MODIFIER = UUID.fromString("bf3cdfba-ed54-4141-bc8b-5da4c9fb4321");

    public PrismaticLampItem(Properties properties) {
        super(properties);
    }

    public static boolean isEquipped(Player player) {
        return ModItems.PRISMATIC_LAMP.isPresent() && CuriosApi.getCuriosInventory(player).resolve()
                .map(handler -> handler.findCurios(ModItems.PRISMATIC_LAMP.get()).stream()
                        .anyMatch(result -> !result.slotContext().cosmetic()
                                && BELT_SLOT.equals(result.slotContext().identifier())))
                .orElse(false);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return BELT_SLOT.equals(context.identifier()) && super.canEquip(context, stack);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext context, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers =
                HashMultimap.create(super.getAttributeModifiers(context, uuid, stack));
        if (!context.cosmetic() && BELT_SLOT.equals(context.identifier())) {
            CuriosApi.addSlotModifier(modifiers, BELT_SLOT, SLOT_MODIFIER, 1.0D,
                    AttributeModifier.Operation.ADDITION);
        }
        return modifiers;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("prismatic_lamp", 0));
    }
}
