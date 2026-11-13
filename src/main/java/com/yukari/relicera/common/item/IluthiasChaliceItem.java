package com.yukari.relicera.common.item;

import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;

public class IluthiasChaliceItem extends RelicCurioItem implements SelectableItemContents {
    private static final String TAG_TOTEMS = "Totems";
    public static final int TOTEM_CAPACITY = 5;

    public IluthiasChaliceItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.relic("iluthias_chalice", 0));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("iluthias_chalice", 1));
        tooltip.add(ItemTooltipText.curioEffect("iluthias_chalice", 2));
        tooltip.add(ItemTooltipText.curioEffect("iluthias_chalice", 3));
        tooltip.add(ItemTooltipText.curioEffect("iluthias_chalice", 4));
        tooltip.add(ItemTooltipText.curioEffect("iluthias_chalice", 5));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioModifiersHeader("relicera_relic"));
        tooltip.add(ItemTooltipText.description("iluthias_chalice", 6,
                ItemTooltipText.signedPercent(ModCommonConfig.ILUTHIAS_CHALICE_UNDEAD_DAMAGE_BONUS.get())));
        tooltip.add(ItemTooltipText.description("iluthias_chalice", 7,
                ItemTooltipText.signedPercent(-ModCommonConfig.ILUTHIAS_CHALICE_UNDEAD_DAMAGE_REDUCTION.get())));
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack chalice, ItemStack carried, Slot slot, ClickAction action, Player player, SlotAccess carriedSlot) {
        if (chalice.getCount() != 1 || action != ClickAction.SECONDARY || !slot.allowModification(player)) {
            return false;
        }

        if (carried.isEmpty()) {
            int selected = getSelectedContentsSlot(chalice);
            ItemStack selectedTotem = getTotemAt(chalice, selected).orElse(ItemStack.EMPTY);
            if (!selectedTotem.isEmpty() && carriedSlot.set(selectedTotem)) {
                removeTotemAt(chalice, selected);
                slot.setChanged();
                playRemoveOneSound(player);
            }
            return true;
        }

        if (carried.is(Items.TOTEM_OF_UNDYING) && addOneTotem(chalice, carried)) {
            carried.shrink(1);
            slot.setChanged();
            playInsertSound(player);
        }
        return true;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new TotemContentsTooltip(getTotems(stack), getSelectedContentsSlot(stack)));
    }

    @Override
    public int getContentsSlotCount() {
        return TOTEM_CAPACITY;
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        ItemUtils.onContainerDestroyed(itemEntity, getTotemContents(itemEntity.getItem()));
    }

    private boolean addOneTotem(ItemStack chalice, ItemStack totem) {
        for (int index = 0; index < TOTEM_CAPACITY; index++) {
            if (getTotemAt(chalice, index).isPresent()) {
                continue;
            }
            ListTag totems = getOrCreateTotems(chalice);
            CompoundTag savedTotem = new CompoundTag();
            totem.copyWithCount(1).save(savedTotem);
            totems.set(index, savedTotem);
            setSelectedContentsSlot(chalice, index);
            return true;
        }
        return false;
    }

    public static Optional<ItemStack> peekOneTotem(ItemStack chalice) {
        return getTotemAt(chalice, findFirstOccupiedSlot(chalice));
    }

    private static int findFirstOccupiedSlot(ItemStack chalice) {
        for (int index = 0; index < TOTEM_CAPACITY; index++) {
            if (getTotemAt(chalice, index).isPresent()) {
                return index;
            }
        }
        return -1;
    }

    private static Optional<ItemStack> getTotemAt(ItemStack chalice, int index) {
        ListTag totems = getTotemList(chalice);
        if (index < 0 || index >= TOTEM_CAPACITY || index >= totems.size()) {
            return Optional.empty();
        }
        CompoundTag savedTotem = totems.getCompound(index);
        if (savedTotem.isEmpty()) {
            return Optional.empty();
        }
        ItemStack totem = ItemStack.of(savedTotem);
        return totem.is(Items.TOTEM_OF_UNDYING) ? Optional.of(totem) : Optional.empty();
    }

    public static void removeOneTotem(ItemStack chalice) {
        removeTotemAt(chalice, findFirstOccupiedSlot(chalice));
    }

    private static void removeTotemAt(ItemStack chalice, int index) {
        if (getTotemAt(chalice, index).isEmpty()) {
            return;
        }

        ListTag totems = getTotemList(chalice);
        totems.set(index, new CompoundTag());
        if (findFirstOccupiedSlot(chalice) < 0) {
            chalice.removeTagKey(TAG_TOTEMS);
        }
    }

    public static NonNullList<ItemStack> getTotems(ItemStack chalice) {
        NonNullList<ItemStack> result = NonNullList.withSize(TOTEM_CAPACITY, ItemStack.EMPTY);
        for (int index = 0; index < TOTEM_CAPACITY; index++) {
            result.set(index, getTotemAt(chalice, index).orElse(ItemStack.EMPTY));
        }
        return result;
    }

    public static Stream<ItemStack> getTotemContents(ItemStack chalice) {
        return getTotems(chalice).stream().filter(stack -> !stack.isEmpty());
    }

    private static ListTag getOrCreateTotems(ItemStack chalice) {
        CompoundTag tag = chalice.getOrCreateTag();
        if (!tag.contains(TAG_TOTEMS, Tag.TAG_LIST)) {
            tag.put(TAG_TOTEMS, new ListTag());
        }
        ListTag totems = tag.getList(TAG_TOTEMS, Tag.TAG_COMPOUND);
        while (totems.size() < TOTEM_CAPACITY) {
            totems.add(new CompoundTag());
        }
        return totems;
    }

    private static ListTag getTotemList(ItemStack chalice) {
        CompoundTag tag = chalice.getTag();
        if (tag == null || !tag.contains(TAG_TOTEMS, Tag.TAG_LIST)) {
            return new ListTag();
        }
        return tag.getList(TAG_TOTEMS, Tag.TAG_COMPOUND);
    }

    public record TotemContentsTooltip(NonNullList<ItemStack> totems, int selectedSlot) implements TooltipComponent {
    }

    private static void playInsertSound(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.playNotifySound(SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 0.8F, 1.0F);
        }
    }

    private static void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
