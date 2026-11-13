package com.yukari.relicera.common.item;

import com.yukari.relicera.registry.ModSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class PastoralMelodyItem extends Item {
    public static final int USE_DURATION_TICKS = 140;
    public static final int MAX_CHARGE = 64;
    private static final String CHARGE_TAG = "ReliceraPastoralCharge";
    private static final int GLOW_PARTICLE_COUNT = 36;

    public PastoralMelodyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int charge = getCharge(stack);
        if (charge <= 0 || player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide()) {
            setCharge(stack, charge - 1);
        }

        player.startUsingItem(hand);
        level.playSound(player, player, ModSoundEvents.PASTORAL_MELODY_PLAY.get(), SoundSource.RECORDS, 16.0F, 1.0F);
        level.gameEvent(GameEvent.INSTRUMENT_PLAY, player.position(), GameEvent.Context.of(player));
        player.getCooldowns().addCooldown(this, USE_DURATION_TICKS);
        player.getCooldowns().addCooldown(Items.GOAT_HORN, USE_DURATION_TICKS);
        player.awardStat(Stats.ITEM_USED.get(this));

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.GLOW, player.getX(), player.getY() + 0.08D, player.getZ(),
                    GLOW_PARTICLE_COUNT, 0.9D, 0.08D, 0.9D, 0.02D);
            PastoralMelodyEffects.charmNearbyAnimals(serverLevel, player);
        }

        return InteractionResultHolder.consume(stack);
    }

    public static int getCharge(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? 0 : Mth.clamp(tag.getInt(CHARGE_TAG), 0, MAX_CHARGE);
    }

    private static void setCharge(ItemStack stack, int charge) {
        int clampedCharge = Mth.clamp(charge, 0, MAX_CHARGE);
        if (clampedCharge == 0) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(CHARGE_TAG);
            }
            return;
        }
        stack.getOrCreateTag().putInt(CHARGE_TAG, clampedCharge);
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
        return Math.round(13.0F * getCharge(stack) / MAX_CHARGE);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb(getCharge(stack) / (float) MAX_CHARGE / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack melodyStack,
            ItemStack carriedStack,
            Slot slot,
            ClickAction action,
            Player player,
            SlotAccess carriedSlot
    ) {
        if (action != ClickAction.SECONDARY || !carriedStack.is(Items.HAY_BLOCK)
                || !slot.allowModification(player)) {
            return false;
        }
        int currentCharge = getCharge(melodyStack);
        int neededCharge = MAX_CHARGE - currentCharge;
        if (neededCharge <= 0) {
            return true;
        }
        boolean creative = player.getAbilities().instabuild;
        if (!(player instanceof ServerPlayer) && !(player.level().isClientSide() && creative)) {
            return true;
        }
        int transferred = creative ? neededCharge : Math.min(neededCharge, carriedStack.getCount());
        if (transferred > 0) {
            setCharge(melodyStack, currentCharge + transferred);
            if (!creative) {
                carriedStack.shrink(transferred);
            }
            slot.setChanged();
            playChargeSound(player);
        }
        return true;
    }

    private static void playChargeSound(Player player) {
        boolean creative = player.getAbilities().instabuild;
        if (player.level().isClientSide() != creative) {
            return;
        }
        SoundEvent sound = switch (player.getRandom().nextInt(3)) {
            case 0 -> SoundEvents.COW_AMBIENT;
            case 1 -> SoundEvents.SHEEP_AMBIENT;
            default -> SoundEvents.PIG_AMBIENT;
        };
        if (player.level().isClientSide()) {
            player.playSound(sound, 1.0F, 1.0F);
        } else if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.playNotifySound(sound, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(ItemTooltipText.flavor("pastoral_melody", 0));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.heading("pastoral_melody", 1));
        tooltip.add(ItemTooltipText.description("pastoral_melody", 2));
    }
}
