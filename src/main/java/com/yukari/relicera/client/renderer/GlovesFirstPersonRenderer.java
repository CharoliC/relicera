package com.yukari.relicera.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yukari.relicera.registry.ModItems;
import java.util.Optional;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public final class GlovesFirstPersonRenderer {
    private GlovesFirstPersonRenderer() {
    }

    public static void renderEquippedArm(
            AbstractClientPlayer player,
            HumanoidArm arm,
            ModelPart sourceArm,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight) {
        CuriosApi.getCuriosInventory(player).resolve().ifPresent(handler -> {
            renderItem(handler.findFirstCurio(ModItems.LEATHER_GLOVES.get()), ModItems.LEATHER_GLOVES.get(),
                    player, arm, sourceArm, poseStack, buffers, packedLight);
            renderItem(handler.findFirstCurio(ModItems.NIGHT_GLOVES.get()), ModItems.NIGHT_GLOVES.get(),
                    player, arm, sourceArm, poseStack, buffers, packedLight);
            renderItem(handler.findFirstCurio(ModItems.TREASURE_HUNTERS_GLOVES.get()),
                    ModItems.TREASURE_HUNTERS_GLOVES.get(),
                    player, arm, sourceArm, poseStack, buffers, packedLight);
            renderItem(handler.findFirstCurio(ModItems.THOUSANDWEIGHT_GAUNTLETS.get()),
                    ModItems.THOUSANDWEIGHT_GAUNTLETS.get(),
                    player, arm, sourceArm, poseStack, buffers, packedLight);
        });
    }

    private static void renderItem(
            Optional<SlotResult> equipped,
            Item item,
            AbstractClientPlayer player,
            HumanoidArm arm,
            ModelPart sourceArm,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight) {
        if (equipped.isEmpty() || !equipped.get().slotContext().visible()) {
            return;
        }
        CuriosRendererRegistry.getRenderer(item)
                .filter(FirstPersonGloveRenderer.class::isInstance)
                .map(FirstPersonGloveRenderer.class::cast)
                .ifPresent(renderer -> renderer.renderFirstPersonArm(
                        player, equipped.get().stack(), arm, sourceArm, poseStack, buffers, packedLight));
    }
}
