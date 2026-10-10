package com.yukari.relicera.mixin;

import com.yukari.relicera.common.effect.souloverload.SoulOverloadState;
import com.yukari.relicera.common.item.SoulknotStoneEffects;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Warden.class)
public abstract class WardenMixin implements SoulOverloadState {
    @Unique
    private static final EntityDataAccessor<Integer> relicera$SOUL_OVERLOAD_LEVEL =
            SynchedEntityData.defineId(Warden.class, EntityDataSerializers.INT);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void relicera$defineSoulOverload(CallbackInfo ci) {
        ((Warden) (Object) this).getEntityData().define(relicera$SOUL_OVERLOAD_LEVEL, 0);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void relicera$tickSoulOverload(CallbackInfo ci) {
        SoulknotStoneEffects.tick((Warden) (Object) this);
    }

    @Override
    public int relicera$getSoulOverloadLevel() {
        return ((Warden) (Object) this).getEntityData().get(relicera$SOUL_OVERLOAD_LEVEL);
    }

    @Override
    public void relicera$setSoulOverloadLevel(int level) {
        ((Warden) (Object) this).getEntityData().set(relicera$SOUL_OVERLOAD_LEVEL, level);
    }
}
