package com.example.registry;

import com.example.CatMod;
import com.example.block.entity.CatBedBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
    public static final Identifier CAT_BED_ID = Identifier.fromNamespaceAndPath(CatMod.MOD_ID, "cat_bed");

    public static final BlockEntityType<CatBedBlockEntity> CAT_BED = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        CAT_BED_ID,
        FabricBlockEntityTypeBuilder.create(CatBedBlockEntity::new, ModBlocks.CAT_BED).build()
    );

    private ModBlockEntities() {
    }

    public static void registerModBlockEntities() {
        CatMod.LOGGER.info("Registering Cat Mod BlockEntities!");
    }
}

