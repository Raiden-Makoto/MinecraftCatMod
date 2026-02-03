package com.example.mixin;

import com.example.entity.CatArmorAccessor;
import com.example.registry.ModAttachments;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Cat.class)
public abstract class CatArmorDataMixin implements CatArmorAccessor {

    @Override
    public ItemStack MCCatMod$getArmor() {
        return ((Cat) (Object) this).getAttachedOrElse(ModAttachments.ARMOR, ItemStack.EMPTY);
    }

    @Override
    public void MCCatMod$setArmor(ItemStack stack) {
        ((Cat) (Object) this).setAttached(ModAttachments.ARMOR, stack == null ? ItemStack.EMPTY : stack);
    }
}
