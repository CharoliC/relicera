package com.yukari.relicera.common.entity.lumen;

import com.yukari.relicera.mixin.LivingEntityAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.ToDoubleFunction;

final class LumenCombat {
    private LumenCombat() {
    }

    static boolean canDamage(Entity owner, LivingEntity target) {
        if (!target.isAlive() || target.isSpectator() || target == owner || target instanceof ArmorStand) {
            return false;
        }
        if (owner == null) {
            return true;
        }
        if (owner.isAlliedTo(target) || target.isAlliedTo(owner)
                || target instanceof OwnableEntity owned && owner.getUUID().equals(owned.getOwnerUUID())) {
            return false;
        }
        return !(owner instanceof Player player && target instanceof Player other && !player.canHarmPlayer(other));
    }

    static void hurtInRadius(ServerLevel level, Vec3 center, Entity owner, Entity directTarget,
                             ToDoubleFunction<LivingEntity> damage, double radius, BiConsumer<LivingEntity, Float> hurt) {
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(center, center).inflate(radius), target -> canDamage(owner, target))) {
            AABB box = target.getBoundingBox();
            Vec3 closest = new Vec3(Mth.clamp(center.x, box.minX, box.maxX),
                    Mth.clamp(center.y, box.minY, box.maxY), Mth.clamp(center.z, box.minZ, box.maxZ));
            double factor = target == directTarget ? 1.0D
                    : Math.max(0.0D, 1.0D - closest.distanceTo(center) / radius)
                            * Explosion.getSeenPercent(center, target);
            float amount = (float) (damage.applyAsDouble(target) * factor);
            if (Float.isFinite(amount) && amount > 0.0F) {
                hurt.accept(target, amount);
            }
        }
    }

    static boolean hurtIgnoringCooldown(LivingEntity target, DamageSource source, float amount) {
        if (!target.isAlive()) {
            return false;
        }
        LivingEntityAccessor accessor = (LivingEntityAccessor) target;
        float previousLastHurt = accessor.relicera$getLastHurt();
        int previousInvulnerableTime = target.invulnerableTime;
        try {
            return target.hurt(new AdditionalDamageSource(source), amount);
        } finally {
            if (accessor.relicera$getLastHurt() == amount) {
                accessor.relicera$setLastHurt(previousLastHurt);
                if (target.invulnerableTime == 20) {
                    target.invulnerableTime = previousInvulnerableTime;
                }
            }
        }
    }

    private static final class AdditionalDamageSource extends DamageSource {
        private AdditionalDamageSource(DamageSource source) {
            super(source.typeHolder(), source.getDirectEntity(), source.getEntity());
        }

        @Override
        public boolean is(TagKey<DamageType> tag) {
            return DamageTypeTags.BYPASSES_COOLDOWN.equals(tag) || super.is(tag);
        }
    }
}
