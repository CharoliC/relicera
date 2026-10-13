package com.yukari.relicera.mixin;

import com.yukari.relicera.common.item.feysilver.FeysilverCoating;
import com.yukari.relicera.common.item.silkofnight.SilkOfNightDecoration;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {
    @Shadow
    @Final
    private static Component UPGRADE_TITLE;

    @Inject(method = "appendUpgradeHoverText", at = @At("TAIL"))
    private static void relicera$appendReliceraUpgrades(ItemStack stack, RegistryAccess registryAccess,
                                                        List<Component> tooltip, CallbackInfo ci) {
        if (!(stack.getItem() instanceof ArmorItem)) {
            return;
        }

        boolean hasFeysilverCoating = FeysilverCoating.hasCoating(stack);
        boolean hasSilkOfNightDecoration = SilkOfNightDecoration.hasDecoration(stack);
        if (!hasFeysilverCoating && !hasSilkOfNightDecoration) {
            return;
        }

        if (ArmorTrim.getTrim(registryAccess, stack).isEmpty()) {
            tooltip.add(UPGRADE_TITLE);
        }
        if (hasFeysilverCoating) {
            tooltip.add(CommonComponents.space().append(Component.translatable("tooltip.relicera.feysilver_coating.0")
                    .withStyle(style -> style.withColor(0xC0D1EB))));
        }
        if (hasSilkOfNightDecoration) {
            tooltip.add(CommonComponents.space().append(Component.translatable("tooltip.relicera.silk_of_night_decoration.0")));
        }
    }
}
