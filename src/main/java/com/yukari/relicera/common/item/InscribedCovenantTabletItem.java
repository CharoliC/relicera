package com.yukari.relicera.common.item;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.curio.CovenantTabletEffects;
import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.common.network.packet.CovenantToastPacket;
import com.yukari.relicera.registry.ModItems;
import com.yukari.relicera.registry.ModEffects;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ReliceraMod.MOD_ID)
public final class InscribedCovenantTabletItem extends Item {
    private static final String CONTRACT_ENTITY_TAG = "ReliceraContractEntity";
    private static final List<EntityType<?>> CONTRACT_ENTITIES = List.of(
            EntityType.IRON_GOLEM, EntityType.SILVERFISH, EntityType.VILLAGER,
            EntityType.PILLAGER, EntityType.ENDER_DRAGON, EntityType.ZOMBIE,
            EntityType.CREEPER, EntityType.BLAZE, EntityType.HUSK,
            EntityType.SKELETON, EntityType.WANDERING_TRADER, EntityType.PIGLIN, EntityType.WITCH);

    public InscribedCovenantTabletItem(Properties properties) {
        super(properties);
    }

    public static boolean supports(EntityType<?> entityType) {
        return entityType != null && entityType != EntityType.PLAYER && entityType != EntityType.ARMOR_STAND
                && DefaultAttributes.hasSupplier(entityType);
    }

    public static boolean hasSpecificContract(EntityType<?> entityType) {
        return CONTRACT_ENTITIES.contains(entityType);
    }

    public static ItemStack create(EntityType<?> entityType) {
        if (!supports(entityType)) {
            return ItemStack.EMPTY;
        }
        ItemStack tablet = new ItemStack(ModItems.INSCRIBED_COVENANT_TABLET.get());
        tablet.getOrCreateTag().putString(CONTRACT_ENTITY_TAG,
                ForgeRegistries.ENTITY_TYPES.getKey(entityType).toString());
        return tablet;
    }

    public static EntityType<?> getContractEntity(ItemStack stack) {
        if (!stack.is(ModItems.INSCRIBED_COVENANT_TABLET.get())) {
            return null;
        }
        CompoundTag tag = stack.getTag();
        ResourceLocation id = tag == null ? null : ResourceLocation.tryParse(tag.getString(CONTRACT_ENTITY_TAG));
        EntityType<?> entityType = id == null ? null : ForgeRegistries.ENTITY_TYPES.getValue(id);
        return entityType != null && supports(entityType) ? entityType : null;
    }

    public static float getModelIndex(ItemStack stack) {
        EntityType<?> entityType = getContractEntity(stack);
        if (entityType == null) {
            return 0.0F;
        }
        int index = CONTRACT_ENTITIES.indexOf(entityType);
        return index < 0 ? CONTRACT_ENTITIES.size() + 1.0F : index + 1.0F;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        sign(event, event.getTarget());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        sign(event, event.getTarget());
    }

    private static void sign(PlayerInteractEvent event, Entity target) {
        ItemStack blank = event.getEntity().getItemInHand(event.getHand());
        if (!CovenantTabletEffects.isBlank(blank)
                || event.getEntity().hasEffect(ModEffects.COVENANT_BREAKER.get())) {
            return;
        }
        Entity resolvedTarget = target instanceof EnderDragonPart part ? part.parentMob : target;
        if (!(resolvedTarget instanceof LivingEntity) || resolvedTarget instanceof Player
                || resolvedTarget instanceof ArmorStand) {
            return;
        }
        EntityType<?> entityType = resolvedTarget.getType();
        if (!supports(entityType)) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
        if (!event.getLevel().isClientSide()) {
            event.getEntity().setItemInHand(event.getHand(), create(entityType));
            if (event.getEntity() instanceof ServerPlayer player) {
                ModNetworking.sendToPlayer(new CovenantToastPacket(
                        CovenantToastPacket.Kind.ESTABLISHED,
                        ForgeRegistries.ENTITY_TYPES.getKey(entityType)), player);
            }
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        EntityType<?> entityType = getContractEntity(stack);
        if (entityType == null) {
            return super.getName(stack);
        }
        return Component.translatable("item.relicera.inscribed_covenant_tablet.named",
                entityType.getDescription());
    }
}
