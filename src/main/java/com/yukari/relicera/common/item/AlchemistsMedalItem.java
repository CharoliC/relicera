package com.yukari.relicera.common.item;

import com.yukari.relicera.client.event.AlchemistsMedalClientInteraction;
import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public final class AlchemistsMedalItem extends QuickEquipCurioItem {
    private static final String BEARSKIN_CONSUMED_TAG = "ReliceraBearskinConsumed";

    public AlchemistsMedalItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack medal, Slot slot, ClickAction action, Player player) {
        if (consumeBearskin(medal, slot.getItem(), slot, action, player, false)) {
            return true;
        }
        ItemStack gold = slot.getItem();
        if (action != ClickAction.SECONDARY || !gold.is(Items.GOLD_INGOT)
                || !slot.allowModification(player) || !canTransmute(player)) {
            return false;
        }
        if (player.level().isClientSide()) {
            requestCreativeInteraction(player, slot, gold.getCount(), false);
        } else if (player instanceof ServerPlayer serverPlayer) {
            int count = player.getAbilities().instabuild ? gold.getCount() : slot.remove(gold.getCount()).getCount();
            awardExperience(serverPlayer, count);
            slot.setChanged();
        }
        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack medal, ItemStack gold, Slot slot, ClickAction action,
                                           Player player, SlotAccess carriedSlot) {
        if (consumeBearskin(medal, gold, slot, action, player, true)) {
            return true;
        }
        if (action != ClickAction.SECONDARY || !gold.is(Items.GOLD_INGOT)
                || !slot.allowModification(player) || !canTransmute(player)) {
            return false;
        }
        if (player.level().isClientSide()) {
            requestCreativeInteraction(player, slot, gold.getCount(), true);
        } else if (player instanceof ServerPlayer serverPlayer) {
            int count = gold.getCount();
            if (!player.getAbilities().instabuild) {
                gold.shrink(count);
            }
            awardExperience(serverPlayer, count);
        }
        return true;
    }

    public static boolean hasConsumedBearskin(ItemStack medal) {
        return medal.is(ModItems.ALCHEMISTS_MEDAL.get()) && medal.hasTag()
                && medal.getTag().getBoolean(BEARSKIN_CONSUMED_TAG);
    }

    private static boolean consumeBearskin(ItemStack medal, ItemStack bearskin, Slot slot, ClickAction action,
                                           Player player, boolean bearskinOnCursor) {
        if (action != ClickAction.SECONDARY || !bearskin.is(ModItems.DEVILS_BEARSKIN.get())
                || !slot.allowModification(player) || !player.isAlive() || player.isSpectator()) {
            return false;
        }
        if (hasConsumedBearskin(medal)) {
            return true;
        }
        boolean creative = player.getAbilities().instabuild;
        if (player.level().isClientSide() && !creative) {
            return true;
        }
        if (!creative) {
            if (bearskinOnCursor) {
                bearskin.shrink(1);
            } else if (slot.remove(1).isEmpty()) {
                return true;
            }
        }
        medal.getOrCreateTag().putBoolean(BEARSKIN_CONSUMED_TAG, true);
        slot.setChanged();
        if (player.level().isClientSide()) {
            player.playSound(SoundEvents.ILLUSIONER_CAST_SPELL, 1.0F, 0.8F);
        } else if (!creative && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.playNotifySound(SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 0.8F);
        }
        return true;
    }

    private static boolean canTransmute(Player player) {
        return player.isAlive() && !player.isSpectator() && ModCommonConfig.ALCHEMISTS_MEDAL_EXPERIENCE_PER_INGOT.get() > 0;
    }

    private static void requestCreativeInteraction(Player player, Slot slot, int count, boolean goldOnCursor) {
        if (player.getAbilities().instabuild) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> AlchemistsMedalClientInteraction.send(player, slot, count, goldOnCursor));
        }
    }

    public static void handleCreativeInteraction(ServerPlayer player, int inventorySlot, int count, boolean goldOnCursor) {
        if (!player.getAbilities().instabuild || !canTransmute(player)
                || player.containerMenu != player.inventoryMenu
                || inventorySlot < 0 || inventorySlot >= player.getInventory().items.size()
                || count < 1 || count > Items.GOLD_INGOT.getMaxStackSize()) {
            return;
        }
        ItemStack target = player.getInventory().getItem(inventorySlot);
        if (goldOnCursor) {
            if (!target.is(ModItems.ALCHEMISTS_MEDAL.get())) {
                return;
            }
        } else {
            if (!target.is(Items.GOLD_INGOT) || target.getCount() != count) {
                return;
            }
            count = target.getCount();
        }
        awardExperience(player, count);
    }

    private static void awardExperience(ServerPlayer player, int count) {
        if (count <= 0) {
            return;
        }
        long experience = (long) count * ModCommonConfig.ALCHEMISTS_MEDAL_EXPERIENCE_PER_INGOT.get();
        ExperienceOrb.award(player.serverLevel(), player.position(), (int) Math.min(Integer.MAX_VALUE, experience));
        player.playNotifySound(SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 0.8F);
    }
}
