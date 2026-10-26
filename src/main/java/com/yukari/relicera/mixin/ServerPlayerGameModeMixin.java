package com.yukari.relicera.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.yukari.relicera.common.item.feysilver.FeysilverMiningContext;
import com.yukari.relicera.common.item.feysilver.FeysilverPickaxeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow @Final protected ServerPlayer player;

    @WrapMethod(method = "destroyBlock")
    private boolean relicera$selectFeysilverMiningEnchantments(BlockPos position, Operation<Boolean> original) {
        if (!(player.getMainHandItem().getItem() instanceof FeysilverPickaxeItem)) {
            return original.call(position);
        }
        try (FeysilverMiningContext ignored = FeysilverMiningContext.begin(player)) {
            return original.call(position);
        }
    }
}
