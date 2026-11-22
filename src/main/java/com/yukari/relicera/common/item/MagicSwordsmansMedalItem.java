package com.yukari.relicera.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.UUID;

public final class MagicSwordsmansMedalItem extends QuickEquipCurioItem {
    public MagicSwordsmansMedalItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return "relicera_brooch".equals(context.identifier()) && super.canEquip(context, stack);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext context, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(context, uuid, stack));
        if (!context.cosmetic() && "relicera_brooch".equals(context.identifier())) {
            Attribute mana = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "max_mana"));
            if (mana != null) {
                modifiers.put(mana, new AttributeModifier(uuid, "Relicera Magic Swordsman's Medal",
                        50.0D, AttributeModifier.Operation.ADDITION));
            }
        }
        return modifiers;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("magic_swordsmans_medal", 0,
                ItemTooltipText.unsignedPercent(ModCommonConfig.MAGIC_SWORDSMANS_MEDAL_MELEE_BONUS.get())));
    }
}
