package com.yukari.relicera.common.curio;

import com.yukari.relicera.registry.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import top.theillusivec4.curios.api.CuriosApi;

public final class SweetGemEffects {
    private SweetGemEffects() {
    }

    public static void applyConsumptionBonus(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()
                || !event.getItem().isEdible()) {
            return;
        }

        boolean equipped = CuriosApi.getCuriosInventory(player).resolve()
                .map(handler -> handler.isEquipped(ModItems.SWEET_GEM.get()))
                .orElse(false);
        if (equipped) {
            player.getFoodData().eat(1, 1.8F);
        }
    }
}
