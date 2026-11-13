package com.yukari.relicera.common.item;

import com.yukari.relicera.common.curio.RippleheartRingClientData;
import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RippleheartRingItem extends QuickEquipCurioItem implements ViewerTooltipItem {
    private static final String PAIR_ID_TAG = "RippleheartPairId";

    private final RingSide side;

    public RippleheartRingItem(Properties properties, RingSide side) {
        super(properties);
        this.side = side;
    }

    @Override
    public void appendViewerTooltip(ItemStack stack, @Nullable Player player, List<Component> tooltip) {
        tooltip.add(Component.empty());

        String sideId = side == RingSide.RED ? "rippleheart_ring_red" : "rippleheart_ring_blue";
        tooltip.add(ItemTooltipText.curioEffectsHeader(side == RingSide.RED ? 1 : 2,
                ItemTooltipText.unsignedNumber(ModCommonConfig.RIPPLEHEART_RINGS_ACTIVATION_RANGE.get())
        ));

        if (side == RingSide.RED) {
            tooltip.add(ItemTooltipText.curioEffect(sideId, 0, effectName(MobEffects.DAMAGE_RESISTANCE, "III")));
            tooltip.add(ItemTooltipText.curioEffect(sideId, 1));
            tooltip.add(ItemTooltipText.curioEffect(sideId, 2,
                    ItemTooltipText.signedPercent(ModCommonConfig.RIPPLEHEART_RINGS_RED_DAMAGE_BONUS.get())
            ));
            tooltip.add(ItemTooltipText.curioEffect(sideId, 3,
                    ItemTooltipText.signedPercent(ModCommonConfig.RIPPLEHEART_RINGS_RED_ATTACK_SPEED_BONUS.get())
            ));
        } else {
            tooltip.add(ItemTooltipText.curioEffect(sideId, 0, effectName(MobEffects.DAMAGE_BOOST, "III")));
            tooltip.add(ItemTooltipText.curioEffect(sideId, 1));
            tooltip.add(ItemTooltipText.curioEffect(sideId, 2,
                    ItemTooltipText.signedPercent(ModCommonConfig.RIPPLEHEART_RINGS_BLUE_DAMAGE_REDUCTION.get())
            ));
            tooltip.add(ItemTooltipText.curioEffect(sideId, 3,
                    ItemTooltipText.signedPercent(ModCommonConfig.RIPPLEHEART_RINGS_BLUE_HEALING_BONUS.get())
            ));
        }

        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.description(sideId, 4, getRippleheartCounterpartName(stack, side)));
    }

    private static Component getRippleheartCounterpartName(ItemStack stack, RingSide side) {
        Optional<UUID> stackPairId = RippleheartRingItem.getPairId(stack);
        if (stackPairId.isEmpty()) {
            return ItemTooltipText.quote("rippleheart_ring", 2);
        }

        if (!stackPairId.equals(RippleheartRingClientData.getPairId())) {
            return ItemTooltipText.quote("rippleheart_ring", 3);
        }

        Component wearerName = side == RingSide.RED
                ? RippleheartRingClientData.getBlueWearerName()
                : RippleheartRingClientData.getRedWearerName();
        if (wearerName.getString().isBlank()) {
            return ItemTooltipText.quote("rippleheart_ring", 3);
        }
        return Component.literal(wearerName.getString());
    }

    private static Component effectName(MobEffect effect, String amplifier) {
        return Component.translatable(effect.getDescriptionId())
                .append(" " + amplifier);
    }

    public RingSide getSide() {
        return side;
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (!super.canEquip(slotContext, stack)) {
            return false;
        }
        if (slotContext.entity() == null) {
            return true;
        }

        return CuriosApi.getCuriosInventory(slotContext.entity())
                .resolve()
                .map(handler -> handler.findCurios(equipped -> equipped.getItem() instanceof RippleheartRingItem)
                        .stream()
                        .noneMatch(result -> result.stack() != stack))
                .orElse(true);
    }

    public ItemStack createBoundStack(UUID pairId) {
        ItemStack stack = new ItemStack(this);
        setPairId(stack, pairId);
        return stack;
    }

    public static void setPairId(ItemStack stack, UUID pairId) {
        if (isPairItem(stack)) {
            stack.getOrCreateTag().putUUID(PAIR_ID_TAG, pairId);
        }
    }

    public static Optional<UUID> getPairId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (!isPairItem(stack) || tag == null || !tag.hasUUID(PAIR_ID_TAG)) {
            return Optional.empty();
        }
        return Optional.of(tag.getUUID(PAIR_ID_TAG));
    }

    private static boolean isPairItem(ItemStack stack) {
        return stack.getItem() instanceof RippleheartRingItem
                || stack.getItem() instanceof RippleheartRingsItem;
    }

    public enum RingSide {
        RED,
        BLUE
    }
}
