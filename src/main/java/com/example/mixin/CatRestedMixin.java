package com.example.mixin;

import com.example.entity.CatRestedAccessor;
import com.example.registry.ModAttachments;
import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Cat.class)
public abstract class CatRestedMixin implements CatRestedAccessor {

    @Override
    public boolean MCCatMod$isRested() {
        return ((Cat) (Object) this).getAttachedOrElse(ModAttachments.RESTED, false);
    }

    @Override
    public void MCCatMod$setRested(boolean rested) {
        ((Cat) (Object) this).setAttached(ModAttachments.RESTED, rested);
    }
}
