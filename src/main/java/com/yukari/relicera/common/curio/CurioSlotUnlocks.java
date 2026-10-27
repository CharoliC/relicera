package com.yukari.relicera.common.curio;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.common.network.packet.SlotUnlockToastPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ReliceraMod.MOD_ID)
public final class CurioSlotUnlocks {
    private static final String DATA_KEY = "ReliceraConsumableSlots";

    private CurioSlotUnlocks() {
    }

    public enum Reward {
        SIRENS_EYE("ring", "5f5293ca-a9a7-49bc-b46f-84b30ac9f9bc"),
        ASPHODEL_BLOOM("charm", "0ed86610-b3e2-492b-b115-879effd03967");

        private final String slot;
        private final UUID modifierId;

        Reward(String slot, String modifierId) {
            this.slot = slot;
            this.modifierId = UUID.fromString(modifierId);
        }
    }

    public static boolean hasUnlocked(Player player, Reward reward) {
        return player.getPersistentData().getCompound(DATA_KEY).getBoolean(reward.name())
                || CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(handler -> handler.getStacksHandler(reward.slot))
                .map(handler -> handler.getModifiers().containsKey(reward.modifierId)).orElse(false);
    }

    public static boolean unlock(ServerPlayer player, Reward reward) {
        if (hasUnlocked(player, reward)) {
            return false;
        }
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(player).resolve().orElse(null);
        if (handler == null || handler.getStacksHandler(reward.slot).isEmpty()) {
            return false;
        }
        addModifier(handler, reward);
        CompoundTag data = player.getPersistentData().getCompound(DATA_KEY);
        data.putBoolean(reward.name(), true);
        player.getPersistentData().put(DATA_KEY, data);
        ModNetworking.sendToPlayer(new SlotUnlockToastPacket(reward), player);
        return true;
    }

    private static void addModifier(ICuriosItemHandler handler, Reward reward) {
        if (handler.getStacksHandler(reward.slot)
                .filter(slots -> !slots.getModifiers().containsKey(reward.modifierId)).isEmpty()) {
            return;
        }
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
        modifiers.put(reward.slot, new AttributeModifier(reward.modifierId,
                "Relicera consumable " + reward.slot + " slot", 1.0D, AttributeModifier.Operation.ADDITION));
        handler.addPermanentSlotModifiers(modifiers);
    }

    private static void restore(Player player) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            CompoundTag data = player.getPersistentData().getCompound(DATA_KEY);
            for (Reward reward : Reward.values()) {
                if (data.getBoolean(reward.name())) {
                    addModifier(handler, reward);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        event.getEntity().getPersistentData().put(DATA_KEY,
                event.getOriginal().getPersistentData().getCompound(DATA_KEY).copy());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        restore(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        restore(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        restore(event.getEntity());
    }
}
