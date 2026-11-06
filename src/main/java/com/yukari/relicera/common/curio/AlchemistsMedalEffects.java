package com.yukari.relicera.common.curio;

import com.yukari.relicera.registry.ModItems;
import com.yukari.relicera.common.item.AlchemistsMedalItem;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

public final class AlchemistsMedalEffects {
    private AlchemistsMedalEffects() {
    }

    public static void onMagicDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide() || event.isCanceled()
                || !Float.isFinite(event.getAmount()) || event.getAmount() <= 0.0F
                || !(event.getSource().is(DamageTypes.MAGIC) || event.getSource().is(DamageTypes.INDIRECT_MAGIC))
                || !(event.getSource().getEntity() instanceof LivingEntity wearer)
                || wearer == event.getEntity() || !wearer.isAlive()) {
            return;
        }
        var medals = CuriosApi.getCuriosInventory(wearer).resolve()
                .map(handler -> handler.findCurios(ModItems.ALCHEMISTS_MEDAL.get())).orElse(List.of());
        if (medals.isEmpty()) {
            return;
        }
        LivingEntity target = event.getEntity();
        float damage = event.getAmount();
        for (MobEffectInstance effect : List.copyOf(target.getActiveEffects())) {
            if (effect.getEffect().isBeneficial()) {
                MobEffectInstance stolen = new MobEffectInstance(effect);
                if (target.removeEffect(effect.getEffect())) {
                    wearer.addEffect(stolen, wearer);
                }
            }
        }
        if (medals.stream().anyMatch(result -> AlchemistsMedalItem.hasConsumedBearskin(result.stack()))) {
            wearer.heal(damage);
        }
    }
}
