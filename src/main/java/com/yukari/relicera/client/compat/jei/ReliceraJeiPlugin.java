package com.yukari.relicera.client.compat.jei;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.client.screen.RelicRepairTableScreen;
import com.yukari.relicera.common.item.StonewallGreatshieldItem;
import com.yukari.relicera.common.recipe.TemplatelessSmithingRecipe;
import com.yukari.relicera.config.ModClientConfig;
import com.yukari.relicera.registry.ModBlocks;
import com.yukari.relicera.registry.ModItems;
import com.yukari.relicera.registry.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.stream.IntStream;
import java.util.List;

@JeiPlugin
public final class ReliceraJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID = ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "item_info");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new ForgelingSmithingRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
        if (showRelicRepairRecipes()) {
            registration.addRecipeCategories(
                    new RelicRepairRecipeCategory(registration.getJeiHelpers().getGuiHelper())
            );
        }
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getSmithingCategory().addExtension(
                TemplatelessSmithingRecipe.class, new TemplatelessSmithingCategoryExtension());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level != null) {
            if (showRelicRepairRecipes()) {
                registration.addRecipes(
                        RelicRepairRecipeCategory.TYPE,
                        Minecraft.getInstance().level.getRecipeManager()
                                .getAllRecipesFor(ModRecipeTypes.RELIC_REPAIR.get())
                );
            }
            registration.addRecipes(
                    ForgelingSmithingRecipeCategory.TYPE,
                    Minecraft.getInstance().level.getRecipeManager()
                            .getAllRecipesFor(ModRecipeTypes.FORGELING_SMITHING.get())
            );
        }

        addInfo(registration, ModItems.FEYSILVER_INGOT.get(), "feysilver_ingot", 1);
        addInfo(registration, ModItems.FORGOTTEN_THREAD.get(), "forgotten_thread", 2);
        addInfo(registration, ModItems.ROTTEN_TUSK.get(), "rotten_tusk", 2);
        addInfo(registration, ModItems.EPHEMERAL_BLOOM.get(), "ephemeral_bloom", 2);
        addInfo(registration, ModItems.WARFIRE_FRAGMENT.get(), "warfire_fragment", 1);
        addInfo(registration, ModItems.STORMSCALE.get(), "stormscale", 1);
        addInfo(registration, ModItems.RIPPLEHEART_PEARL.get(), "rippleheart_pearl", 2);
        addInfo(registration, ModItems.ASTRAL_LENS.get(), "astral_lens", 2);
        addInfo(registration, ModItems.ASTRAL_STORYBOOK.get(), "astral_storybook", 1);
        addInfo(registration, ModItems.EXTINGUISHED_SOLAR_FURNACE.get(), "extinguished_solar_furnace", 3);
        addInfo(registration, ModItems.WITHERED_LIFE_CHALICE.get(), "withered_life_chalice", 3);
        addInfo(registration, ModItems.DRIED_CROWN.get(), "dried_crown", 3);
        addInfo(registration, ModItems.VINDICATORS_MEDAL.get(), "vindicators_medal", 2);
        addInfo(registration, ModItems.FEYSILVER_FORGING_ART_VOLUME_ONE.get(), "feysilver_forging_art_volume_one", 1);
        addInfo(registration, ModItems.MASTER_SMITHS_BROOCH.get(), "master_smiths_brooch", 1);
        addInfo(registration, ModItems.LITTLE_TAILORS_BELT.get(), "little_tailors_belt", 1);
        addInfo(registration, ModItems.DEVILS_BEARSKIN.get(), "devils_bearskin", 1);
        addInfo(registration, ModItems.STOPPED_SILVER_CHRONOMETER.get(), "stopped_silver_chronometer", 1);
        addInfo(registration, ModItems.HEART_OF_THE_MOUNTAIN.get(), "heart_of_the_mountain", 2);
        addInfo(registration, ModItems.WEATHERED_MOUNTAIN_COVENANT.get(), "weathered_mountain_covenant", 2);
        addInfo(registration, ModItems.BRUTE_BELT.get(), "brute_belt", 2);
        registerStonewallGreatshieldRepair(registration);
        registerDepthbreakerRepair(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(Items.SMITHING_TABLE, ForgelingSmithingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(ModItems.FORGELING_BUCKET.get(), ForgelingSmithingRecipeCategory.TYPE);
        if (showRelicRepairRecipes()) {
            registration.addRecipeCatalyst(ModBlocks.RELIC_REPAIR_TABLE.get(), RelicRepairRecipeCategory.TYPE);
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        if (showRelicRepairRecipes()) {
            registration.addRecipeClickArea(
                    RelicRepairTableScreen.class,
                    82,
                    32,
                    13,
                    14,
                    RelicRepairRecipeCategory.TYPE
            );
        }
    }

    private static void addInfo(IRecipeRegistration registration, Item item, String itemName, int lineCount) {
        Component[] lines = IntStream.rangeClosed(1, lineCount)
                .mapToObj(line -> Component.translatable("jei.relicera.info." + itemName + ".line_" + line))
                .toArray(Component[]::new);
        registration.addItemStackInfo(new ItemStack(item), lines);
    }

    private static void registerStonewallGreatshieldRepair(IRecipeRegistration registration) {
        ItemStack damagedShield = new ItemStack(ModItems.STONEWALL_GREATSHIELD.get());
        damagedShield.setDamageValue(damagedShield.getMaxDamage() - 1);

        ItemStack repairedShield = StonewallGreatshieldItem.createRepairedCopy(damagedShield, 1);

        var recipe = registration.getVanillaRecipeFactory().createAnvilRecipe(
                damagedShield,
                List.of(new ItemStack(Items.EMERALD_BLOCK)),
                List.of(repairedShield),
                ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "anvil.stonewall_greatshield_repair")
        );
        registration.addRecipes(RecipeTypes.ANVIL, List.of(recipe));
    }

    private static void registerDepthbreakerRepair(IRecipeRegistration registration) {
        if (!ModItems.DEPTHBREAKER.isPresent()) {
            return;
        }
        ItemStack damagedPickaxe = new ItemStack(ModItems.DEPTHBREAKER.get());
        damagedPickaxe.setDamageValue(damagedPickaxe.getMaxDamage() - 1);
        ItemStack repairedPickaxe = damagedPickaxe.copy();
        repairedPickaxe.setDamageValue(Math.max(0,
                repairedPickaxe.getDamageValue() - repairedPickaxe.getMaxDamage() / 4));

        var recipe = registration.getVanillaRecipeFactory().createAnvilRecipe(
                damagedPickaxe,
                List.of(new ItemStack(Items.EMERALD)),
                List.of(repairedPickaxe),
                ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "anvil.depthbreaker_repair")
        );
        registration.addRecipes(RecipeTypes.ANVIL, List.of(recipe));
    }

    private static boolean showRelicRepairRecipes() {
        return ModClientConfig.JEI_SHOW_RELIC_REPAIR_RECIPES.get();
    }
}
