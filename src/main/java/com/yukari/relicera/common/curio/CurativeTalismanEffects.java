package com.yukari.relicera.common.curio;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.common.network.packet.ItemActivationPacket;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class CurativeTalismanEffects {
    private static final ResourceLocation TRANSFORMATION_ADVANCEMENT =
            ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "sufferers_exultation");
    private static final String RESISTANCE_TAG = "ReliceraCurativeResistance";
    private static final int MAX_RESISTANCE_LEVEL = 4;
    private static final Map<LivingEntity, Integer> PENDING_RECOVERIES = new IdentityHashMap<>();

    private CurativeTalismanEffects() {
    }

    public static int countHarmfulEffects(LivingEntity entity) {
        int count = 0;
        for (MobEffectInstance effect : entity.getActiveEffects()) {
            if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL
                    && (effect.isInfiniteDuration() || effect.getDuration() > 0)) {
                count++;
            }
        }
        return count;
    }

    public static void tick(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return;
        }
        entity.getPersistentData().remove("ReliceraCurativeHadHarmfulEffects");
        tickResistance(entity);
        var inventory = CuriosApi.getCuriosInventory(entity).resolve();
        List<SlotResult> talismans = inventory.map(handler -> handler.findCurios(ModItems.CURATIVE_TALISMAN.get()))
                .orElse(List.of());
        if (!entity.isAlive() || entity.isSpectator() || talismans.isEmpty()) {
            PENDING_RECOVERIES.remove(entity);
            TalismanEffectOwnership.update(entity, MobEffects.REGENERATION, -1);
            return;
        }

        int count = countHarmfulEffects(entity);
        if (entity.isOnFire() && count >= ModCommonConfig.CURATIVE_TALISMAN_TRANSFORMATION_THRESHOLD.get()) {
            PENDING_RECOVERIES.remove(entity);
            TalismanEffectOwnership.update(entity, MobEffects.REGENERATION, -1);
            inventory.ifPresent(handler -> {
                for (SlotResult slot : talismans) {
                    ItemStack transformed = new ItemStack(ModItems.SUFFERERS_EXULTATION.get());
                    if (slot.stack().hasCustomHoverName()) {
                        transformed.setHoverName(slot.stack().getHoverName());
                    }
                    handler.setEquippedCurio(slot.slotContext().identifier(), slot.slotContext().index(), transformed);
                }
                if (entity instanceof ServerPlayer player) {
                    awardTransformationAdvancement(player);
                    ModNetworking.sendToPlayer(new ItemActivationPacket(new ItemStack(ModItems.SUFFERERS_EXULTATION.get())), player);
                }
            });
            return;
        }

        int interval = ModCommonConfig.CURATIVE_TALISMAN_EFFECTS_PER_REGENERATION_LEVEL.get();
        TalismanEffectOwnership.update(entity, MobEffects.REGENERATION,
                count == 0 ? -1 : (count - 1) / interval);
    }

    private static void awardTransformationAdvancement(ServerPlayer player) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(TRANSFORMATION_ADVANCEMENT);
        if (advancement != null) {
            player.getAdvancements().award(advancement, "transformed_talisman");
        }
    }

    public static void onEffectRemoved(LivingEntity entity, MobEffectInstance effect) {
        if (entity.level().isClientSide() || TalismanEffectOwnership.isInternalRemoval(effect)) {
            return;
        }
        if (effect.getEffect() == MobEffects.DAMAGE_RESISTANCE) {
            entity.getPersistentData().remove(RESISTANCE_TAG);
        }
        if (!entity.isAlive() || entity.isSpectator()) {
            entity.getPersistentData().remove(RESISTANCE_TAG);
            PENDING_RECOVERIES.remove(entity);
            return;
        }
        if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL && isEquipped(entity)) {
            PENDING_RECOVERIES.merge(entity, 1, Integer::sum);
        }
    }

    public static void processPendingRecoveries(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || PENDING_RECOVERIES.isEmpty()) {
            return;
        }
        Map<LivingEntity, Integer> pending = new IdentityHashMap<>(PENDING_RECOVERIES);
        PENDING_RECOVERIES.clear();
        pending.forEach((entity, count) -> {
            if (entity.isAlive() && !entity.isRemoved() && !entity.isSpectator() && isEquipped(entity)) {
                grantResistance(entity, count);
            }
        });
    }

    public static void clearPendingRecoveries() {
        PENDING_RECOVERIES.clear();
    }

    private static boolean isEquipped(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).resolve()
                .map(handler -> handler.isEquipped(ModItems.CURATIVE_TALISMAN.get())).orElse(false);
    }

    private static void tickResistance(LivingEntity entity) {
        CompoundTag data = entity.getPersistentData();
        if (!data.contains(RESISTANCE_TAG, Tag.TAG_COMPOUND)) {
            return;
        }
        CompoundTag resistance = data.getCompound(RESISTANCE_TAG);
        int remaining = resistance.getInt("RemainingTicks");
        if (!entity.isAlive() || !entity.hasEffect(MobEffects.DAMAGE_RESISTANCE) || remaining <= 1) {
            data.remove(RESISTANCE_TAG);
        } else {
            resistance.putInt("RemainingTicks", remaining - 1);
        }
    }

    private static void grantResistance(LivingEntity entity, int recoveries) {
        CompoundTag data = entity.getPersistentData();
        CompoundTag resistance = data.getCompound(RESISTANCE_TAG);
        int level = Math.min(MAX_RESISTANCE_LEVEL, Math.max(0, resistance.getInt("Level")));
        int progress = Math.max(0, resistance.getInt("Progress"));
        if (level == 0 || resistance.getInt("RemainingTicks") <= 0
                || !entity.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
            level = 1;
            progress = 0;
            recoveries--;
        }
        int interval = ModCommonConfig.CURATIVE_TALISMAN_REMOVALS_PER_RESISTANCE_LEVEL.get();
        long totalProgress = (long) progress + recoveries;
        level = (int) Math.min(MAX_RESISTANCE_LEVEL, level + totalProgress / interval);
        progress = level == MAX_RESISTANCE_LEVEL ? 0 : (int) (totalProgress % interval);
        int duration = ModCommonConfig.CURATIVE_TALISMAN_RESISTANCE_DURATION_TICKS.get();
        MobEffectInstance reward = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, level - 1);
        if (!entity.canBeAffected(reward)) {
            return;
        }
        entity.addEffect(reward);
        if (!entity.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
            return;
        }
        resistance.putInt("Level", level);
        resistance.putInt("Progress", progress);
        resistance.putInt("RemainingTicks", duration);
        data.put(RESISTANCE_TAG, resistance);
    }
}
