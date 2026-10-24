package com.yukari.relicera.common.curio;

import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.registry.ModItems;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import top.theillusivec4.curios.api.CuriosApi;

public final class NereiasCrownEffects {
    private static final int CONDUIT_POWER_DURATION_TICKS = 260;
    private static final int AQUATIC_AURA_UPDATE_INTERVAL = 40;
    private static final int AQUATIC_ALLY_UPDATE_INTERVAL = 20;
    private static final int WEARER_TARGET_MEMORY_TICKS = 10 * 20;
    private static final double SWIM_SPEED_BONUS = 3.20D;
    private static final double AQUATIC_ALLY_FOLLOW_STOP_DISTANCE_SQR = 4.0D;
    private static final double AQUATIC_ALLY_NAVIGATION_SPEED = 1.15D;
    private static final UUID SWIM_SPEED_MODIFIER_ID = UUID.fromString("46583b42-f21e-4bd2-918b-ae6caa0eee0c");
    private static final UUID AQUATIC_ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("7e78f32f-9de1-420a-bc0f-f2cf60b437b9");
    private static final UUID AQUATIC_ARMOR_MODIFIER_ID = UUID.fromString("a9d0237c-b5d7-4d05-b756-a783228b14db");
    private static final String SWIM_SPEED_MODIFIER_NAME = "Relicera Nereia's Crown swim speed";
    private static final String AQUATIC_ATTACK_DAMAGE_MODIFIER_NAME = "Relicera Nereia's Crown aquatic attack damage";
    private static final String AQUATIC_ARMOR_MODIFIER_NAME = "Relicera Nereia's Crown aquatic armor";
    private static final Map<UUID, WearerTargetMemory> RECENT_WEARER_TARGETS = new HashMap<>();

    private NereiasCrownEffects() {
    }

    public static boolean isEquipped(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .resolve()
                .map(handler -> handler.isEquipped(ModItems.NEREIAS_CROWN.get()))
                .orElse(false);
    }

