package com.yukari.relicera.common.item;

import com.yukari.relicera.common.curio.FourfoldSherdPendantEffects;
import com.yukari.relicera.common.menu.FourfoldSherdPendantMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class FourfoldSherdPendantItem extends Item implements ICurioItem {
    public static final int SHERD_SLOT_COUNT = 4;
    public static final String SHERDS_TAG = "Sherds";

    public FourfoldSherdPendantItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioEffectsHeader());
        tooltip.add(ItemTooltipText.curioEffect("fourfold_sherd_pendant", 0));

        List<ItemStack> sherds = FourfoldSherdPendantEffects.getSherdsInStack(stack);
        if (sherds.isEmpty()) {
            return;
        }

        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.curioModifiersHeader("necklace"));
        for (ItemStack sherd : sherds) {
            tooltip.add(createFourfoldSherdEffectLine(sherd));
        }
    }

    private static Component createFourfoldSherdEffectLine(ItemStack sherd) {
        FourfoldSherdPendantEffects.SherdPattern pattern = FourfoldSherdPendantEffects.getPattern(sherd);
        if (pattern == null) {
            return ItemTooltipText.description("fourfold_sherd_pendant", 1, Component.literal(sherd.getHoverName().getString()));
        }

        return switch (pattern) {
            case SNORT -> fourfoldEffectLine(2, 22, "+1");
            case BREWER -> fourfoldEffectLine(3, 23, "+20%");
            case MOURNER -> fourfoldEffectLine(4, 24, "+50%");
            case EXPLORER -> fourfoldEffectLine(5, 25, "+20%");
            case HEARTBREAK -> fourfoldEffectLine(6, 26, "30%", "+25%");
            case ARMS_UP -> fourfoldEffectLine(7, 27, "+1");
            case ARCHER -> fourfoldEffectLine(8, 28, "+20%");
            case DANGER -> fourfoldEffectLine(9, 29, "-50%");
            case PLENTY -> fourfoldEffectLine(10, 30, "+1");
            case SHEAF -> fourfoldEffectLine(11, 31, "+1");
            case SHELTER -> fourfoldEffectLine(12, 32, "+4");
            case BLADE -> fourfoldEffectLine(13, 33, "+10%");
            case MINER -> fourfoldEffectLine(14, 34, "+10%");
            case SKULL -> fourfoldEffectLine(15, 35, "+15%");
            case HEART -> fourfoldEffectLine(16, 36, "+4");
            case PRIZE -> fourfoldEffectLine(17, 37, "+1");
            case FRIEND -> fourfoldEffectLine(18, 38, "+1");
            case ANGLER -> fourfoldEffectLine(19, 39, "+1");
            case HOWL -> fourfoldEffectLine(20, 40, "+20%");
            case BURN -> fourfoldEffectLine(21, 41, "+20%");
        };
    }

    private static Component fourfoldEffectLine(int patternLine, int effectLine, String... values) {
        Object[] args = new Object[values.length + 1];
        args[0] = Component.translatable("tooltip.relicera.fourfold_sherd_pendant." + patternLine);
        System.arraycopy(values, 0, args, 1, values.length);
        return ItemTooltipText.description("fourfold_sherd_pendant", effectLine, args);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(
                    serverPlayer,
                    new SimpleMenuProvider(
                            (containerId, inventory, menuPlayer) -> new FourfoldSherdPendantMenu(containerId, inventory, stack),
                            Component.translatable("container.relicera.fourfold_sherd_pendant.title")
                    )
            );
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack stack) {
        return FourfoldSherdPendantEffects.countInStack(stack, FourfoldSherdPendantEffects.SherdPattern.PRIZE);
    }

    @Override
    public int getLootingLevel(SlotContext slotContext, DamageSource source, LivingEntity target, int baseLooting, ItemStack stack) {
        return FourfoldSherdPendantEffects.countInStack(stack, FourfoldSherdPendantEffects.SherdPattern.SNORT);
    }
}
