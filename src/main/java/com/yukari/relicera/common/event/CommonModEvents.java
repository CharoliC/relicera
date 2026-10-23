package com.yukari.relicera.common.event;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.entity.forgeling.Forgeling;
import com.yukari.relicera.registry.ModBlocks;
import com.yukari.relicera.registry.ModEntityTypes;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = ReliceraMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonModEvents {
    private CommonModEvents() {
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(
                    ModBlocks.EPHEMERAL_BLOOM.getId(), ModBlocks.POTTED_EPHEMERAL_BLOOM);
            ComposterBlock.COMPOSTABLES.put(ModItems.EPHEMERAL_BLOOM.get(), 0.99F);
            ComposterBlock.COMPOSTABLES.put(ModItems.SCULK_FRUIT_SEEDS.get(), 0.30F);
            for (Item bottle : new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION}) {
                ItemStack water = PotionUtils.setPotion(new ItemStack(bottle), Potions.WATER);
                BrewingRecipeRegistry.addRecipe(PartialNBTIngredient.of(bottle, water.getOrCreateTag()),
                        Ingredient.of(ModItems.ALCHEMICAL_RESIDUE.get()),
                        PotionUtils.setPotion(new ItemStack(bottle), Potions.AWKWARD));
            }
        });
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.FORGELING.get(), Forgeling.createAttributes().build());
    }
}
