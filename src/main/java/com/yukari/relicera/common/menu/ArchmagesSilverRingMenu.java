package com.yukari.relicera.common.menu;

import com.yukari.relicera.common.compat.irons_spellbooks.ArchmagesSilverRingCompat;
import com.yukari.relicera.common.item.ArchmagesSilverRingItem;
import net.minecraft.world.inventory.DataSlot;
import static com.yukari.relicera.common.item.ArchmagesSilverRingItem.BOOK_TAG;
import com.yukari.relicera.registry.ModItems;
import com.yukari.relicera.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public final class ArchmagesSilverRingMenu extends AbstractContainerMenu {
    public static final int TOGGLE_INSCRIBED_BUTTON = 0;
    private final DataSlot excludeInscribed = DataSlot.standalone();
    private static final int INVENTORY_START = 1;
    private static final int HOTBAR_START = 28;
    private static final int SLOT_END = 37;
    private final ItemStack ring;
    private final int ringSlot;

    public ArchmagesSilverRingMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, buffer.readVarInt());
    }

    public ArchmagesSilverRingMenu(int id, Inventory inventory, int ringSlot) {
        super(ModMenuTypes.ARCHMAGES_SILVER_RING.get(), id);
        this.ringSlot = ringSlot;
        this.ring = inventory.getItem(ringSlot);
        excludeInscribed.set(ArchmagesSilverRingItem.excludesInscribedSpells(ring) ? 1 : 0);
        addDataSlot(excludeInscribed);
        ItemStackHandler books = new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return ArchmagesSilverRingCompat.isSpellbook(stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            protected void onContentsChanged(int slot) {
                if (!inventory.player.level().isClientSide) {
                    if (getStackInSlot(0).isEmpty()) {
                        ring.removeTagKey(BOOK_TAG);
                    } else {
                        ring.getOrCreateTag().put(BOOK_TAG, getStackInSlot(0).save(new net.minecraft.nbt.CompoundTag()));
                    }
                    inventory.setChanged();
                }
            }
        };
        if (ring.hasTag() && ring.getTag().contains(BOOK_TAG)) {
            books.setStackInSlot(0, ItemStack.of(ring.getTag().getCompound(BOOK_TAG)));
        }
        addSlot(new SlotItemHandler(books, 0, 26, 38));
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, 9 + row * 9 + column, 16 + column * 18, 101 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            final boolean locked = column == ringSlot;
            addSlot(new Slot(inventory, column, 16 + column * 18, 159) {
                @Override
                public boolean mayPickup(Player player) {
                    return !locked;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return !locked;
                }
            });
        }
    }

    public ItemStack getSpellbook() {
        return getSlot(0).getItem();
    }

    public boolean excludesInscribedSpells() {
        return excludeInscribed.get() != 0;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (player.level().isClientSide || id != TOGGLE_INSCRIBED_BUTTON || !stillValid(player)) {
            return false;
        }
        excludeInscribed.set(excludesInscribedSpells() ? 0 : 1);
        ring.getOrCreateTag().putBoolean(ArchmagesSilverRingItem.EXCLUDE_INSCRIBED_TAG, excludesInscribedSpells());
        player.getInventory().setChanged();
        broadcastChanges();
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive() && player.getInventory().selected == ringSlot
                && player.getInventory().getItem(ringSlot) == ring
                && ring.is(ModItems.ARCHMAGES_SILVER_RING.get());
    }

    @Override
    public void clicked(int slot, int button, ClickType type, Player player) {
        if ((!player.level().isClientSide && !stillValid(player))
                || slot == HOTBAR_START + ringSlot || (type == ClickType.SWAP && button == ringSlot)) {
            return;
        }
        super.clicked(slot, button, type, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= SLOT_END || index == HOTBAR_START + ringSlot
                || (!player.level().isClientSide && !stillValid(player))) {
            return ItemStack.EMPTY;
        }
        Slot slot = getSlot(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == 0) {
            if (!moveItemStackTo(stack, INVENTORY_START, SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (ArchmagesSilverRingCompat.isSpellbook(stack) && !getSlot(0).hasItem()) {
            if (!moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < HOTBAR_START) {
            if (!moveItemStackTo(stack, HOTBAR_START, SLOT_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, INVENTORY_START, HOTBAR_START, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, stack);
        return original;
    }
}
