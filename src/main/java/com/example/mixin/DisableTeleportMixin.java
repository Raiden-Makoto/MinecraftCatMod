package com.example.mixin;

import com.example.entity.LongRangeHuntState;
import com.example.entity.ZoomieState;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FollowOwnerGoal.class)
public abstract class DisableTeleportMixin {
    @Shadow
    @Final
    private TamableAnimal tamable;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void MCCatMod$cancelFollowDuringZoomies(CallbackInfoReturnable<Boolean> cir) {
        // If zooming, the cat refuses to follow/teleport
        if (this.tamable instanceof ZoomieState zoomer && zoomer.isZooming()) {
            cir.setReturnValue(false);
        }
        // Hunt urge takes priority over teleporting - cat stays to hunt instead
        if (this.tamable instanceof LongRangeHuntState hunter && hunter.MCCatMod$isLongRangeHunting()) {
            cir.setReturnValue(false);
        }
    }
}