package com.yukari.relicera.common.worldgen.structure;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.registry.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public final class AlchemistHousePiece extends TemplateStructurePiece {
    static final ResourceLocation TEMPLATE = ResourceLocation.fromNamespaceAndPath(ReliceraMod.MOD_ID, "alchemist_house");
    private static final int MAX_FOUNDATION_DEPTH = 6;

    public AlchemistHousePiece(StructureTemplateManager manager, BlockPos position, Rotation rotation) {
        super(ModStructures.ALCHEMIST_HOUSE_PIECE.get(), 0, manager, TEMPLATE, TEMPLATE.toString(), makeSettings(rotation), position);
    }

    public AlchemistHousePiece(StructureTemplateManager manager, CompoundTag tag) {
        super(ModStructures.ALCHEMIST_HOUSE_PIECE.get(), tag, manager,
                ignored -> makeSettings(Rotation.valueOf(tag.getString("Rot"))));
    }

    static StructurePlaceSettings makeSettings(Rotation rotation) {
        return new StructurePlaceSettings().setRotation(rotation).setMirror(Mirror.NONE)
                .setIgnoreEntities(false).setKeepLiquids(false)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString("Rot", this.placeSettings.getRotation().name());
    }

    @Override
    protected void handleDataMarker(String metadata, BlockPos pos, ServerLevelAccessor level,
                                    RandomSource random, BoundingBox chunkBoundingBox) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                            RandomSource random, BoundingBox chunkBoundingBox, ChunkPos chunkPos, BlockPos pivot) {
        super.postProcess(level, structureManager, generator, random, chunkBoundingBox, chunkPos, pivot);
        for (int x = 0; x < this.template.getSize().getX(); x++) {
            for (int z = 0; z < this.template.getSize().getZ(); z++) {
                BlockPos floor = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(
                        this.placeSettings, new BlockPos(x, 0, z)));
                if (!chunkBoundingBox.isInside(floor)) {
                    continue;
                }
                BlockState floorState = level.getBlockState(floor);
                if (!floorState.getFluidState().isEmpty() || !floorState.isFaceSturdy(level, floor, Direction.DOWN)) {
                    continue;
                }
                BlockState fill = (floorState.is(Blocks.GRASS_BLOCK) || floorState.is(Blocks.DIRT_PATH)
                        || floorState.is(Blocks.MOSS_BLOCK) ? Blocks.DIRT : Blocks.STONE).defaultBlockState();
                BlockPos.MutableBlockPos support = floor.mutable().move(Direction.DOWN);
                for (int depth = 0; depth < MAX_FOUNDATION_DEPTH && support.getY() > level.getMinBuildHeight(); depth++) {
                    BlockState current = level.getBlockState(support);
                    if (current.getFluidState().isEmpty() && current.isFaceSturdy(level, support, Direction.UP)) {
                        break;
                    }
                    if (!current.isAir() && current.getFluidState().isEmpty() && !current.canBeReplaced()) {
                        break;
                    }
                    level.setBlock(support, fill, 2);
                    support.move(Direction.DOWN);
                }
            }
        }
    }
}
