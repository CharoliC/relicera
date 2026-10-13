package com.yukari.relicera.common.menu;

import com.yukari.relicera.common.block.RelicRepairTableBlockEntity;
import com.yukari.relicera.common.curio.CovenantTabletEffects;
import com.yukari.relicera.common.recipe.relicrepair.RelicRepairRecipes;
import com.yukari.relicera.registry.ModBlocks;
import com.yukari.relicera.registry.ModItems;
import com.yukari.relicera.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;

public class RelicRepairTableMenu extends AbstractContainerMenu {
    private static final int CONTAINER_SLOT_COUNT = RelicRepairTableBlockEntity.SLOT_COUNT;
    private static final int PLAYER_INVENTORY_START = CONTAINER_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final ContainerLevelAccess access;
    private final ContainerData data;
    private final Level level;

    public RelicRepairTableMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, getBlockEntity(playerInventory.player.level(), extraData.readBlockPos()));
    }

    public RelicRepairTableMenu(int containerId, Inventory playerInventory, RelicRepairTableBlockEntity blockEntity) {
        super(ModMenuTypes.RELIC_REPAIR_TABLE.get(), containerId);
        this.data = blockEntity.getDataAccess();
        this.level = playerInventory.player.level();
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        ItemStackHandler itemHandler = blockEntity.getItemHandler();
        addBlockSlots(itemHandler);
        addPlayerInventorySlots(playerInventory);
        addDataSlots(data);
    }

    private void addBlockSlots(ItemStackHandler itemHandler) {
        this.addSlot(new SlotItemHandler(itemHandler, RelicRepairTableBlockEntity.SLOT_CATALYST, 54, 21) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return RelicRepairRecipes.acceptsCatalyst(level, stack);
            }
        });

        this.addSlot(new SlotItemHandler(itemHandler, RelicRepairTableBlockEntity.SLOT_RELIC, 80, 12) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return RelicRepairRecipes.acceptsRelic(level, stack);
            }
        });

        this.addSlot(new SlotItemHandler(itemHandler, RelicRepairTableBlockEntity.SLOT_MATERIAL_1, 106, 21));
        this.addSlot(new SlotItemHandler(itemHandler, RelicRepairTableBlockEntity.SLOT_MATERIAL_2, 45, 45));
        this.addSlot(new SlotItemHandler(itemHandler, RelicRepairTableBlockEntity.SLOT_MATERIAL_3, 115, 45));

        this.addSlot(new SlotItemHandler(itemHandler, RelicRepairTableBlockEntity.SLOT_OUTPUT, 80, 54) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                if (!player.level().isClientSide && stack.is(ModItems.BAROMS_COVENANT_STONE.get())) {
                    ItemHandlerHelper.giveItemToPlayer(player, CovenantTabletEffects.createFullyUnlockedStack());
                }
            }
        });
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new net.minecraft.world.inventory.Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new net.minecraft.world.inventory.Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        var slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return result;
        }

        ItemStack stack = slot.getItem();
        result = stack.copy();

        if (index < CONTAINER_SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (RelicRepairRecipes.acceptsCatalyst(level, stack)) {
            if (!this.moveItemStackTo(stack, RelicRepairTableBlockEntity.SLOT_CATALYST, RelicRepairTableBlockEntity.SLOT_CATALYST + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (RelicRepairRecipes.acceptsRelic(level, stack)) {
            if (!this.moveItemStackTo(stack, RelicRepairTableBlockEntity.SLOT_RELIC, RelicRepairTableBlockEntity.SLOT_RELIC + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, RelicRepairTableBlockEntity.SLOT_MATERIAL_1, RelicRepairTableBlockEntity.SLOT_OUTPUT, false)) {
            return ItemStack.EMPTY;
        }

        int takenCount = result.getCount() - stack.getCount();
        if (takenCount <= 0) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        slot.onTake(player, result.copyWithCount(takenCount));
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.RELIC_REPAIR_TABLE.get());
    }

    public ItemStack getRepairSlotItem(int slot) {
        if (slot < 0 || slot >= CONTAINER_SLOT_COUNT) {
            return ItemStack.EMPTY;
        }
        return this.slots.get(slot).getItem();
    }

    public int getRepairProgress() {
        return data.get(0);
    }

    public int getRepairTime() {
        return data.get(1);
    }

    public int getScaledRepairProgress(int size) {
        int repairTime = getRepairTime();
        if (repairTime <= 0 || getRepairProgress() <= 0) {
            return 0;
        }
        return Math.min(size, getRepairProgress() * size / repairTime);
    }

    private static RelicRepairTableBlockEntity getBlockEntity(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RelicRepairTableBlockEntity relicRepairTable) {
            return relicRepairTable;
        }
        throw new IllegalStateException("Expected relic repair table block entity at " + pos);
    }
}
