package com.example.mixin;

import com.example.entity.CatBedAccessor;
import com.example.entity.CatRestedAccessor;
import com.example.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(Cat.class)
public abstract class CatRestedMixin implements CatRestedAccessor, CatBedAccessor {

    @Override
    public boolean MCCatMod$isRested() {
        return ((Cat) (Object) this).getAttachedOrElse(ModAttachments.RESTED, false);
    }

    @Override
    public void MCCatMod$setRested(boolean rested) {
        ((Cat) (Object) this).setAttached(ModAttachments.RESTED, rested);
    }

    @Override
    public Optional<BlockPos> MCCatMod$getCatBed() {
        return ((Cat) (Object) this).getAttachedOrElse(ModAttachments.CAT_BED, Optional.empty());
    }

    @Override
    public void MCCatMod$setCatBed(Optional<BlockPos> bed) {
        ((Cat) (Object) this).setAttached(ModAttachments.CAT_BED, bed);
    }
}
