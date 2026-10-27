package com.yukari.relicera.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yukari.relicera.config.ModCommonConfig;
import com.yukari.relicera.registry.ModItems;
import com.yukari.relicera.registry.ModLootModifiers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class AddSirensEyeLootModifier extends LootModifier {
    public static final Codec<AddSirensEyeLootModifier> CODEC = RecordCodecBuilder.create(instance ->
            codecStart(instance).apply(instance, AddSirensEyeLootModifier::new));
    private static final Set<ResourceLocation> CHESTS = Set.of(
            ResourceLocation.withDefaultNamespace("chests/shipwreck_supply"),
            ResourceLocation.withDefaultNamespace("chests/buried_treasure"),
            ResourceLocation.withDefaultNamespace("chests/underwater_ruin_big"),
            ResourceLocation.withDefaultNamespace("chests/underwater_ruin_small"));

    public AddSirensEyeLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (CHESTS.contains(context.getQueriedLootTableId())
                && context.getRandom().nextDouble() < ModCommonConfig.SIRENS_EYE_CHEST_CHANCE.get()) {
            generatedLoot.add(new ItemStack(ModItems.SIRENS_EYE.get()));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return ModLootModifiers.ADD_SIRENS_EYE.get();
    }
}
