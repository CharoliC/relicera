package com.yukari.relicera.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public final class AddSculkFruitLootModifier extends LootModifier {
    public static final Codec<AddSculkFruitLootModifier> CODEC = RecordCodecBuilder.create(instance ->
            codecStart(instance).apply(instance, AddSculkFruitLootModifier::new));

    private static final ResourceLocation ANCIENT_CITY = ResourceLocation.withDefaultNamespace("chests/ancient_city");
    private static final ResourceLocation ANCIENT_CITY_ICE_BOX =
            ResourceLocation.withDefaultNamespace("chests/ancient_city_ice_box");

    public AddSculkFruitLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ResourceLocation table = context.getQueriedLootTableId();
        if (ANCIENT_CITY.equals(table)) {
            if (context.getRandom().nextFloat() < 0.063F) {
                generatedLoot.add(new ItemStack(ModItems.SCULK_FRUIT_SEEDS.get(), 1 + context.getRandom().nextInt(2)));
            }
        } else if (ANCIENT_CITY_ICE_BOX.equals(table)) {
            if (context.getRandom().nextFloat() < 0.46F) {
                generatedLoot.add(new ItemStack(ModItems.SCULK_FRUIT_SEEDS.get(), 1 + context.getRandom().nextInt(4)));
            }
            if (context.getRandom().nextFloat() < 0.42F) {
                generatedLoot.add(new ItemStack(ModItems.SCULK_FRUIT.get()));
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return ModLootModifiers.ADD_SCULK_FRUIT.get();
    }
}
