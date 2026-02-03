package com.example.mixin;

import com.example.block.entity.CatBedBlockEntity;
import com.example.entity.CatBedAccessor;
import com.example.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class CatBedReleaseMixin {

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void MCCatMod$releaseBedOnRemoval(Entity.RemovalReason reason, CallbackInfo ci) {
        if (!((Object) this instanceof Cat cat)) {
            return;
        }
        if (cat.level().isClientSide()) {
            return;
        }
        Optional<BlockPos> bed = ((CatBedAccessor) cat).MCCatMod$getCatBed();
        if (bed.isEmpty()) {
            return;
        }
        if (cat.level().getBlockState(bed.get()).is(ModBlocks.CAT_BED)
            && cat.level().getBlockEntity(bed.get()) instanceof CatBedBlockEntity bedEntity) {
            if (bedEntity.MCCatMod$isClaimedBy(cat)) {
                bedEntity.MCCatMod$release();
            }
        }
    }
}
