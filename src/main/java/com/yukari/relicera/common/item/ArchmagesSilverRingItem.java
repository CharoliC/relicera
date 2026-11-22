package com.yukari.relicera.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.yukari.relicera.common.menu.ArchmagesSilverRingMenu;
import com.yukari.relicera.common.compat.irons_spellbooks.ArchmagesSilverRingCompat;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.NetworkHooks;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;
import java.nio.charset.StandardCharsets;

public final class ArchmagesSilverRingItem extends Item implements ICurioItem, ViewerTooltipItem {
    public static final String BOOK_TAG = "Spellbook";
    public static final String EXCLUDE_INSCRIBED_TAG = "ExcludeInscribedSpells";

    public static ItemStack getSpellbook(ItemStack ring) {
        return ring.hasTag() && ring.getTag().contains(BOOK_TAG, net.minecraft.nbt.Tag.TAG_COMPOUND)
                ? ItemStack.of(ring.getTag().getCompound(BOOK_TAG)) : ItemStack.EMPTY;
    }

    public static boolean excludesInscribedSpells(ItemStack ring) {
        return ring.hasTag() && ring.getTag().getBoolean(EXCLUDE_INSCRIBED_TAG);
    }

    public ArchmagesSilverRingItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendViewerTooltip(ItemStack stack, @Nullable Player player, List<Component> tooltip) {
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        ItemStack book = getSpellbook(stack);
        if (book.isEmpty()) {
            for (int line = 0; line < 3; line++) {
                tooltip.add(ItemTooltipText.curioEffect("archmages_silver_ring", line));
            }
            return;
        }
        Component bookName = book.getHoverName().copy().withStyle(book.getRarity().getStyleModifier());
        ModCommonConfig.SpellbookPowerBonus bonus = getBookBonus(book);
        Component schoolName = bonus != null && bonus.amount() > 0
                ? ArchmagesSilverRingCompat.getSchoolName(bonus.attribute()) : null;
        tooltip.add(schoolName == null
                ? ItemTooltipText.curioEffect("archmages_silver_ring", 6, bookName)
                : ItemTooltipText.curioEffect("archmages_silver_ring", 3, bookName, schoolName));
        List<Component> spells = ArchmagesSilverRingCompat.getSelectedSpellNames(book, excludesInscribedSpells(stack), player);
        for (Component spell : spells) {
            tooltip.add(ItemTooltipText.curioEffect("archmages_silver_ring", 4, spell,
                    Component.translatable("tooltip.relicera.archmages_silver_ring.5").withStyle(ChatFormatting.YELLOW)));
        }
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return "ring".equals(context.identifier()) && ICurioItem.super.canEquip(context, stack);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext context, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers =
                HashMultimap.create(ICurioItem.super.getAttributeModifiers(context, uuid, stack));
        if (!context.cosmetic() && "ring".equals(context.identifier())) {
            addAttribute(modifiers, uuid, "max_mana", 150.0D, AttributeModifier.Operation.ADDITION);
            addAttribute(modifiers, uuid, "spell_power", 0.10D, AttributeModifier.Operation.MULTIPLY_BASE);
            addAttribute(modifiers, uuid, "mana_regen", 0.25D, AttributeModifier.Operation.MULTIPLY_BASE);
            addBookBonus(modifiers, uuid, getSpellbook(stack));
        }
        return modifiers;
    }

    private static void addBookBonus(Multimap<Attribute, AttributeModifier> modifiers, UUID uuid, ItemStack book) {
        ModCommonConfig.SpellbookPowerBonus bonus = getBookBonus(book);
        if (bonus == null) {
            return;
        }
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(bonus.attribute());
        UUID bonusId = UUID.nameUUIDFromBytes((uuid + ":relicera:spellbook_power").getBytes(StandardCharsets.UTF_8));
        modifiers.put(attribute, new AttributeModifier(bonusId, "Relicera Archmage's Silver Ring book",
                bonus.amount(), AttributeModifier.Operation.MULTIPLY_BASE));
    }

    @Nullable
    private static ModCommonConfig.SpellbookPowerBonus getBookBonus(ItemStack book) {
        if (book.isEmpty()) {
            return null;
        }
        ResourceLocation bookId = ForgeRegistries.ITEMS.getKey(book.getItem());
        for (String entry : ModCommonConfig.ARCHMAGES_SILVER_RING_BOOK_BONUSES.get()) {
            ModCommonConfig.SpellbookPowerBonus bonus = ModCommonConfig.SpellbookPowerBonus.parse(entry);
            if (bonus == null || !bonus.book().equals(bookId)) {
                continue;
            }
            if (ForgeRegistries.ATTRIBUTES.containsKey(bonus.attribute())) {
                return bonus;
            }
        }
        return null;
    }

    private static void addAttribute(Multimap<Attribute, AttributeModifier> modifiers, UUID uuid,
                                     String name, double amount, AttributeModifier.Operation operation) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", name));
        if (attribute != null) {
            modifiers.put(attribute, new AttributeModifier(uuid, "Relicera Archmage's Silver Ring " + name,
                    amount, operation));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            int slot = player.getInventory().selected;
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                    (id, inventory, owner) -> new ArchmagesSilverRingMenu(id, inventory, slot),
                    stack.getHoverName()), buffer -> buffer.writeVarInt(slot));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
