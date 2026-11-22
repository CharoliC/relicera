package com.yukari.relicera.mixin.compat.irons_spellbooks;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.yukari.relicera.common.item.PrismaticLampItem;
import io.redspace.ironsspellbooks.block.alchemist_cauldron.AlchemistCauldronTile;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@Mixin(value = AlchemistCauldronTile.class, remap = false)
public abstract class AlchemistCauldronMixin {
    @Shadow @Final public NonNullList<ItemStack> inputItems;
    @Unique private static final String RELICERA_GUARANTEED_SLOTS = "ReliceraPrismaticSlots";
    @Unique private final Set<ItemStack> relicera$guaranteedInputs =
            Collections.newSetFromMap(new IdentityHashMap<>());

    @WrapOperation(method = "handleUse", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private Object relicera$recordInsertion(NonNullList<ItemStack> list, int index, Object value,
                                          Operation<Object> original, BlockState state, Level level,
                                          BlockPos pos, Player player, InteractionHand hand) {
        Object previous = original.call(list, index, value);
        if (!level.isClientSide() && list == inputItems) {
            relicera$guaranteedInputs.removeIf(stack -> stack.isEmpty() || !inputItems.contains(stack));
            if (value instanceof ItemStack stack && stack.is(ItemRegistry.SCROLL.get())
                    && PrismaticLampItem.isEquipped(player)) {
                relicera$guaranteedInputs.add(stack);
            }
        }
        return previous;
    }

    @ModifyExpressionValue(method = "tryMeltInput", at = @At(value = "INVOKE",
            target = "Lnet/minecraftforge/common/ForgeConfigSpec$ConfigValue;get()Ljava/lang/Object;"))
    private Object relicera$guaranteeRecycling(Object original, ItemStack input) {
        return input.is(ItemRegistry.SCROLL.get()) && inputItems.contains(input)
                && relicera$guaranteedInputs.contains(input) ? Double.valueOf(1.0D) : original;
    }

    @Inject(method = {"setItem", "m_6836_"}, at = @At("HEAD"))
    private void relicera$clearAutomatedInsertion(int slot, ItemStack stack, CallbackInfo ci) {
        relicera$guaranteedInputs.remove(stack);
        if (slot >= 0 && slot < inputItems.size()) {
            relicera$guaranteedInputs.remove(inputItems.get(slot));
        }
    }

    @Inject(method = {"removeItem", "m_7407_", "removeItemNoUpdate", "m_8016_"}, at = @At("RETURN"), require = 2)
    private void relicera$clearExtraction(CallbackInfoReturnable<ItemStack> cir) {
        relicera$guaranteedInputs.remove(cir.getReturnValue());
    }

    @Inject(method = {"clearContent", "m_6211_"}, at = @At("HEAD"))
    private void relicera$clearContents(CallbackInfo ci) {
        relicera$guaranteedInputs.clear();
    }

    @Inject(method = "saveAdditional", at = @At("RETURN"), remap = true)
    private void relicera$saveGuarantees(CompoundTag tag, CallbackInfo ci) {
        int slots = 0;
        for (int i = 0; i < inputItems.size(); i++) {
            ItemStack input = inputItems.get(i);
            if (!input.isEmpty() && relicera$guaranteedInputs.contains(input)) {
                slots |= 1 << i;
            }
        }
        tag.putInt(RELICERA_GUARANTEED_SLOTS, slots);
    }

    @Inject(method = "load", at = @At("RETURN"), remap = true)
    private void relicera$loadGuarantees(CompoundTag tag, CallbackInfo ci) {
        relicera$guaranteedInputs.clear();
        int slots = tag.getInt(RELICERA_GUARANTEED_SLOTS);
        for (int i = 0; i < inputItems.size(); i++) {
            ItemStack input = inputItems.get(i);
            if ((slots & 1 << i) != 0 && input.is(ItemRegistry.SCROLL.get())) {
                relicera$guaranteedInputs.add(input);
            }
        }
    }
}
