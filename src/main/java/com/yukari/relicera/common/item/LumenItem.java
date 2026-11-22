package com.yukari.relicera.common.item;

import com.yukari.relicera.common.entity.lumen.LumenMoon;
import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.common.network.packet.CameraShakePacket;
import com.yukari.relicera.registry.ModEntityTypes;
import com.yukari.relicera.registry.ModEnchantments;
import com.yukari.relicera.registry.ModSoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public final class LumenItem extends BowItem {
    public LumenItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResultHolder<ItemStack> result = ForgeEventFactory.onArrowNock(stack, level, player, hand, true);
        if (result != null) {
            return result;
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remainingTicks) {
        if (!(entity instanceof Player player)) {
            return;
        }
        int ticks = ForgeEventFactory.onArrowLoose(stack, level, player,
                getUseDuration(stack) - remainingTicks, true);
        if (ticks < 0) {
            return;
        }
        float power = getPowerForTime(ticks);
        if (power < 0.1F) {
            return;
        }
        if (!level.isClientSide()) {
            LumenMoon moon = new LumenMoon(ModEntityTypes.LUMEN_MOON.get(), level);
            moon.setOwner(player);
            moon.setPos(player.getX(), player.getEyeY() - 0.1D, player.getZ());
            moon.configureShot(power, stack.getEnchantmentLevel(ModEnchantments.STARCROWN.get()) > 0,
                    stack.getEnchantmentLevel(ModEnchantments.DAYLIGHT.get()),
                    stack.getEnchantmentLevel(ModEnchantments.MOONFROST.get()));
            AbstractArrow projectile = customArrow(moon);
            Vec3 direction = player.getLookAngle();
            projectile.shoot(direction.x, direction.y, direction.z, 3.0F * power, 0.0F);
            if (!level.addFreshEntity(projectile)) {
                return;
            }
            stack.hurtAndBreak(1, player, holder -> holder.broadcastBreakEvent(player.getUsedItemHand()));
            level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.LUMEN_FIRE.get(),
                    SoundSource.PLAYERS, 0.2F + 0.5F * power, 1.2F - 0.2F * power);
            if (player instanceof ServerPlayer serverPlayer) {
                ModNetworking.sendToPlayer(new CameraShakePacket(Math.round(4.0F + 4.0F * power),
                        0.8F * power), serverPlayer);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return !stack.isEnchanted();
    }

    @Override
    public int getEnchantmentValue() {
        return 21;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(Items.NETHER_STAR);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment != ModEnchantments.STARCROWN.get() && isAllowedEnchantment(enchantment);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        var enchantments = EnchantmentHelper.getEnchantments(book);
        return !enchantments.isEmpty()
                && enchantments.keySet().stream().allMatch(LumenItem::isAllowedEnchantment);
    }

    private static boolean isAllowedEnchantment(Enchantment enchantment) {
        return ModEnchantments.isLumenEnchantment(enchantment)
                || enchantment == Enchantments.UNBREAKING || enchantment == Enchantments.MENDING;
    }
}
