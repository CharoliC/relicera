package com.yukari.relicera.client.event;

import com.mojang.datafixers.util.Either;
import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.astral.AstralObservationClientData;
import com.yukari.relicera.common.item.ItemTooltipText;
import com.yukari.relicera.common.item.ViewerTooltipItem;
import com.yukari.relicera.common.item.feysilver.FeysilverForgingClientData;
import com.yukari.relicera.common.item.feysilver.ForgingVolume;
import com.yukari.relicera.registry.ModItems;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringDecomposer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = ReliceraMod.MOD_ID, value = Dist.CLIENT)
public final class ClientTooltipEvents {
    private static final String LIBTOOLTIPS_SHIFT_UP_KEY = "tooltip.libtooltips.generic.shift_up";
    private static final String LIBTOOLTIPS_SHIFT_DOWN_KEY = "tooltip.libtooltips.generic.shift_down";
    private static final int LIBTOOLTIPS_MAX_LINES = 100;
    private static final int LIBTOOLTIPS_MAX_SPACES_BEFORE_TOOLTIP = 10;

    private ClientTooltipEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onViewerTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof ViewerTooltipItem item) {
            item.appendViewerTooltip(event.getItemStack(), event.getEntity(), event.getToolTip());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSpyglassTooltip(ItemTooltipEvent event) {
        if (!event.getItemStack().is(Items.SPYGLASS) || event.getEntity() == null) {
            return;
        }

        int observedCount = AstralObservationClientData.getObservedCount();
        if (observedCount <= 0 || AstralObservationClientData.hasClaimedAstralLens()) {
            return;
        }

        event.getToolTip().add(ItemTooltipText.description("spyglass", 0).copy()
                .append(Component.literal(" "))
                .append(Component.literal(observedCount + " / 8")));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onReliceraTooltipOrder(ItemTooltipEvent event) {
        if (isReliceraItem(event.getItemStack())) {
            removeLibTooltipsGeneratedLines(event);
            moveReliceraModNameToEnd(event.getToolTip());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        if (!isReliceraItem(event)) {
            return;
        }

        List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
        for (int index = elements.size() - 1; index >= 0; index--) {
            Either<FormattedText, TooltipComponent> element = elements.get(index);
            if (element.left().map(FormattedText::getString).filter(ClientTooltipEvents::isReliceraModNameLine).isPresent()) {
                elements.remove(index);
                elements.add(element);
            }
        }
        ItemStack stack = event.getItemStack();
        boolean unknownFeysilverMaterial = !FeysilverForgingClientData.hasLearned(ForgingVolume.TWO)
                && (stack.is(ModItems.SILVERING_SOLUTION.get()) || stack.is(ModItems.SILVERED_DEBRIS.get())
                || stack.is(ModItems.FEYSILVER_SCRAP.get()));
        if (stack.is(ModItems.PHILOSOPHERS_STONE.get()) || stack.is(ModItems.AULTIAS_BLOOD.get()) || unknownFeysilverMaterial) {
            elements.replaceAll(element -> element.mapLeft(ClientTooltipEvents::obfuscateMaterialText));
        }
        if (unknownFeysilverMaterial) {
            elements.add(Math.min(1, elements.size()), Either.left(ItemTooltipText.quote("feysilver_material", 0)));
        } else if (stack.is(ModItems.SILVERING_SOLUTION.get())) {
            elements.add(Math.min(1, elements.size()), Either.left(ItemTooltipText.flavor("silvering_solution", 0)));
        }
    }

    private static FormattedText obfuscateMaterialText(FormattedText text) {
        MutableComponent result = Component.empty();
        StringDecomposer.iterateFormatted(text, Style.EMPTY, (index, style, codePoint) -> {
            result.append(Component.literal(new String(Character.toChars(codePoint)))
                    .setStyle(style.withObfuscated(true)));
            return true;
        });
        return result;
    }

    private static void moveReliceraModNameToEnd(List<Component> tooltip) {
        for (int index = tooltip.size() - 1; index >= 0; index--) {
            Component line = tooltip.get(index);
            if (isReliceraModNameLine(line.getString())) {
                tooltip.remove(index);
                tooltip.add(line);
            }
        }
    }

    private static void removeLibTooltipsGeneratedLines(ItemTooltipEvent event) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
        if (itemId == null) {
            return;
        }

        Set<String> generatedTexts = getLibTooltipsGeneratedLineTexts(itemId);
        List<Component> tooltip = event.getToolTip();
        for (int index = tooltip.size() - 1; index > 0; index--) {
            Component line = tooltip.get(index);
            if (isLibTooltipsGenericShiftLine(line) || isLibTooltipsGeneratedLiteralLine(line, generatedTexts)) {
                tooltip.remove(index);
            }
        }
    }

    private static Set<String> getLibTooltipsGeneratedLineTexts(ResourceLocation itemId) {
        Set<String> generatedTexts = new HashSet<>();
        String keyPrefix = "tooltip." + itemId.getNamespace() + "." + itemId.getPath() + ".";
        for (int line = 0; line < LIBTOOLTIPS_MAX_LINES; line++) {
            String key = keyPrefix + line;
            if (!I18n.exists(key)) {
                continue;
            }

            String text = I18n.get(key);
            if ("hide".equals(text)) {
                break;
            }

            addWithPossibleLibTooltipsIndent(generatedTexts, text);
        }
        return generatedTexts;
    }

    private static void addWithPossibleLibTooltipsIndent(Set<String> generatedTexts, String text) {
        for (int spaces = 0; spaces <= LIBTOOLTIPS_MAX_SPACES_BEFORE_TOOLTIP; spaces++) {
            generatedTexts.add(" ".repeat(spaces) + text);
        }
    }

    private static boolean isLibTooltipsGenericShiftLine(Component line) {
        if (line.getContents() instanceof TranslatableContents translatable) {
            String key = translatable.getKey();
            return LIBTOOLTIPS_SHIFT_UP_KEY.equals(key) || LIBTOOLTIPS_SHIFT_DOWN_KEY.equals(key);
        }
        return false;
    }

    private static boolean isLibTooltipsGeneratedLiteralLine(Component line, Set<String> generatedTexts) {
        return !generatedTexts.isEmpty()
                && line.getContents() instanceof LiteralContents
                && line.getSiblings().isEmpty()
                && generatedTexts.contains(line.getString());
    }

    private static boolean isReliceraModNameLine(String text) {
        return ReliceraMod.MOD_ID.equals(text)
                || "Relicera".equals(text);
    }

    private static boolean isReliceraItem(RenderTooltipEvent.GatherComponents event) {
        return isReliceraItem(event.getItemStack());
    }

    private static boolean isReliceraItem(net.minecraft.world.item.ItemStack stack) {
        return stack.getItem() != Items.AIR
                && ForgeRegistries.ITEMS.getKey(stack.getItem()) != null
                && ReliceraMod.MOD_ID.equals(ForgeRegistries.ITEMS.getKey(stack.getItem()).getNamespace());
    }
}
