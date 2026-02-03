package com.example.mixin.client;

import com.example.client.render.CatArmorRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CatRenderState.class)
public class CatRenderStateArmorMixin implements CatArmorRenderStateAccessor {
    @Unique
    private ItemStack MCCatMod$armorStack = ItemStack.EMPTY;

    @Override
    public ItemStack MCCatMod$getArmorStack() {
        return this.MCCatMod$armorStack;
    }

    @Override
    public void MCCatMod$setArmorStack(ItemStack stack) {
        this.MCCatMod$armorStack = stack;
    }
}

