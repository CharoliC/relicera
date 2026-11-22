package com.yukari.relicera.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class BaromsCovenantStoneItem extends RelicCurioItem implements SelectableItemContents {
    public static final String RELIC_SLOT = "relicera_relic";
    public static final int CONTRACT_CAPACITY = 5;
    private static final String TAG_CONTRACTS = "ReliceraContracts";

    public BaromsCovenantStoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        String itemId = "baroms_covenant_stone";
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.relic(itemId, 0));
        tooltip.add(Component.empty());

        List<ItemStack> contracts = BaromsCovenantStoneItem.getContracts(stack);
        if (contracts.stream().allMatch(ItemStack::isEmpty)) {
            tooltip.add(ItemTooltipText.curioEffectsHeader());
            tooltip.add(ItemTooltipText.curioEffect(itemId, 15));
            tooltip.add(ItemTooltipText.curioEffect(itemId, 16));
        }
        for (ItemStack contract : contracts) {
            appendBaromsCovenantContractTooltip(tooltip, contract);
        }
    }

    private static void appendBaromsCovenantContractTooltip(List<Component> tooltip, ItemStack contract) {
        EntityType<?> entityType = InscribedCovenantTabletItem.getContractEntity(contract);
        if (entityType == null) {
            return;
        }
        int effectLine;
        if (entityType == EntityType.IRON_GOLEM) {
            effectLine = 1;
        } else if (entityType == EntityType.SILVERFISH) {
            effectLine = 2;
        } else if (entityType == EntityType.VILLAGER) {
            effectLine = 4;
        } else if (entityType == EntityType.PILLAGER) {
            effectLine = 5;
        } else if (entityType == EntityType.ENDER_DRAGON) {
            effectLine = 6;
        } else if (entityType == EntityType.ZOMBIE) {
            effectLine = 7;
        } else if (entityType == EntityType.CREEPER) {
            effectLine = 8;
        } else if (entityType == EntityType.BLAZE) {
            effectLine = 9;
        } else if (entityType == EntityType.HUSK) {
            effectLine = 10;
        } else if (entityType == EntityType.SKELETON) {
            effectLine = 11;
        } else if (entityType == EntityType.WANDERING_TRADER) {
            effectLine = 12;
        } else if (entityType == EntityType.PIGLIN) {
            effectLine = 13;
        } else if (entityType == EntityType.WITCH) {
            effectLine = 14;
        } else {
            effectLine = 17;
        }

        tooltip.add(contract.getHoverName().copy().withStyle(ChatFormatting.BLUE));
        if (entityType == EntityType.SILVERFISH) {
            double volumeMultiplier = ModCommonConfig.BAROM_VOLUME_MULTIPLIER.get();
            tooltip.add(volumeMultiplier == 1.0D
                    ? ItemTooltipText.effect("baroms_covenant_stone", 2)
                    : ItemTooltipText.effect("baroms_covenant_stone", 3, java.math.BigDecimal.valueOf(volumeMultiplier)
                            .stripTrailingZeros().toPlainString()));
        } else if (entityType == EntityType.HUSK) {
            String chance = java.math.BigDecimal.valueOf(ModCommonConfig.BAROM_HUSK_HUNGER_CHANCE.get())
                    .multiply(java.math.BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%";
            tooltip.add(ItemTooltipText.effect("baroms_covenant_stone", effectLine, chance));
        } else if (entityType == EntityType.WITCH) {
            String chance = java.math.BigDecimal.valueOf(ModCommonConfig.BAROM_WITCH_POTION_PRESERVATION_CHANCE.get())
                    .multiply(java.math.BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%";
            tooltip.add(ItemTooltipText.effect("baroms_covenant_stone", effectLine, chance));
        } else {
            tooltip.add(ItemTooltipText.effect("baroms_covenant_stone", effectLine));
        }
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (!RELIC_SLOT.equals(context.identifier())) {
            return false;
        }
        return context.entity() == null || CuriosApi.getCuriosInventory(context.entity()).resolve()
                .map(handler -> handler.findCurios(ModItems.BAROMS_COVENANT_STONE.get()).stream()
                        .noneMatch(result -> !result.slotContext().identifier().equals(context.identifier())
                                || result.slotContext().index() != context.index()))
                .orElse(true);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext context, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers =
                HashMultimap.create(super.getAttributeModifiers(context, uuid, stack));
        if (!context.cosmetic() && RELIC_SLOT.equals(context.identifier())) {
            if (hasContract(stack, EntityType.ENDER_DRAGON)) {
                CuriosApi.addSlotModifier(modifiers, RELIC_SLOT, uuid, 1.0D, AttributeModifier.Operation.ADDITION);
            }
            long genericContracts = getContracts(stack).stream()
                    .map(InscribedCovenantTabletItem::getContractEntity)
                    .filter(type -> type != null && !InscribedCovenantTabletItem.hasSpecificContract(type))
                    .distinct().count();
            if (genericContracts > 0) {
                modifiers.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid,
                        "Relicera Barom generic covenant toughness", 2.0D * genericContracts,
                        AttributeModifier.Operation.ADDITION));
            }
        }
        return modifiers;
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (!context.cosmetic() && RELIC_SLOT.equals(context.identifier())
                && hasContract(stack, EntityType.VILLAGER)
                && !context.entity().level().isClientSide()) {
            context.entity().removeEffect(MobEffects.DIG_SLOWDOWN);
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stone, ItemStack carried, Slot slot, ClickAction action,
            Player player, SlotAccess carriedSlot) {
        if (stone.getCount() != 1 || action != ClickAction.SECONDARY || !slot.allowModification(player)) {
            return false;
        }
        if (carried.isEmpty()) {
            int selected = getSelectedContentsSlot(stone);
            ItemStack tablet = getContractAt(stone, selected);
            if (!tablet.isEmpty() && carriedSlot.set(tablet)) {
                removeContractAt(stone, selected);
                slot.setChanged();
                player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 1.0F);
            }
            return true;
        }
        if (InscribedCovenantTabletItem.getContractEntity(carried) != null && addContract(stone, carried)) {
            carried.shrink(1);
            slot.setChanged();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.playNotifySound(SoundEvents.UI_STONECUTTER_TAKE_RESULT,
                        SoundSource.PLAYERS, 0.8F, 1.0F);
            }
        }
        return true;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new ContractContentsTooltip(getContracts(stack), getSelectedContentsSlot(stack)));
    }

    @Override
    public int getContentsSlotCount() {
        return CONTRACT_CAPACITY;
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        ItemUtils.onContainerDestroyed(itemEntity,
                getContracts(itemEntity.getItem()).stream().filter(stack -> !stack.isEmpty()));
    }

    public static boolean hasContract(ItemStack stone, EntityType<?> entityType) {
        if (entityType == null) {
            return false;
        }
        for (int index = 0; index < CONTRACT_CAPACITY; index++) {
            if (InscribedCovenantTabletItem.getContractEntity(getContractAt(stone, index)) == entityType) {
                return true;
            }
        }
        return false;
    }

    public static NonNullList<ItemStack> getContracts(ItemStack stone) {
        NonNullList<ItemStack> result = NonNullList.withSize(CONTRACT_CAPACITY, ItemStack.EMPTY);
        for (int index = 0; index < CONTRACT_CAPACITY; index++) {
            result.set(index, getContractAt(stone, index));
        }
        return result;
    }

    public static boolean removeContract(ItemStack stone, EntityType<?> entityType) {
        if (entityType == null) {
            return false;
        }
        for (int index = 0; index < CONTRACT_CAPACITY; index++) {
            if (InscribedCovenantTabletItem.getContractEntity(getContractAt(stone, index)) == entityType) {
                removeContractAt(stone, index);
                return true;
            }
        }
        return false;
    }

    private boolean addContract(ItemStack stone, ItemStack tablet) {
        EntityType<?> entityType = InscribedCovenantTabletItem.getContractEntity(tablet);
        if (entityType == null || hasContract(stone, entityType)) {
            return false;
        }
        for (int index = 0; index < CONTRACT_CAPACITY; index++) {
            if (!getContractAt(stone, index).isEmpty()) {
                continue;
            }
            CompoundTag saved = new CompoundTag();
            tablet.copyWithCount(1).save(saved);
            getOrCreateContracts(stone).set(index, saved);
            setSelectedContentsSlot(stone, index);
            return true;
        }
        return false;
    }

    private static void removeContractAt(ItemStack stone, int index) {
        if (getContractAt(stone, index).isEmpty()) {
            return;
        }
        getContractList(stone).set(index, new CompoundTag());
        if (getContracts(stone).stream().allMatch(ItemStack::isEmpty)) {
            stone.removeTagKey(TAG_CONTRACTS);
        }
    }

    private static ItemStack getContractAt(ItemStack stone, int index) {
        ListTag contracts = getContractList(stone);
        if (index < 0 || index >= CONTRACT_CAPACITY || index >= contracts.size()) {
            return ItemStack.EMPTY;
        }
        CompoundTag saved = contracts.getCompound(index);
        return saved.isEmpty() ? ItemStack.EMPTY : ItemStack.of(saved).copyWithCount(1);
    }

    private static ListTag getOrCreateContracts(ItemStack stone) {
        CompoundTag tag = stone.getOrCreateTag();
        if (!tag.contains(TAG_CONTRACTS, Tag.TAG_LIST)) {
            tag.put(TAG_CONTRACTS, new ListTag());
        }
        ListTag contracts = tag.getList(TAG_CONTRACTS, Tag.TAG_COMPOUND);
        while (contracts.size() < CONTRACT_CAPACITY) {
            contracts.add(new CompoundTag());
        }
        return contracts;
    }

    private static ListTag getContractList(ItemStack stone) {
        CompoundTag tag = stone.getTag();
        return tag == null || !tag.contains(TAG_CONTRACTS, Tag.TAG_LIST)
                ? new ListTag() : tag.getList(TAG_CONTRACTS, Tag.TAG_COMPOUND);
    }

    public record ContractContentsTooltip(NonNullList<ItemStack> contracts, int selectedSlot)
            implements TooltipComponent {
    }
}
