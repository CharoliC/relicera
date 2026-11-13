package com.yukari.relicera.common.item;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.yukari.relicera.client.renderer.StonewallGreatshieldClientExtensions;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.AnvilUpdateEvent;

import java.util.function.Consumer;

public final class StonewallGreatshieldItem extends ShieldItem {
    public static final int MAX_DURABILITY = 1275;
    public static final double ATTACK_DAMAGE = 9.0D;
    public static final double ATTACK_SPEED = 0.4D;
    public static final int REPAIR_PER_EMERALD_BLOCK = MAX_DURABILITY / 2;

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public StonewallGreatshieldItem(Properties properties) {
        super(properties);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID,
                "Weapon modifier",
                ATTACK_DAMAGE - 1.0D,
                AttributeModifier.Operation.ADDITION
        ));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                BASE_ATTACK_SPEED_UUID,
                "Weapon modifier",
                ATTACK_SPEED - 4.0D,
                AttributeModifier.Operation.ADDITION
        ));
        this.defaultModifiers = builder.build();
    }

    public static void updateAnvilRepair(AnvilUpdateEvent event) {
        ItemStack shield = event.getLeft();
        ItemStack material = event.getRight();
        if (!shield.is(ModItems.STONEWALL_GREATSHIELD.get())
                || !material.is(Items.EMERALD_BLOCK)
                || !shield.isDamaged()) {
            return;
        }

        int materialCost = Math.min(
                material.getCount(),
                (shield.getDamageValue() + REPAIR_PER_EMERALD_BLOCK - 1) / REPAIR_PER_EMERALD_BLOCK
        );
        if (materialCost <= 0) {
            return;
        }

        ItemStack output = createRepairedCopy(shield, materialCost);

        int renameCost = applyAnvilName(event.getName(), shield, output);
        int levelCost = event.getCost() + materialCost + renameCost;
        Player player = event.getPlayer();
        if (levelCost >= 40 && !player.getAbilities().instabuild) {
            event.setCanceled(true);
            return;
        }

        output.setRepairCost(AnvilMenu.calculateIncreasedRepairCost(
                Math.max(shield.getBaseRepairCost(), material.getBaseRepairCost())
        ));
        event.setOutput(output);
        event.setCost(levelCost);
        event.setMaterialCost(materialCost);
    }

    public static ItemStack createRepairedCopy(ItemStack shield, int materialCount) {
        ItemStack output = shield.copy();
        output.setDamageValue(Math.max(0, shield.getDamageValue() - materialCount * REPAIR_PER_EMERALD_BLOCK));
        return output;
    }

    private static int applyAnvilName(String name, ItemStack input, ItemStack output) {
        if (name == null) {
            return 0;
        }
        if (Util.isBlank(name)) {
            if (input.hasCustomHoverName()) {
                output.resetHoverName();
                return 1;
            }
        } else if (!name.equals(input.getHoverName().getString())) {
            output.setHoverName(Component.literal(name));
            return 1;
        }
        return 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(ItemTooltipText.flavor("stonewall_greatshield", 0));
    }

    @Override
    public boolean isValidRepairItem(ItemStack shield, ItemStack ingredient) {
        return ingredient.is(Items.EMERALD_BLOCK);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND
                ? this.defaultModifiers
                : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(StonewallGreatshieldClientExtensions.INSTANCE);
    }
}
