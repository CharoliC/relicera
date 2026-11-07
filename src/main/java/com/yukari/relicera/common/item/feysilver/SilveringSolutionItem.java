package com.yukari.relicera.common.item.feysilver;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.entity.forgeling.Forgeling;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public final class SilveringSolutionItem extends Item {
    private static final ResourceLocation SILVERED_FORGELING_ADVANCEMENT =
            ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "silvered_forgeling");

    public SilveringSolutionItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return FeysilverForgingClientData.materialName(super.getName(stack));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player == null || !level.getBlockState(pos).is(Blocks.ANCIENT_DEBRIS)) {
            return InteractionResult.PASS;
        }
        if (!FeysilverForgingKnowledge.canUseSilveringSolution(player)) {
            return InteractionResult.FAIL;
        }
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, context.getClickedFace(), stack)) {
            return InteractionResult.FAIL;
        }
        if (level instanceof ServerLevel serverLevel) {
            if (!SilveringSolutionEffects.start(serverLevel, pos)) {
                return InteractionResult.CONSUME;
            }
            consumeSolution(player, context.getHand(), stack);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof Forgeling forgeling)) {
            return InteractionResult.PASS;
        }
        if (!FeysilverForgingKnowledge.canUseSilveringSolution(player)) {
            return InteractionResult.FAIL;
        }
        if (!forgeling.isAlive() || forgeling.isSilvered()) {
            return InteractionResult.CONSUME;
        }
        if (!target.level().isClientSide() && forgeling.startSilvering()) {
            consumeSolution(player, hand, stack);
            if (player instanceof ServerPlayer serverPlayer) {
                awardSilveredForgelingAdvancement(serverPlayer);
            }
        }
        return InteractionResult.sidedSuccess(target.level().isClientSide());
    }

    private static void awardSilveredForgelingAdvancement(ServerPlayer player) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(SILVERED_FORGELING_ADVANCEMENT);
        if (advancement != null) {
            player.getAdvancements().award(advancement, "used_solution_on_forgeling");
        }
    }

    private void consumeSolution(Player player, InteractionHand hand, ItemStack stack) {
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
        }
    }
}
