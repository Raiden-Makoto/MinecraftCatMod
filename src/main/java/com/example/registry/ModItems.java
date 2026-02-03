package com.example.registry;

import com.example.item.CatArmorItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import com.example.item.CatnipItem;

public class ModItems {
    public static final Identifier CAT_ARMOR_ID = Identifier.fromNamespaceAndPath("mc_cat_mod", "cat_armor");
    public static final ResourceKey<Item> CAT_ARMOR_KEY = ResourceKey.create(Registries.ITEM, CAT_ARMOR_ID);

    // This creates the "cat_armor" item in the game's internal list
    public static final Item CAT_ARMOR = Registry.register(
        BuiltInRegistries.ITEM, 
        CAT_ARMOR_ID,
        new CatArmorItem(
            new Item.Properties()
                .setId(CAT_ARMOR_KEY)
                .durability(240) // Unbreaking requires a durability value to function
                .enchantable(15) // Determines how good the enchantments are at the table
        )
    );

    public static final Identifier CATNIP_ID = Identifier.fromNamespaceAndPath("mc_cat_mod", "catnip");
    public static final ResourceKey<Item> CATNIP_KEY = ResourceKey.create(Registries.ITEM, CATNIP_ID);
    public static final Item CATNIP = Registry.register(
        BuiltInRegistries.ITEM, CATNIP_ID, new CatnipItem(new Item.Properties().setId(CATNIP_KEY).stacksTo(64)) // stackable to 64
    );
    public static void registerModItems() {
        // This method is just a hook for our main class
        System.out.println("LOG: Registering Cat Mod Items!");
    }
}