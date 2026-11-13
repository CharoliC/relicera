package com.yukari.relicera.common.item.armor.devilsbearskin;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.item.ItemTooltipText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;
import com.yukari.relicera.client.renderer.DevilsBearskinClientExtensions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class DevilsBearskinItem extends ArmorItem {
    private static final String ARMOR_TEXTURE = ReliceraMod.MOD_ID
            + ":textures/models/armor/devils_bearskin.png";

    public DevilsBearskinItem(Properties properties) {
        super(DevilsBearskinArmorMaterial.INSTANCE, Type.CHESTPLATE, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.armorEffectsHeader());
        if (DevilsBearskinEffects.isReleased(stack)) {
            tooltip.add(ItemTooltipText.curioEffect("devils_bearskin", 8));
            tooltip.add(Component.empty());
            tooltip.add(ItemTooltipText.quote("devils_bearskin", 9));
            return;
        }
        tooltip.add(ItemTooltipText.curioEffect("devils_bearskin", 0));
        tooltip.add(ItemTooltipText.curioEffect("devils_bearskin", 1));
        tooltip.add(ItemTooltipText.curioEffect("devils_bearskin", 2));
        tooltip.add(ItemTooltipText.curioEffect("devils_bearskin", 3));
        if (DevilsBearskinEffects.hasReachedTradeRefusalStage(stack)) {
            tooltip.add(ItemTooltipText.curioEffect("devils_bearskin", 4));
        }
        if (DevilsBearskinEffects.hasReachedNauseaStage(stack)) {
            tooltip.add(ItemTooltipText.curioEffect("devils_bearskin", 5));
        }
        tooltip.add(ItemTooltipText.curioEffect("devils_bearskin", 6,
                ItemTooltipText.unsignedPercent(DevilsBearskinEffects.MAX_HEALTH_LOSS)));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.quote("devils_bearskin", 7));
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack bearskin,
            ItemStack carried,
            Slot slot,
            ClickAction action,
            Player player,
            SlotAccess carriedSlot
    ) {
        if (action != ClickAction.SECONDARY) {
            return false;
        }

        if (DevilsBearskinEffects.canExchangeWealth(bearskin, player)) {
            if (carried.is(Items.GOLD_INGOT)) {
                return replaceCarriedStack(
                        carriedSlot,
                        new ItemStack(Items.EMERALD, carried.getCount()),
                        player,
                        SoundEvents.VILLAGER_YES
                );
            }
            if (carried.is(Items.EMERALD)) {
                return replaceCarriedStack(
                        carriedSlot,
                        new ItemStack(Items.GOLD_INGOT, carried.getCount()),
                        player,
                        SoundEvents.PIGLIN_JEALOUS
                );
            }
        }

        if (carried.isEmpty() && DevilsBearskinEffects.canExtractGold(bearskin, player)) {
            return replaceCarriedStack(
                    carriedSlot,
                    new ItemStack(Items.GOLD_INGOT, 64),
                    player,
                    SoundEvents.PIGLIN_JEALOUS
            );
        }
        return false;
    }

    private static boolean replaceCarriedStack(
            SlotAccess carriedSlot,
            ItemStack replacement,
            Player player,
            SoundEvent sound
    ) {
        if (!carriedSlot.set(replacement)) {
            return false;
        }
        if (player.level().isClientSide()) {
            if (player.getAbilities().instabuild) {
                player.playSound(sound, 1.0F, 1.0F);
            }
        } else if (!player.getAbilities().instabuild) {
            player.level().playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    sound,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        }
        return true;
    }

    @Override
    public <T extends LivingEntity> int damageItem(
            ItemStack stack,
            int amount,
            T entity,
            Consumer<T> onBroken
    ) {
        return 0;
    }

    @Override
    public String getArmorTexture(
            ItemStack stack,
            Entity entity,
            EquipmentSlot slot,
            @Nullable String type
    ) {
        return ARMOR_TEXTURE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(DevilsBearskinClientExtensions.INSTANCE);
    }
}
