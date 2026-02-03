package com.example.mixin;

import com.example.entity.LongRangeHuntState;
import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Cat.class)
public abstract class CatLongRangeHuntStateMixin implements LongRangeHuntState {
    @Unique
    private boolean MCCatMod$longRangeHunting;

    @Override
    public void MCCatMod$setLongRangeHunting(boolean hunting) {
        this.MCCatMod$longRangeHunting = hunting;
    }

    @Override
    public boolean MCCatMod$isLongRangeHunting() {
        return this.MCCatMod$longRangeHunting;
    }
}
