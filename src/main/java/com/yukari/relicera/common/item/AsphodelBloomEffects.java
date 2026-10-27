package com.yukari.relicera.common.item;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.curio.CurioSlotUnlocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ReliceraMod.MOD_ID)
public final class AsphodelBloomEffects {
    private static final ResourceLocation COMPLETION_ADVANCEMENT =
            ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "asphodel_bloom");
    private static final Map<UUID, UseSession> SESSIONS = new HashMap<>();

    private AsphodelBloomEffects() {
    }

    static void begin(ServerPlayer player, ItemStack stack) {
        stop(player);
        UseSession session = new UseSession(stack);
        SESSIONS.put(player.getUUID(), session);
        session.ensureEffects(player);
        playHeartbeat(player);
    }

    static void tick(ServerPlayer player, ItemStack stack) {
        UseSession session = SESSIONS.get(player.getUUID());
        if (session == null || session.stack != stack || !player.isAlive()) {
            stop(player);
            return;
        }
        session.ticks++;
        session.ensureEffects(player);
        if (session.ticks % 5 == 0) {
            player.serverLevel().sendParticles(ParticleTypes.SCULK_CHARGE_POP,
                    player.getX(), player.getY() + player.getBbHeight() * 0.5D, player.getZ(),
                    1, 0.65D, 0.45D, 0.65D, 0.0D);
        }
        if (session.ticks % 20 == 0 && session.ticks < AsphodelBloomItem.USE_DURATION) {
            playHeartbeat(player);
        }
    }

    static void finish(ServerPlayer player, ItemStack stack) {
        UseSession session = SESSIONS.get(player.getUUID());
        boolean complete = session != null && session.stack == stack
                && session.ticks >= AsphodelBloomItem.USE_DURATION && player.isAlive();
        stop(player);
        if (complete && CurioSlotUnlocks.unlock(player, CurioSlotUnlocks.Reward.ASPHODEL_BLOOM)) {
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            Advancement advancement = player.server.getAdvancements().getAdvancement(COMPLETION_ADVANCEMENT);
            if (advancement != null) {
                player.getAdvancements().award(advancement, "used_asphodel_bloom");
            }
        }
    }

    static void stop(ServerPlayer player) {
        UseSession session = SESSIONS.remove(player.getUUID());
        if (session != null) {
            session.darkness.release(player);
            session.blindness.release(player);
        }
    }

    private static void playHeartbeat(ServerPlayer player) {
        player.playNotifySound(SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        UseSession session = SESSIONS.get(player.getUUID());
        if (session == null) {
            return;
        }
        MobEffect effect = event.getEffectInstance().getEffect();
        GrantedEffect granted = effect == MobEffects.DARKNESS ? session.darkness
                : effect == MobEffects.BLINDNESS ? session.blindness : null;
        if (granted == null || granted.supplied == null || event.getEffectInstance() == granted.supplied
                || event.getOldEffectInstance() != granted.supplied) {
            return;
        }
        MobEffectInstance external = granted.remainingExternal(player);
        if (external == null) {
            external = MobEffectInstance.load(event.getEffectInstance().save(new CompoundTag()));
        } else {
            external.update(event.getEffectInstance());
        }
        granted.externalEffect = external == null ? null : external.save(new CompoundTag());
        granted.externalTime = player.level().getGameTime();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        UseSession session = SESSIONS.get(player.getUUID());
        if (session != null && (!player.isAlive() || !player.isUsingItem() || player.getUseItem() != session.stack)) {
            stop(player);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            stop(player);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayer player) {
            stop(player);
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && SESSIONS.containsKey(player.getUUID())) {
            player.stopUsingItem();
            stop(player);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        SESSIONS.clear();
    }

    private static final class UseSession {
        private final ItemStack stack;
        private int ticks;
        private final GrantedEffect darkness = new GrantedEffect(MobEffects.DARKNESS);
        private final GrantedEffect blindness = new GrantedEffect(MobEffects.BLINDNESS);

        private UseSession(ItemStack stack) {
            this.stack = stack;
        }

        private void ensureEffects(ServerPlayer player) {
            darkness.ensure(player);
            blindness.ensure(player);
        }
    }

    private static final class GrantedEffect {
        private final MobEffect effect;
        private MobEffectInstance supplied;
        private CompoundTag externalEffect;
        private long externalTime;

        private GrantedEffect(MobEffect effect) {
            this.effect = effect;
        }

        private void ensure(ServerPlayer player) {
            if (player.hasEffect(effect)) {
                return;
            }
            externalEffect = null;
            supplied = new MobEffectInstance(effect, AsphodelBloomItem.USE_DURATION + 20, 0, false, false, true);
            if (!player.addEffect(supplied)) {
                supplied = null;
            }
        }

        private void release(ServerPlayer player) {
            if (supplied == null || player.getEffect(effect) != supplied) {
                return;
            }
            if (player.removeEffect(effect)) {
                MobEffectInstance external = remainingExternal(player);
                if (external != null && player.isAlive()) {
                    player.addEffect(external);
                }
            }
        }

        private MobEffectInstance remainingExternal(ServerPlayer player) {
            if (externalEffect == null) {
                return null;
            }
            CompoundTag remaining = ageEffect(externalEffect.copy(),
                    Math.max(0L, player.level().getGameTime() - externalTime));
            return remaining == null ? null : MobEffectInstance.load(remaining);
        }

        private static CompoundTag ageEffect(CompoundTag effect, long elapsed) {
            CompoundTag hidden = effect.contains("HiddenEffect", Tag.TAG_COMPOUND)
                    ? ageEffect(effect.getCompound("HiddenEffect"), elapsed) : null;
            effect.remove("HiddenEffect");
            if (hidden != null) {
                effect.put("HiddenEffect", hidden);
            }
            int duration = effect.getInt("Duration");
            if (duration != MobEffectInstance.INFINITE_DURATION) {
                if (duration <= elapsed) {
                    return hidden;
                }
                effect.putInt("Duration", (int) (duration - elapsed));
            }
            return effect;
        }
    }
}
