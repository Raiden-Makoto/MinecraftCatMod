package com.example.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class CatArmorItem extends Item{
    public CatArmorItem(Properties properties) {
        super(properties.stacksTo(1).fireResistant().rarity(Rarity.RARE));
    }
}
