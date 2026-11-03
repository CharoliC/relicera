package com.yukari.relicera.registry;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.block.DreamcatcherBoxBlock;
import com.yukari.relicera.common.block.EphemeralBloomBlock;
import com.yukari.relicera.common.block.PottedEphemeralBloomBlock;
import com.yukari.relicera.common.block.RelicRepairTableBlock;
import com.yukari.relicera.common.block.SculkFruitCropBlock;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ReliceraMod.MOD_ID);

    public static final RegistryObject<Block> EPHEMERAL_BLOOM = BLOCKS.register("ephemeral_bloom", () ->
            new EphemeralBloomBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> POTTED_EPHEMERAL_BLOOM = BLOCKS.register("potted_ephemeral_bloom", () ->
            new PottedEphemeralBloomBlock(
                    () -> (FlowerPotBlock) Blocks.FLOWER_POT,
                    EPHEMERAL_BLOOM,
                    BlockBehaviour.Properties.copy(Blocks.FLOWER_POT)));

    public static final RegistryObject<Block> RELIC_REPAIR_TABLE = BLOCKS.register("relic_repair_table", () ->
            new RelicRepairTableBlock(BlockBehaviour.Properties.copy(Blocks.SMITHING_TABLE)
                    .strength(3.5F, 1200.0F)
                    .sound(SoundType.DEEPSLATE)
                    .noOcclusion()));

    public static final RegistryObject<Block> DREAMCATCHER_BOX = BLOCKS.register("dreamcatcher_box", () ->
            new DreamcatcherBoxBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(3.0F, 1200.0F)
                    .sound(SoundType.DEEPSLATE)
                    .lightLevel(state -> 7)));

    public static final RegistryObject<Block> SILVERED_DEBRIS = BLOCKS.register("silvered_debris", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.ANCIENT_DEBRIS)));

    public static final RegistryObject<Block> SCULK_FRUIT_CROP = BLOCKS.register("sculk_fruit_crop", () ->
            new SculkFruitCropBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noOcclusion()
                    .instabreak()
                    .sound(SoundType.SCULK)
                    .lightLevel(state -> 9)
                    .pushReaction(PushReaction.DESTROY)));

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
