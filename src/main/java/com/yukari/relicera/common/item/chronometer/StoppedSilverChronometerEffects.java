package com.yukari.relicera.common.item.chronometer;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ReliceraMod.MOD_ID)
public final class StoppedSilverChronometerEffects {
    public static final int MIN_STEP = -20;
    public static final int MAX_STEP = 20;
    private static final String STEP_TAG = "ReliceraChronometerStep";
    private static final UUID SPEED_MODIFIER_ID = UUID.fromString("a08cd152-7d74-4724-9a38-49304396f602");

    private StoppedSilverChronometerEffects() {
    }

    public static int getStep(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? 0 : Mth.clamp(tag.getInt(STEP_TAG), MIN_STEP, MAX_STEP);
    }

    public static void setStep(ItemStack stack, int step) {
        if (step == 0) {
            stack.removeTagKey(STEP_TAG);
        } else {
            stack.getOrCreateTag().putInt(STEP_TAG, Mth.clamp(step, MIN_STEP, MAX_STEP));
        }
    }

    public static int getActiveStep(Player player) {
        if (isChronometer(player.getMainHandItem())) {
            return getStep(player.getMainHandItem());
        }
        if (isChronometer(player.getOffhandItem())) {
            return getStep(player.getOffhandItem());
        }
        for (ItemStack stack : player.getInventory().items) {
            if (isChronometer(stack)) {
                return getStep(stack);
            }
        }
        return 0;
    }

    private static boolean isChronometer(ItemStack stack) {
        return stack.is(ModItems.STOPPED_SILVER_CHRONOMETER.get());
    }

    public static void updatePlayer(Player player) {
        if (player.level().isClientSide()) {
            return;
        }
        int step = getActiveStep(player);
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            double amount = step / 20.0D;
            AttributeModifier existing = speed.getModifier(SPEED_MODIFIER_ID);
            if (existing != null && (step == 0 || existing.getAmount() != amount)) {
                speed.removeModifier(SPEED_MODIFIER_ID);
                existing = null;
            }
            if (step != 0 && existing == null) {
                speed.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_ID,
                        "Relicera stopped silver chronometer movement speed", amount,
                        AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
        if (step == MIN_STEP) {
            ((ChronometerCooldownControl) player.getCooldowns()).relicera$clearChronometerCooldowns();
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            updatePlayer(event.player);
        }
    }

    @SubscribeEvent
    public static void onMissingMappings(MissingMappingsEvent event) {
        for (var mapping : event.getMappings(ForgeRegistries.Keys.ITEMS, ReliceraMod.MOD_ID)) {
            if (mapping.getKey().getPath().equals("rabbits_pocket_watch")) {
                mapping.remap(ModItems.STOPPED_SILVER_CHRONOMETER.get());
            }
        }
    }
}
