package com.yukari.relicera.common.raid;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.curio.CovenantTabletEffects;
import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.common.network.packet.ItemActivationPacket;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public final class RaidVictoryRewards {
    private static final ResourceLocation TURNCOATS_MEDAL_ADVANCEMENT =
            ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "turncoats_medal");
    private static final String TURNCOATS_MEDAL_CRITERION = "victorious_turncoat";

    private RaidVictoryRewards() {
    }

    public static void grant(ServerPlayer player) {
        CovenantTabletEffects.completeRaidTask(player);
        if (transformVindicatorsMedal(player)) {
            awardTurncoatsMedalAdvancement(player);
            ModNetworking.sendToPlayer(new ItemActivationPacket(new ItemStack(ModItems.TURNCOATS_MEDAL.get())), player);
        }
    }

    private static boolean transformVindicatorsMedal(ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player).map(handler -> {
            boolean transformedAny = false;
            for (SlotResult result : handler.findCurios(ModItems.VINDICATORS_MEDAL.get())) {
                ItemStack transformed = new ItemStack(ModItems.TURNCOATS_MEDAL.get());
                if (result.stack().hasCustomHoverName()) {
                    transformed.setHoverName(result.stack().getHoverName());
                }
                handler.setEquippedCurio(
                        result.slotContext().identifier(),
                        result.slotContext().index(),
                        transformed
                );
                transformedAny = true;
            }
            return transformedAny;
        }).orElse(false);
    }

    private static void awardTurncoatsMedalAdvancement(ServerPlayer player) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(TURNCOATS_MEDAL_ADVANCEMENT);
        if (advancement != null) {
            player.getAdvancements().award(advancement, TURNCOATS_MEDAL_CRITERION);
        }
    }
}
