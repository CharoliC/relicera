package com.yukari.relicera.common.curio;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.effect.covenantbreaker.CovenantBreakerEffects;
import com.yukari.relicera.common.item.BaromsCovenantStoneItem;
import com.yukari.relicera.common.item.InscribedCovenantTabletItem;
import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.common.network.packet.CovenantToastPacket;
import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.registry.ModItems;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = ReliceraMod.MOD_ID)
public final class BaromsCovenantStoneEffects {
    private static final TagKey<Item> LABOR_TOOLS = itemTag("barom_labor_tools");
    private static final TagKey<Item> EXCLUDED_TOOLS = itemTag("barom_labor_tool_exclusions");
    private static final List<ToolAction> LABOR_ACTIONS = List.of(
            ToolActions.AXE_DIG, ToolActions.PICKAXE_DIG, ToolActions.SHOVEL_DIG, ToolActions.HOE_DIG,
            ToolActions.AXE_STRIP, ToolActions.AXE_SCRAPE, ToolActions.AXE_WAX_OFF,
            ToolActions.SHOVEL_FLATTEN, ToolActions.HOE_TILL, ToolActions.SHEARS_DIG,
            ToolActions.SHEARS_HARVEST, ToolActions.SHEARS_CARVE, ToolActions.SHEARS_DISARM,
            ToolActions.FISHING_ROD_CAST);

    private BaromsCovenantStoneEffects() {
    }

    private static TagKey<Item> itemTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, name));
    }

    public static boolean hasContract(Entity entity, EntityType<?> entityType) {
        if (!(entity instanceof LivingEntity living) || living.isSpectator()) {
            return false;
        }
        return CuriosApi.getCuriosInventory(living).resolve()
                .map(handler -> handler.findCurios(ModItems.BAROMS_COVENANT_STONE.get()).stream()
                        .anyMatch(result -> !result.slotContext().cosmetic()
                                && BaromsCovenantStoneItem.RELIC_SLOT.equals(result.slotContext().identifier())
                                && BaromsCovenantStoneItem.hasContract(result.stack(), entityType)))
                .orElse(false);
    }

    public static boolean preventsKnockback(Entity entity) {
        return hasContract(entity, EntityType.IRON_GOLEM);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onKnockback(LivingKnockBackEvent event) {
        if (preventsKnockback(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttack(LivingAttackEvent event) {
        LivingEntity wearer = event.getEntity();
        if (event.getSource().getEntity() instanceof Mob mob
                && isNeutralContractMob(mob, wearer)) {
            event.setCanceled(true);
            return;
        }
        if (event.getSource().getEntity() instanceof LivingEntity attacker && attacker != wearer
                && hasContract(wearer, EntityType.SILVERFISH)
                && volume(attacker.getBoundingBox()) < volume(wearer.getBoundingBox())
                        * ModCommonConfig.BAROM_VOLUME_MULTIPLIER.get()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Mob mob && !mob.level().isClientSide()
                && event.getNewTarget() != null
                && isNeutralContractMob(mob, event.getNewTarget())) {
            event.setNewTarget(null);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || mob.level().isClientSide()) {
            return;
        }
        if (mob.getTarget() != null && isNeutralContractMob(mob, mob.getTarget())) {
            mob.setTarget(null);
        }
        if (mob.getBrain().checkMemory(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)) {
            mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET)
                    .filter(target -> isNeutralContractMob(mob, target))
                    .ifPresent(target -> {
                        mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                        mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                    });
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.isCanceled() || event.getEntity().level().isClientSide()
                || !(event.getSource().getEntity() instanceof LivingEntity killer)) {
            return;
        }
        EntityType<?> entityType = event.getEntity().getType();
        if (!hasContract(killer, entityType)) {
            return;
        }
        breakContractOnKill(killer, entityType);
    }

    static void breakContractOnKill(LivingEntity killer, EntityType<?> entityType) {
        boolean removed = CuriosApi.getCuriosInventory(killer).resolve()
                .map(handler -> handler.findCurios(ModItems.BAROMS_COVENANT_STONE.get()).stream()
                        .filter(result -> !result.slotContext().cosmetic()
                                && BaromsCovenantStoneItem.RELIC_SLOT.equals(result.slotContext().identifier()))
                        .map(result -> BaromsCovenantStoneItem.removeContract(result.stack(), entityType))
                        .reduce(false, Boolean::logicalOr))
                .orElse(false);
        if (removed && killer instanceof ServerPlayer player) {
            CovenantBreakerEffects.apply(player);
            ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
            if (entityId != null) {
                ModNetworking.sendToPlayer(new CovenantToastPacket(CovenantToastPacket.Kind.BROKEN, entityId), player);
            }
        }
    }

    private static boolean isNeutralContractMob(Mob mob, LivingEntity wearer) {
        EntityType<?> entityType = mob.getType();
        return (!InscribedCovenantTabletItem.hasSpecificContract(entityType)
                        || entityType == EntityType.IRON_GOLEM || entityType == EntityType.SILVERFISH
                        || entityType == EntityType.PILLAGER || entityType == EntityType.ZOMBIE
                        || entityType == EntityType.CREEPER || entityType == EntityType.BLAZE
                        || entityType == EntityType.HUSK || entityType == EntityType.SKELETON
                        || entityType == EntityType.PIGLIN || entityType == EntityType.WITCH)
                && mob.getLastHurtByMob() != wearer
                && hasContract(wearer, entityType);
    }

    private static double volume(AABB box) {
        return box.getXsize() * box.getYsize() * box.getZsize();
    }

    public static boolean protectsTool(LivingEntity wearer, ItemStack stack) {
        return stack.isDamageableItem() && !stack.is(EXCLUDED_TOOLS)
                && (stack.is(LABOR_TOOLS) || LABOR_ACTIONS.stream().anyMatch(stack::canPerformAction))
                && hasContract(wearer, EntityType.VILLAGER);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!hasContract(event.getEntity(), EntityType.VILLAGER)) {
            return;
        }
        float multiplier = 1.0F;
        if (event.getEntity().isEyeInFluidType(ForgeMod.WATER_TYPE.get())
                && !EnchantmentHelper.hasAquaAffinity(event.getEntity())) {
            multiplier *= 5.0F;
        }
        if (!event.getEntity().onGround()) {
            multiplier *= 5.0F;
        }
        event.setNewSpeed(event.getNewSpeed() * multiplier);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffect(MobEffectEvent.Applicable event) {
        if (event.getEffectInstance().getEffect() == MobEffects.DIG_SLOWDOWN
                && hasContract(event.getEntity(), EntityType.VILLAGER)) {
            event.setResult(Event.Result.DENY);
        }
    }
}
