package com.example.mixin;

import com.example.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cat.class)
public abstract class CatBedOffsetMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void MCCatMod$adjustHeightOnBed(CallbackInfo ci) {
        Cat cat = (Cat) (Object) this;
        BlockPos pos = cat.blockPosition();

        if (cat.level().getBlockState(pos).is(ModBlocks.CAT_BED)) {
            cat.setPos(cat.getX(), pos.getY() + 9.0 / 16.0, cat.getZ());
        }
    }
}