    public static void tick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }

        if (entity instanceof Mob mob && isAquaticAlly(mob)) {
            tickAquaticAlly(mob);
        }

        boolean equipped = isEquipped(entity);
        applyAttribute(entity, ForgeMod.SWIM_SPEED.get(), SWIM_SPEED_MODIFIER_ID, SWIM_SPEED_MODIFIER_NAME,
                equipped ? SWIM_SPEED_BONUS : 0.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        tickAquaticAura(entity, equipped);

        boolean active = equipped && isOceanGuardActive(entity);
        if (active && entity.isInWaterOrBubble()) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, CONDUIT_POWER_DURATION_TICKS, 0, false, true, true));
        }
    }

    public static boolean preventDamage(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide() && isEquipped(entity) && isPreventedDamage(event.getSource())) {
            event.setCanceled(true);
            return true;
        }
        return false;
    }

    public static boolean preventDamage(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide() && isEquipped(entity) && isPreventedDamage(event.getSource())) {
            event.setCanceled(true);
            return true;
        }
        return false;
    }

    public static boolean preventAquaticAllyDamage(LivingAttackEvent event) {
        if (isAquaticAllyDamage(event.getSource(), event.getEntity())) {
            event.setCanceled(true);
            return true;
        }
        return false;
    }

    public static boolean preventAquaticAllyDamage(LivingHurtEvent event) {
        if (isAquaticAllyDamage(event.getSource(), event.getEntity())) {
            event.setCanceled(true);
            return true;
        }
        return false;
    }

    public static void rememberWearerTarget(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker) || !isEquipped(attacker)) {
            return;
        }

        LivingEntity target = event.getEntity();
        if (target == attacker || !target.isAlive() || isAquaticAlly(target)) {
            return;
        }

        long expireTime = attacker.level().getGameTime() + WEARER_TARGET_MEMORY_TICKS;
        RECENT_WEARER_TARGETS.put(attacker.getUUID(), new WearerTargetMemory(target.getUUID(), expireTime));
    }

    public static void redirectAquaticAllyTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Mob ally) || !isAquaticAlly(ally) || ally.level().isClientSide()) {
            return;
        }

        LivingEntity newTarget = event.getNewTarget();
        if (newTarget != null && isEquipped(newTarget)) {
            event.setNewTarget(null);
            return;
        }

        if (!isGuardianAlly(ally)) {
            return;
        }

        double range = ModCommonConfig.NEREIAS_CROWN_AQUATIC_AURA_RANGE.get();
        if (range <= 0.0D || !(ally.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        LivingEntity wearer = findNearestValidWearer(ally, range);
        if (wearer == null) {
            return;
        }

        LivingEntity commandedTarget = findRememberedTarget(serverLevel, wearer, ally, range);
        if (commandedTarget == null) {
            event.setNewTarget(null);
        } else if (newTarget != commandedTarget) {
            event.setNewTarget(commandedTarget);
        }
    }

    public static void takeDrownedHeldItems(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Drowned drowned) || !isEquipped(event.getEntity())) {
            return;
        }

        ItemStack mainHand = drowned.getMainHandItem();
        ItemStack offhand = drowned.getOffhandItem();
        if (mainHand.isEmpty() && offhand.isEmpty()) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
        if (event.getLevel().isClientSide()) {
            return;
        }

        dropAndClearHeldItem(drowned, EquipmentSlot.MAINHAND, mainHand);
        dropAndClearHeldItem(drowned, EquipmentSlot.OFFHAND, offhand);
        drowned.level().playSound(null, drowned.getX(), drowned.getY(), drowned.getZ(),
                SoundEvents.ITEM_PICKUP, SoundSource.HOSTILE, 0.8F, 0.9F + drowned.getRandom().nextFloat() * 0.2F);
    }

    public static void reduceActiveDamage(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide() || !isEquipped(entity) || !isOceanGuardActive(entity)) {
            return;
        }

        double reduction = ModCommonConfig.NEREIAS_CROWN_ACTIVE_DAMAGE_REDUCTION.get();
        if (reduction > 0.0D) {
            event.setAmount((float) (event.getAmount() * Math.max(0.0D, 1.0D - reduction)));
        }
    }

    private static boolean isOceanGuardActive(LivingEntity entity) {
        if (entity.isInWaterOrBubble()) {
            return true;
        }

        Level level = entity.level();
        if (!level.isRaining()) {
            return false;
        }

        BlockPos pos = entity.blockPosition();
        return level.isRainingAt(pos) || level.isRainingAt(pos.above());
    }

    private static void tickAquaticAura(LivingEntity wearer, boolean equipped) {
        if (!equipped) {
            clearAquaticAura(wearer);
            return;
        }

        if (wearer.tickCount % AQUATIC_AURA_UPDATE_INTERVAL != 0) {
            return;
        }

        AquaticAuraBonus bonus = calculateAquaticAuraBonus(wearer);

        applyAttribute(wearer, Attributes.ATTACK_DAMAGE, AQUATIC_ATTACK_DAMAGE_MODIFIER_ID, AQUATIC_ATTACK_DAMAGE_MODIFIER_NAME,
                bonus.attackDamage(), AttributeModifier.Operation.ADDITION);
        applyAttribute(wearer, Attributes.ARMOR, AQUATIC_ARMOR_MODIFIER_ID, AQUATIC_ARMOR_MODIFIER_NAME,
                bonus.armor(), AttributeModifier.Operation.ADDITION);
    }

    public static double getAquaticAttackDamageBonus(LivingEntity wearer) {
        return calculateAquaticAuraBonus(wearer).attackDamage();
    }

    public static double getAquaticArmorBonus(LivingEntity wearer) {
        return calculateAquaticAuraBonus(wearer).armor();
    }

    private static AquaticAuraBonus calculateAquaticAuraBonus(LivingEntity wearer) {
        double range = ModCommonConfig.NEREIAS_CROWN_AQUATIC_AURA_RANGE.get();
        if (range <= 0.0D) {
            return AquaticAuraBonus.ZERO;
        }

        double rangeSqr = range * range;
        List<LivingEntity> nearby = wearer.level().getEntitiesOfClass(
                LivingEntity.class,
                wearer.getBoundingBox().inflate(range),
                entity -> entity != wearer && entity.isAlive() && isAquaticAuraSource(entity) && entity.distanceToSqr(wearer) <= rangeSqr
        );

        double attackDamageTotal = 0.0D;
        double maxHealthTotal = 0.0D;
        for (LivingEntity entity : nearby) {
            AttributeInstance attackDamage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamage != null) {
                attackDamageTotal += attackDamage.getValue();
            }
            maxHealthTotal += entity.getMaxHealth();
        }

        return new AquaticAuraBonus(
                attackDamageTotal * ModCommonConfig.NEREIAS_CROWN_AQUATIC_ATTACK_DAMAGE_SHARE.get(),
                maxHealthTotal * ModCommonConfig.NEREIAS_CROWN_AQUATIC_MAX_HEALTH_ARMOR_SHARE.get()
        );
    }

    private static void clearAquaticAura(LivingEntity entity) {
        removeAttribute(entity, Attributes.ATTACK_DAMAGE, AQUATIC_ATTACK_DAMAGE_MODIFIER_ID);
        removeAttribute(entity, Attributes.ARMOR, AQUATIC_ARMOR_MODIFIER_ID);
    }

    private static boolean isAquaticAuraSource(LivingEntity entity) {
        if (entity.getType() == EntityType.GUARDIAN
                || entity.getType() == EntityType.ELDER_GUARDIAN
                || entity.getType() == EntityType.DROWNED) {
            return true;
        }

        MobCategory category = entity.getType().getCategory();
        return category == MobCategory.WATER_CREATURE
                || category == MobCategory.WATER_AMBIENT
                || category == MobCategory.UNDERGROUND_WATER_CREATURE;
    }

    private static void tickAquaticAlly(Mob ally) {
        if (ally.tickCount % AQUATIC_ALLY_UPDATE_INTERVAL != 0 || !(ally.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        LivingEntity target = ally.getTarget();
        if (target != null && (!target.isAlive() || isEquipped(target))) {
            ally.setTarget(null);
            target = null;
        }

        if (!isGuardianAlly(ally)) {
            return;
        }

        double range = ModCommonConfig.NEREIAS_CROWN_AQUATIC_AURA_RANGE.get();
        if (range <= 0.0D) {
            return;
        }

        LivingEntity wearer = findNearestValidWearer(ally, range);
        if (wearer == null) {
            return;
        }

        LivingEntity commandedTarget = findRememberedTarget(serverLevel, wearer, ally, range);
        if (commandedTarget != null) {
            if (target != commandedTarget) {
                ally.setTarget(commandedTarget);
            }
            return;
        }

        if (target != null) {
            ally.setTarget(null);
            target = null;
        }

        if (target == null && ally.distanceToSqr(wearer) > AQUATIC_ALLY_FOLLOW_STOP_DISTANCE_SQR) {
            ally.getNavigation().moveTo(wearer, AQUATIC_ALLY_NAVIGATION_SPEED);
        }
    }

    private static LivingEntity findNearestValidWearer(Mob ally, double range) {
        double rangeSqr = range * range;
        List<LivingEntity> nearby = ally.level().getEntitiesOfClass(
                LivingEntity.class,
                ally.getBoundingBox().inflate(range),
                entity -> entity != ally
                        && entity.isAlive()
                        && entity.distanceToSqr(ally) <= rangeSqr
                        && canAllyRespondToWearer(ally, entity)
                        && isEquipped(entity)
        );

        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (LivingEntity entity : nearby) {
            double distance = entity.distanceToSqr(ally);
            if (distance < nearestDistance) {
                nearest = entity;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    private static LivingEntity findRememberedTarget(ServerLevel level, LivingEntity wearer, Mob ally, double range) {
        WearerTargetMemory memory = RECENT_WEARER_TARGETS.get(wearer.getUUID());
        if (memory == null) {
            return null;
        }

        if (memory.expireTime < level.getGameTime()) {
            RECENT_WEARER_TARGETS.remove(wearer.getUUID());
            return null;
        }

        Entity entity = level.getEntity(memory.targetId);
        if (!(entity instanceof LivingEntity target)
                || !target.isAlive()
                || target == wearer
                || target == ally
                || isEquipped(target)
                || isAquaticAlly(target)
                || target.distanceToSqr(ally) > range * range) {
            return null;
        }
        return target;
    }

    private static boolean canAllyRespondToWearer(Mob ally, LivingEntity wearer) {
        EntityType<?> type = ally.getType();
        if (type == EntityType.GUARDIAN || type == EntityType.ELDER_GUARDIAN) {
            return wearer.isInWaterOrBubble();
        }
        return true;
    }

    private static boolean isAquaticAlly(LivingEntity entity) {
        return entity.getType() == EntityType.GUARDIAN
                || entity.getType() == EntityType.ELDER_GUARDIAN
                || entity.getType() == EntityType.DROWNED;
    }

    private static boolean isGuardianAlly(LivingEntity entity) {
        return entity.getType() == EntityType.GUARDIAN || entity.getType() == EntityType.ELDER_GUARDIAN;
    }

    private static boolean isAquaticAllyDamage(DamageSource source, LivingEntity target) {
        if (target.level().isClientSide() || !isEquipped(target)) {
            return false;
        }

        Entity attacker = source.getEntity();
        Entity direct = source.getDirectEntity();
        return attacker instanceof LivingEntity livingAttacker && isAquaticAlly(livingAttacker)
                || direct instanceof LivingEntity directAttacker && isAquaticAlly(directAttacker);
    }

    private static void dropAndClearHeldItem(Drowned drowned, EquipmentSlot slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        ItemStack drop = stack.copy();
        drowned.setItemSlot(slot, ItemStack.EMPTY);
        drowned.spawnAtLocation(drop);
    }

    private static boolean isPreventedDamage(DamageSource source) {
        return source.is(DamageTypes.DROWN) || source.is(DamageTypeTags.IS_LIGHTNING);
    }

    private static void applyAttribute(LivingEntity entity, Attribute attribute, UUID id, String name, double amount,
                                       AttributeModifier.Operation operation) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return;
        }

        AttributeModifier existing = instance.getModifier(id);
        if (amount <= 0.0D) {
            if (existing != null) {
                instance.removeModifier(id);
            }
            return;
        }

        if (existing != null && Math.abs(existing.getAmount() - amount) < 0.0001D) {
            return;
        }

        if (existing != null) {
            instance.removeModifier(id);
        }
        instance.addTransientModifier(new AttributeModifier(id, name, amount, operation));
    }

    private static void removeAttribute(LivingEntity entity, Attribute attribute, UUID id) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null && instance.getModifier(id) != null) {
            instance.removeModifier(id);
        }
    }

    private record WearerTargetMemory(UUID targetId, long expireTime) {
    }

    private record AquaticAuraBonus(double attackDamage, double armor) {
        private static final AquaticAuraBonus ZERO = new AquaticAuraBonus(0.0D, 0.0D);
    }
}
