package com.example.client.render;

import net.minecraft.world.item.ItemStack;

public interface CatArmorRenderStateAccessor {
    ItemStack MCCatMod$getArmorStack();
    void MCCatMod$setArmorStack(ItemStack stack);
}

