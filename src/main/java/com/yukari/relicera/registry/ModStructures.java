package com.yukari.relicera.registry;

import com.yukari.relicera.ReliceraMod;
import com.yukari.relicera.common.worldgen.structure.AncientForgePiece;
import com.yukari.relicera.common.worldgen.structure.AncientForgeStructure;
import com.yukari.relicera.common.worldgen.structure.AlchemistHouseStructure;
import com.yukari.relicera.common.worldgen.structure.AlchemistHousePiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModStructures {
    private static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, ReliceraMod.MOD_ID);
    private static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, ReliceraMod.MOD_ID);

    public static final RegistryObject<StructureType<AncientForgeStructure>> ANCIENT_FORGE =
            STRUCTURE_TYPES.register("ancient_forge", () -> () -> AncientForgeStructure.CODEC);

    public static final RegistryObject<StructurePieceType> ANCIENT_FORGE_PIECE =
            STRUCTURE_PIECE_TYPES.register("ancient_forge", () ->
                    (StructurePieceType.StructureTemplateType) AncientForgePiece::new);

    public static final RegistryObject<StructureType<AlchemistHouseStructure>> ALCHEMIST_HOUSE =
            STRUCTURE_TYPES.register("alchemist_house", () -> () -> AlchemistHouseStructure.CODEC);

    public static final RegistryObject<StructurePieceType> ALCHEMIST_HOUSE_PIECE =
            STRUCTURE_PIECE_TYPES.register("alchemist_house", () ->
                    (StructurePieceType.StructureTemplateType) AlchemistHousePiece::new);

    private ModStructures() {
    }

    public static void register(IEventBus eventBus) {
        STRUCTURE_TYPES.register(eventBus);
        STRUCTURE_PIECE_TYPES.register(eventBus);
    }
}
