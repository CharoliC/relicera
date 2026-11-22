package com.yukari.relicera.common.entity.lumen;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public final class LumenStar extends AbstractArrow {
    private static final double BASE_ARROW_DAMAGE = 2.0D;
    private static final float FLAME_CHANCE_PER_TICK = 0.15F;
    private double shotDamage = 12.0D;
    private double blastRadius = 1.5D;
    private UUID launchTarget;
    private int age;
    private boolean detonated;

    public LumenStar(EntityType<? extends LumenStar> type, Level level) {
        super(type, level);
        setBaseDamage(BASE_ARROW_DAMAGE);
        pickup = Pickup.DISALLOWED;
    }

    public void configureShot(double damage, double radius, double arrowDamage, Entity directTarget) {
        shotDamage = damage;
        blastRadius = radius;
        setBaseDamage(arrowDamage);
        launchTarget = directTarget == null ? null : directTarget.getUUID();
    }

    @Override
    public void tick() {
        if (level() instanceof ServerLevel level) {
            if (++age >= 200) {
                discard();
                return;
            }
            if (launchTarget != null) {
                Entity target = level.getEntity(launchTarget);
                if (target == null || !target.getBoundingBox().inflate(0.3D).intersects(getBoundingBox())) {
                    launchTarget = null;
                }
            }
        }
        Vec3 previousPosition = position();
        super.tick();
        if (!isRemoved() && inGround) {
            detonate(position(), null);
        }
        if (level().isClientSide() && !isRemoved() && !inGround) {
            spawnFlameTrail(previousPosition);
        }
    }

    private void spawnFlameTrail(Vec3 previousPosition) {
        Vec3 movement = position().subtract(previousPosition);
        if (movement.lengthSqr() < 0.000001D || random.nextFloat() >= FLAME_CHANCE_PER_TICK) {
            return;
        }
        Vec3 point = previousPosition.add(movement.scale(random.nextDouble()));
        level().addParticle(ParticleTypes.FLAME,
                point.x + (random.nextDouble() - 0.5D) * 0.08D,
                point.y + getBbHeight() * 0.5D + (random.nextDouble() - 0.5D) * 0.08D,
                point.z + (random.nextDouble() - 0.5D) * 0.08D,
                0.0D, 0.0D, 0.0D);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return !entity.getUUID().equals(launchTarget) && LumenCombat.canHit(getOwner(), entity)
                && super.canHitEntity(entity);
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        detonate(hit.getLocation(), hit.getEntity());
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        detonate(hit.getLocation().add(Vec3.atLowerCornerOf(hit.getDirection().getNormal()).scale(0.01D)), null);
    }

    private void detonate(Vec3 center, Entity directTarget) {
        if (!(level() instanceof ServerLevel level) || detonated) {
            return;
        }
        detonated = true;
        level.sendParticles(LumenCircles.STAR_IMPACT, center.x, center.y + 0.03D, center.z,
                1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z,
                0, 1.0D, 0.0D, 0.0D, 1.0D);
        level.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE,
                SoundSource.PLAYERS, 0.45F, 1.6F);
        LumenCombat.hurtInRadius(level, center, getOwner(), directTarget,
                target -> shotDamage * getBaseDamage() / BASE_ARROW_DAMAGE, blastRadius,
                (target, amount) -> LumenCombat.hurtIgnoringCooldown(target,
                        damageSources().arrow(this, getOwner()), amount));
        LumenCombat.hurtEndCrystal(directTarget, this);
        discard();
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble("LumenDamage", shotDamage);
        tag.putDouble("LumenRadius", blastRadius);
        tag.putInt("LumenAge", age);
        if (launchTarget != null) {
            tag.putUUID("LaunchTarget", launchTarget);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        shotDamage = tag.getDouble("LumenDamage");
        blastRadius = Math.max(0.1D, tag.getDouble("LumenRadius"));
        age = tag.getInt("LumenAge");
        launchTarget = tag.hasUUID("LaunchTarget") ? tag.getUUID("LaunchTarget") : null;
        pickup = Pickup.DISALLOWED;
    }
}
