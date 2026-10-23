package com.yukari.relicera.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yukari.relicera.registry.ModItems;
import com.yukari.relicera.registry.ModLootModifiers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public final class AddAlchemicalResidueLootModifier extends LootModifier {
    public static final Codec<AddAlchemicalResidueLootModifier> CODEC = RecordCodecBuilder.create(instance ->
            codecStart(instance).apply(instance, AddAlchemicalResidueLootModifier::new));

    private static final ResourceLocation WITCH_LOOT_TABLE = ResourceLocation.withDefaultNamespace("entities/witch");
    private static final LootItemFunction LOOTING_BONUS =
            LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)).build();

    public AddAlchemicalResidueLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (WITCH_LOOT_TABLE.equals(context.getQueriedLootTableId()) && context.getRandom().nextFloat() < 0.12F) {
            ItemStack residue = new ItemStack(ModItems.ALCHEMICAL_RESIDUE.get(), 1 + context.getRandom().nextInt(2));
            generatedLoot.add(LOOTING_BONUS.apply(residue, context));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return ModLootModifiers.ADD_ALCHEMICAL_RESIDUE.get();
    }
}
