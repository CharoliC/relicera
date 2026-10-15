package com.yukari.relicera.common.item;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.network.ModNetworking;
import com.yukari.relicera.common.network.packet.OpenAstralStorybookPacket;
import com.yukari.relicera.registry.ModParticleTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class AstralStorybookItem extends Item {
    public static final int LITTLE_TAILOR_STORY_ID = 1;
    public static final int DEVILS_BEARSKIN_STORY_ID = 2;
    public static final int ALICE_IN_WONDERLAND_STORY_ID = 3;
    private static final Map<Integer, StoryDefinition> STORIES = Map.of(
            LITTLE_TAILOR_STORY_ID, new StoryDefinition(
                    LITTLE_TAILOR_STORY_ID,
                    11,
                    ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "little_tailors_belt")
            ),
            DEVILS_BEARSKIN_STORY_ID, new StoryDefinition(
                    DEVILS_BEARSKIN_STORY_ID,
                    13,
                    ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "devils_bearskin")
            ),
            ALICE_IN_WONDERLAND_STORY_ID, new StoryDefinition(
                    ALICE_IN_WONDERLAND_STORY_ID,
                    14,
                    ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "stopped_silver_chronometer")
            )
    );
    private static final String DATA_KEY = "AstralStorybook";
    private static final String STORY_KEY = "Story";
    private static final String ACTIVE_STORY_KEY = "ReliceraActiveStory";
    private static final String ACTIVE_STORY_DAY_KEY = "ReliceraActiveStoryDay";
    private static final long DREAM_BUBBLE_START_TIME = 11000L;
    private static final int DREAM_BUBBLE_SPAWN_INTERVAL_TICKS = 20;

    public AstralStorybookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            ItemStack readingCopy = stack.copy();
            int storyId = getStoryId(readingCopy);
            if (isKnownStory(storyId)) {
                rememberActiveStory(serverPlayer, storyId);
                setStoryId(stack, 0);
            }
            ModNetworking.sendToPlayer(new OpenAstralStorybookPacket(readingCopy), serverPlayer);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(getStoryDefinition(getStoryId(stack))
                .map(StoryDefinition::title)
                .orElseGet(() -> Component.translatable("tooltip.relicera.astral_storybook.empty")));
    }

    public static int getStoryId(ItemStack stack) {
        CompoundTag root = stack.getTag();
        if (root == null || !root.contains(DATA_KEY, Tag.TAG_COMPOUND)) {
            return 0;
        }
        CompoundTag data = root.getCompound(DATA_KEY);
        return data.contains(STORY_KEY, Tag.TAG_ANY_NUMERIC)
                ? Math.max(data.getInt(STORY_KEY), 0)
                : 0;
    }

    public static Optional<StoryDefinition> getStoryDefinition(int storyId) {
        return Optional.ofNullable(STORIES.get(storyId));
    }

    public static boolean isKnownStory(int storyId) {
        return STORIES.containsKey(storyId);
    }

    private static int getActiveStoryId(ServerPlayer player) {
        int storyId = player.getPersistentData().getInt(ACTIVE_STORY_KEY);
        return isKnownStory(storyId) ? storyId : 0;
    }

    public static int getActiveStoryIdForDay(ServerPlayer player, long day) {
        CompoundTag data = player.getPersistentData();
        int storyId = getActiveStoryId(player);
        if (storyId == 0
                || !data.contains(ACTIVE_STORY_DAY_KEY, Tag.TAG_ANY_NUMERIC)
                || data.getLong(ACTIVE_STORY_DAY_KEY) != day) {
            clearActiveStory(player);
            return 0;
        }
        return storyId;
    }

    public static void clearActiveStory(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        data.remove(ACTIVE_STORY_KEY);
        data.remove(ACTIVE_STORY_DAY_KEY);
    }

    private static void rememberActiveStory(ServerPlayer player, int storyId) {
        CompoundTag data = player.getPersistentData();
        data.putInt(ACTIVE_STORY_KEY, storyId);
        data.putLong(ACTIVE_STORY_DAY_KEY, player.getServer().overworld().getDayTime() / 24000L);
    }

    public static void tickDreamBubbles(ServerPlayer player) {
        if (player.tickCount % DREAM_BUBBLE_SPAWN_INTERVAL_TICKS != 0) {
            return;
        }

        long dayTime = player.server.overworld().getDayTime();
        if (Math.floorMod(dayTime, 24000L) < DREAM_BUBBLE_START_TIME
                || getActiveStoryIdForDay(player, Math.floorDiv(dayTime, 24000L)) == 0) {
            return;
        }

        ServerLevel level = player.serverLevel();
        RandomSource random = player.getRandom();
        double angle = random.nextDouble() * Math.PI * 2.0D;
        double radius = 0.3D + random.nextDouble() * 0.55D;
        double x = player.getX() + Math.cos(angle) * radius;
        double y = player.getY() + 0.25D + random.nextDouble() * 1.45D;
        double z = player.getZ() + Math.sin(angle) * radius;
        level.sendParticles(ModParticleTypes.DREAM_BUBBLE.get(), x, y, z,
                1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    public static void setStoryId(ItemStack stack, int storyId) {
        if (storyId > 0) {
            stack.getOrCreateTagElement(DATA_KEY).putInt(STORY_KEY, storyId);
            return;
        }

        CompoundTag root = stack.getTag();
        if (root == null || !root.contains(DATA_KEY, Tag.TAG_COMPOUND)) {
            return;
        }
        CompoundTag data = root.getCompound(DATA_KEY);
        data.remove(STORY_KEY);
        if (data.isEmpty()) {
            root.remove(DATA_KEY);
        }
        if (root.isEmpty()) {
            stack.setTag(null);
        }
    }

    public record StoryDefinition(int id, int paragraphCount, ResourceLocation rewardItemId) {
        public Component title() {
            return Component.translatable("tooltip.relicera.astral_storybook.story." + id);
        }

        public List<Component> paragraphs() {
            List<Component> paragraphs = new ArrayList<>(paragraphCount);
            for (int index = 0; index < paragraphCount; index++) {
                paragraphs.add(Component.translatable("storybook.relicera." + id + ".paragraph." + index));
            }
            return List.copyOf(paragraphs);
        }

        public ItemStack createReward() {
            Item item = ForgeRegistries.ITEMS.getValue(rewardItemId);
            return item == null || item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
        }
    }
}
