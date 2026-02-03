package com.example.mixin;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {
    @Accessor("cookingTimer")
    int MCCatMod$getCookingTimer();

    @Accessor("cookingTimer")
    void MCCatMod$setCookingTimer(int cookingTimer);

    @Accessor("cookingTotalTime")
    int MCCatMod$getCookingTotalTime();
}

