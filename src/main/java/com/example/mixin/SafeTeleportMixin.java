package com.example.mixin;

import com.example.util.CatSafetyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TamableAnimal.class)
public abstract class SafeTeleportMixin {

    @Inject(method = "canTeleportTo", at = @At("HEAD"), cancellable = true)
    private void MCCatMod$blockUnsafeCatTeleports(BlockPos targetPos, CallbackInfoReturnable<Boolean> cir) {
        // Only apply this safety rule to cats (other tamables keep vanilla behavior).
        if (!((Object) this instanceof Cat cat)) {
            return;
        }

        // If the intended teleport spot is dangerous, abort this teleport attempt.
        if (!CatSafetyUtil.isSafe(cat.level(), targetPos)) {
            cir.setReturnValue(false);
        }
    }
}