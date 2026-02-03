package com.example.registry;

import com.example.CatMod;
import com.example.block.CatBedBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
    public static final Identifier CAT_BED_ID = Identifier.fromNamespaceAndPath(CatMod.MOD_ID, "cat_bed");
    public static final ResourceKey<Block> CAT_BED_KEY = ResourceKey.create(Registries.BLOCK, CAT_BED_ID);
    public static final ResourceKey<Item> CAT_BED_ITEM_KEY = ResourceKey.create(Registries.ITEM, CAT_BED_ID);

    public static final Block CAT_BED = Registry.register(
        BuiltInRegistries.BLOCK,
        CAT_BED_ID,
        new CatBedBlock(
            BlockBehaviour.Properties.of()
                .setId(CAT_BED_KEY)
                .mapColor(MapColor.WOOL)
                .strength(0.8F)
                .sound(SoundType.WOOL)
                .noOcclusion()
        )
    );

    public static final Item CAT_BED_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        CAT_BED_ID,
        new BlockItem(CAT_BED, new Item.Properties().setId(CAT_BED_ITEM_KEY))
    );

    private ModBlocks() {
    }

    public static void registerModBlocks() {
        CatMod.LOGGER.info("Registering Cat Mod Blocks!");
    }
}