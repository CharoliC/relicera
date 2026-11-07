package com.yukari.relicera.common.worldgen.structure;

import com.mojang.serialization.Codec;
import com.yukari.relicera.registry.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

public final class AlchemistHouseStructure extends Structure {
    public static final Codec<AlchemistHouseStructure> CODEC = simpleCodec(AlchemistHouseStructure::new);
    private static final int TERRAIN_MARGIN = 2;
    private static final int MAX_HEIGHT_DIFFERENCE = 5;

    public AlchemistHouseStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int centerX = context.chunkPos().getMiddleBlockX();
        int centerZ = context.chunkPos().getMiddleBlockZ();
        int centerY = context.chunkGenerator().getBaseHeight(centerX, centerZ, Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(), context.randomState()) - 1;
        if (!isAllowedBiome(context, centerX, centerY, centerZ)) {
            return Optional.empty();
        }

        Optional<StructureTemplate> template = context.structureTemplateManager().get(AlchemistHousePiece.TEMPLATE);
        if (template.isEmpty() || template.get().getSize().getY() <= 0) {
            return Optional.empty();
        }
        Rotation rotation = Rotation.getRandom(context.random());
        BoundingBox relativeBox = template.get().getBoundingBox(AlchemistHousePiece.makeSettings(rotation), BlockPos.ZERO);
        BlockPos origin = new BlockPos(centerX - relativeBox.getCenter().getX(), 0,
                centerZ - relativeBox.getCenter().getZ());
        BoundingBox footprint = template.get().getBoundingBox(AlchemistHousePiece.makeSettings(rotation), origin);
        OptionalInt floorY = findFloor(context, footprint);
        if (floorY.isEmpty() || floorY.getAsInt() + template.get().getSize().getY() > context.heightAccessor().getMaxBuildHeight()) {
            return Optional.empty();
        }
        BlockPos position = origin.atY(floorY.getAsInt());
        return Optional.of(new GenerationStub(new BlockPos(centerX, floorY.getAsInt(), centerZ),
                pieces -> pieces.addPiece(new AlchemistHousePiece(context.structureTemplateManager(), position, rotation))));
    }

    private static OptionalInt findFloor(GenerationContext context, BoundingBox footprint) {
        int minX = footprint.minX() - TERRAIN_MARGIN;
        int maxX = footprint.maxX() + TERRAIN_MARGIN;
        int minZ = footprint.minZ() - TERRAIN_MARGIN;
        int maxZ = footprint.maxZ() + TERRAIN_MARGIN;
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        int floor = Integer.MIN_VALUE;
        Map<Long, Integer> sampled = new HashMap<>();
        for (int x : new int[]{footprint.minX(), footprint.maxX()}) {
            for (int z : new int[]{footprint.minZ(), footprint.maxZ()}) {
                OptionalInt surface = drySurface(context, x, z);
                if (surface.isEmpty() || !isAllowedBiome(context, x, surface.getAsInt(), z)) {
                    return OptionalInt.empty();
                }
                int y = surface.getAsInt();
                sampled.put(BlockPos.asLong(x, 0, z), y);
                lowest = Math.min(lowest, y);
                highest = Math.max(highest, y);
            }
        }
        if (highest - lowest > MAX_HEIGHT_DIFFERENCE) {
            return OptionalInt.empty();
        }
        for (int stride : new int[]{4, 1}) {
            for (int x = minX; x <= maxX; x += stride) {
                for (int z = minZ; z <= maxZ; z += stride) {
                    long key = BlockPos.asLong(x, 0, z);
                    Integer y = sampled.get(key);
                    if (y == null) {
                        OptionalInt surface = drySurface(context, x, z);
                        if (surface.isEmpty()) {
                            return OptionalInt.empty();
                        }
                        y = surface.getAsInt();
                        sampled.put(key, y);
                    }
                    lowest = Math.min(lowest, y);
                    highest = Math.max(highest, y);
                    if (highest - lowest > MAX_HEIGHT_DIFFERENCE) {
                        return OptionalInt.empty();
                    }
                    if (x >= footprint.minX() && x <= footprint.maxX() && z >= footprint.minZ() && z <= footprint.maxZ()) {
                        floor = Math.max(floor, y);
                    }
                }
            }
        }
        return OptionalInt.of(floor - 1);
    }

    private static OptionalInt drySurface(GenerationContext context, int x, int z) {
        NoiseColumn column = context.chunkGenerator().getBaseColumn(x, z, context.heightAccessor(), context.randomState());
        int minY = context.heightAccessor().getMinBuildHeight();
        int y = context.heightAccessor().getMaxBuildHeight() - 1;
        while (y > minY && column.getBlock(y).isAir()) {
            y--;
        }
        BlockPos surface = new BlockPos(x, y, z);
        BlockState state = column.getBlock(y);
        if (y <= minY || !state.getFluidState().isEmpty()
                || !state.isFaceSturdy(EmptyBlockGetter.INSTANCE, surface, Direction.UP)) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(y);
    }

    private static boolean isAllowedBiome(GenerationContext context, int x, int y, int z) {
        return context.validBiome().test(context.biomeSource().getNoiseBiome(
                QuartPos.fromBlock(x), QuartPos.fromBlock(y), QuartPos.fromBlock(z), context.randomState().sampler()));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.ALCHEMIST_HOUSE.get();
    }
}
