package com.yukari.relicera.common.effect.souloverload;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;

public class SoulOverloadMobEffect extends MobEffect {
    public SoulOverloadMobEffect() {
        super(MobEffectCategory.NEUTRAL, 0x38CFD4);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, "8fd36377-c055-4c33-8698-40db4c21f8b1",
                3.0D, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, "c56be934-8ef8-4954-bd8b-904fa43d5de4",
                0.2D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        if (entity instanceof Warden) {
            super.addAttributeModifiers(entity, attributes, amplifier);
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        if (entity instanceof Warden) {
            super.removeAttributeModifiers(entity, attributes, amplifier);
        }
    }
}
