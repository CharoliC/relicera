package com.yukari.relicera.common.entity.lumen;

import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.common.network.packet.CameraShakePacket;
import com.yukari.relicera.registry.ModEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public final class LumenMoon extends AbstractArrow {
    private static final double BASE_ARROW_DAMAGE = 2.0D;
    private static final int STATUS_DURATION_TICKS = 200;
    private static final double SHAKE_RADIUS = 12.0D;
    private static final int SHAKE_DURATION_TICKS = 12;
    private static final float SHAKE_INTENSITY = 2.0F;
    private double shotDamage = 40.0D;
    private double blastRadius = 3.0D;
    private boolean starcrown;
    private float shotPower = 1.0F;
    private int moonfrostLevel;
    private double starDamage;
    private double starRadius = 1.5D;
    private int age;
    private boolean detonated;
    private double circleDistance;

    public LumenMoon(EntityType<? extends LumenMoon> type, Level level) {
        super(type, level);
        setNoGravity(true);
        setBaseDamage(BASE_ARROW_DAMAGE);
        pickup = Pickup.DISALLOWED;
    }

    public void configureShot(float power, boolean starcrown, int daylight, int moonfrost) {
        shotPower = Mth.clamp(power, 0.0F, 1.0F);
        double daylightMultiplier = 1.0D + 0.2D * Mth.clamp(daylight, 0, 5);
        shotDamage = ModCommonConfig.LUMEN_DAMAGE.get() * shotPower * daylightMultiplier;
        blastRadius = ModCommonConfig.LUMEN_RADIUS.get();
        this.starcrown = starcrown;
        starDamage = ModCommonConfig.LUMEN_STAR_DAMAGE.get() * shotPower * daylightMultiplier;
        starRadius = ModCommonConfig.LUMEN_STAR_RADIUS.get();
        moonfrostLevel = Mth.clamp(moonfrost, 0, 3);
    }

    @Override
    public void tick() {
        if (!level().isClientSide() && ++age >= 200) {
            discard();
            return;
        }
        setNoGravity(true);
        Vec3 previousPosition = position();
        super.tick();
        if (!isRemoved() && !inGround) {
            setDeltaMovement(getDeltaMovement().scale(1.0D / (double) 0.99F));
            if (level().isClientSide()) {
                emitFlightCircles(previousPosition, position());
            }
        }
    }

    private void emitFlightCircles(Vec3 start, Vec3 end) {
        Vec3 movement = end.subtract(start);
        double distance = movement.length();
        if (distance < 1.0E-6D) {
            return;
        }
        if (distance > 16.0D) {
            circleDistance = 0.0D;
            return;
        }
        Vec3 direction = movement.scale(1.0D / distance);
        var circle = LumenCircles.flight(direction, getDeltaMovement().length());
        for (double offset = 3.5D - circleDistance; offset <= distance; offset += 3.5D) {
            Vec3 center = start.add(direction.scale(offset - 0.55D)).add(0.0D, getBbHeight() * 0.5D, 0.0D);
            level().addParticle(circle, center.x, center.y, center.z, 0.0D, 0.0D, 0.0D);
        }
        circleDistance = (circleDistance + distance) % 3.5D;
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return entity instanceof LivingEntity living && LumenCombat.canDamage(getOwner(), living)
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
        level.sendParticles(LumenCircles.MOON_IMPACT, center.x, center.y + 0.03D, center.z,
                1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z,
                1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(ParticleTypes.END_ROD, center.x, center.y, center.z,
                80, 0.35D, 0.35D, 0.35D, 0.25D);
        for (ServerPlayer player : level.players()) {
            double distance = player.position().distanceTo(center);
            if (distance < SHAKE_RADIUS) {
                float intensity = SHAKE_INTENSITY * (float) (1.0D - distance / SHAKE_RADIUS);
                ModNetworking.sendToPlayer(new CameraShakePacket(SHAKE_DURATION_TICKS, intensity), player);
            }
        }
        level.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE,
                SoundSource.PLAYERS, 1.0F, 1.2F);
        LumenCombat.hurtInRadius(level, center, getOwner(), directTarget, this::damageForTarget,
                blastRadius, this::damageTarget);
        if (starcrown) {
            releaseStars(level, center, directTarget);
        }
        discard();
    }

    private void releaseStars(ServerLevel level, Vec3 center, Entity directTarget) {
        double rotation = random.nextDouble() * Math.PI * 2.0D;
        for (int i = 0; i < 6; i++) {
            double angle = rotation + i * Math.PI / 3.0D;
            LumenStar star = new LumenStar(ModEntityTypes.LUMEN_STAR.get(), level);
            star.setOwner(getOwner());
            star.setPos(center.x, center.y + 0.05D, center.z);
            star.configureShot(starDamage, starRadius, getBaseDamage(), directTarget);
            star.setDeltaMovement(Math.cos(angle) * 0.19D, 0.5D, Math.sin(angle) * 0.19D);
            level.addFreshEntity(star);
        }
    }

    private double damageForTarget(LivingEntity target) {
        double healthRatio = target.getMobType() == MobType.UNDEAD ? 0.12D : 0.05D;
        return (shotDamage + target.getMaxHealth() * healthRatio * shotPower)
                * getBaseDamage() / BASE_ARROW_DAMAGE;
    }

    private void damageTarget(LivingEntity target, float amount) {
        Entity owner = getOwner();
        boolean hurt = target.hurt(damageSources().arrow(this, owner), amount * 0.3F);
        hurt |= LumenCombat.hurtIgnoringCooldown(target, damageSources().explosion(this, owner), amount * 0.3F);
        hurt |= LumenCombat.hurtIgnoringCooldown(target, damageSources().indirectMagic(this, owner), amount * 0.4F);
        if (hurt && target.isAlive()) {
            LumenMoonfrost.apply(target, moonfrostLevel);
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, STATUS_DURATION_TICKS, 0), owner);
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, STATUS_DURATION_TICKS, 0), owner);
        }
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
        tag.putFloat("LumenPower", shotPower);
        tag.putInt("MoonfrostLevel", moonfrostLevel);
        tag.putBoolean("Starcrown", starcrown);
        tag.putDouble("LumenStarDamage", starDamage);
        tag.putDouble("LumenStarRadius", starRadius);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        shotDamage = tag.getDouble("LumenDamage");
        blastRadius = Math.max(0.1D, tag.getDouble("LumenRadius"));
        age = tag.getInt("LumenAge");
        shotPower = tag.contains("LumenPower") ? Mth.clamp(tag.getFloat("LumenPower"), 0.0F, 1.0F) : 1.0F;
        moonfrostLevel = Mth.clamp(tag.getInt("MoonfrostLevel"), 0, 3);
        starcrown = tag.getBoolean("Starcrown");
        starDamage = tag.getDouble("LumenStarDamage");
        starRadius = Math.max(0.1D, tag.getDouble("LumenStarRadius"));
        setNoGravity(true);
        pickup = Pickup.DISALLOWED;
    }
}
