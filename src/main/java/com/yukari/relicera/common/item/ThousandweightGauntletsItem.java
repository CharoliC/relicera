package com.yukari.relicera.common.item;

import com.yukari.relicera.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ThousandweightGauntletsItem extends QuickEquipCurioItem {
    public static final int MAX_WIND_BURST_CHARGE = 16;
    private static final String WIND_BURST_CHARGE_TAG = "ReliceraWindBurstCharge";

    public ThousandweightGauntletsItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("thousandweight_gauntlets", 0));
        tooltip.add(ItemTooltipText.curioEffect("thousandweight_gauntlets", 1));
        tooltip.add(ItemTooltipText.curioEffect("thousandweight_gauntlets", 2));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioModifiersHeader("hands"));
        tooltip.add(ItemTooltipText.negativeStat("thousandweight_gauntlets", 3,
                ItemTooltipText.signedPercent(-ModCommonConfig.THOUSANDWEIGHT_GAUNTLETS_ATTACK_SPEED_REDUCTION.get())));
    }

    public static int getWindBurstCharge(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return 0;
        }
        return Mth.clamp(tag.getInt(WIND_BURST_CHARGE_TAG), 0, MAX_WIND_BURST_CHARGE);
    }

    public static boolean consumeWindBurstCharge(ItemStack stack) {
        int charge = getWindBurstCharge(stack);
        if (charge <= 0) {
            return false;
        }
        setWindBurstCharge(stack, charge - 1);
        return true;
    }

    private static void setWindBurstCharge(ItemStack stack, int charge) {
        int clampedCharge = Mth.clamp(charge, 0, MAX_WIND_BURST_CHARGE);
        if (clampedCharge == 0) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(WIND_BURST_CHARGE_TAG);
            }
            return;
        }
        stack.getOrCreateTag().putInt(WIND_BURST_CHARGE_TAG, clampedCharge);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getWindBurstCharge(stack) / MAX_WIND_BURST_CHARGE);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float chargeRatio = getWindBurstCharge(stack) / (float) MAX_WIND_BURST_CHARGE;
        return Mth.hsvToRgb(chargeRatio / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack gauntletsStack,
            ItemStack carriedStack,
            Slot slot,
            ClickAction action,
            Player player,
            SlotAccess carriedSlot
    ) {
        if (action != ClickAction.SECONDARY
                || !carriedStack.is(Items.TNT)
                || !slot.allowModification(player)) {
            return false;
        }

        int currentCharge = getWindBurstCharge(gauntletsStack);
        int neededCharge = MAX_WIND_BURST_CHARGE - currentCharge;
        if (neededCharge <= 0) {
            return true;
        }

        boolean creative = player.getAbilities().instabuild;
        // Creative inventory clicks run locally and synchronize through vanilla creative-slot packets.
        if (!(player instanceof ServerPlayer) && !(player.level().isClientSide() && creative)) {
            return true;
        }

        int transferred = creative ? neededCharge : Math.min(neededCharge, carriedStack.getCount());
        if (transferred > 0) {
            setWindBurstCharge(gauntletsStack, currentCharge + transferred);
            if (!creative) {
                carriedStack.shrink(transferred);
            }
            slot.setChanged();
            playChargeSound(player);
        }
        return true;
    }

    private static void playChargeSound(Player player) {
        if (player.level().isClientSide()) {
            if (player.getAbilities().instabuild) {
                player.playSound(SoundEvents.SAND_PLACE, 1.0F, 1.0F);
            }
            return;
        }

        if (!player.getAbilities().instabuild) {
            player.level().playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.SAND_PLACE,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        }
    }
}
