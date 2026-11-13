package com.yukari.relicera.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import com.yukari.relicera.ReliceraMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.HorseArmorItem;

public class TempestsReinsItem extends HorseArmorItem {
    private static final int ARMOR_PROTECTION = 16;
    private static final ResourceLocation ARMOR_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ReliceraMod.MOD_ID,
            "textures/entity/horse/armor/horse_armor_tempests_reins.png"
    );

    public TempestsReinsItem(Properties properties) {
        super(ARMOR_PROTECTION, "tempests_reins", properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(ItemTooltipText.flavor("tempests_reins", 0));
        tooltip.add(Component.empty());
        tooltip.add(ItemTooltipText.heading("tempests_reins", 1));
        tooltip.add(ItemTooltipText.description("tempests_reins", 2));
    }

    @Override
    public ResourceLocation getTexture() {
        return ARMOR_TEXTURE;
    }
}
