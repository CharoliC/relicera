package com.yukari.relicera.common.item.chronometer;

import com.yukari.relicera.registry.ModSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class StoppedSilverChronometerItem extends Item {
    public StoppedSilverChronometerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack watch = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            int current = StoppedSilverChronometerEffects.getStep(watch);
            int next = Mth.clamp(current + (player.isShiftKeyDown() ? 1 : -1),
                    StoppedSilverChronometerEffects.MIN_STEP, StoppedSilverChronometerEffects.MAX_STEP);

            if (next != current) {
                StoppedSilverChronometerEffects.setStep(watch, next);
                StoppedSilverChronometerEffects.updatePlayer(player);
                player.getInventory().setChanged();
                player.containerMenu.broadcastChanges();
                player.awardStat(Stats.ITEM_USED.get(this));
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSoundEvents.RABBITS_POCKET_WATCH_USE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
        return InteractionResultHolder.sidedSuccess(watch, level.isClientSide());
    }
}
