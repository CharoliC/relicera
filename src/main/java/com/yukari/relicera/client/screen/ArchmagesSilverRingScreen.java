package com.yukari.relicera.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.menu.ArchmagesSilverRingMenu;
import com.yukari.relicera.common.compat.irons_spellbooks.ArchmagesSilverRingCompat;
import net.minecraft.client.gui.components.Button;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public final class ArchmagesSilverRingScreen extends AbstractContainerScreen<ArchmagesSilverRingMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ReliceraMod.MOD_ID, "textures/gui/iss/archmages_silver_ring.png");
    private List<SpellData> spells = List.of();
    private Button inscribedToggle;

    public ArchmagesSilverRingScreen(ArchmagesSilverRingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 191;
        imageHeight = 190;
    }

    @Override
    protected void init() {
        super.init();
        inscribedToggle = addRenderableWidget(new Button(leftPos + 25, topPos + 61, 18, 10,
                toggleDescription(), button -> {
                    if (minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, ArchmagesSilverRingMenu.TOGGLE_INSCRIBED_BUTTON);
                    }
                }, narration -> narration.get()) {
            @Override
            public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                setMessage(toggleDescription());
                RenderSystem.enableBlend();
                graphics.blit(TEXTURE, getX(), getY(), isHovered() ? 18 : 0,
                        menu.excludesInscribedSpells() ? 226 : 216, 18, 10);
                RenderSystem.disableBlend();
            }
        });
    }

    private Component toggleDescription() {
        return Component.translatable("gui.relicera.archmages_silver_ring." + (menu.excludesInscribedSpells() ? 1 : 0));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        spells = ArchmagesSilverRingCompat.getSelectedSpells(menu.getSpellbook(), menu.excludesInscribedSpells());
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (inscribedToggle.isHovered()) {
            graphics.renderTooltip(font, toggleDescription(), mouseX, mouseY);
        }
        for (int index = 0; index < spells.size(); index++) {
            if (isHovering(53 + 32 * index, 60, 16, 16, mouseX, mouseY)) {
                graphics.renderTooltip(font, ArchmagesSilverRingCompat.getSpellName(spells.get(index), minecraft.player), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        RenderSystem.enableBlend();
        if (!menu.getSpellbook().isEmpty()) {
            graphics.blit(TEXTURE, leftPos + 54, topPos + 44, 52, 190, 110, 3);
            graphics.blit(TEXTURE, leftPos + 64, topPos + 89, 52, 193, 110, 3);
        }
        for (int index = 0; index < 4; index++) {
            boolean occupied = index < spells.size();
            boolean hovered = occupied && isHovering(53 + 32 * index, 60, 16, 16, mouseX, mouseY);
            if (occupied) {
                graphics.blit(spells.get(index).getSpell().getSpellIconResource(),
                        leftPos + 53 + 32 * index, topPos + 60, 0, 0, 16, 16, 16, 16);
            }
            graphics.blit(TEXTURE, leftPos + 48 + 32 * index, topPos + 55, hovered ? 26 : 0, 190, 26, 26);
        }
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }
}
