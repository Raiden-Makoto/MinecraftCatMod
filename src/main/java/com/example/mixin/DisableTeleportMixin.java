package com.example.mixin;

import com.example.entity.LongRangeHuntState;
import com.example.entity.ZoomieState;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
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
        // Block teleport when cat is actively hunting a rabbit
        if (this.tamable instanceof Cat cat) {
            LivingEntity target = ((Mob) cat).getTarget();
            if (target instanceof Rabbit) {
                cir.setReturnValue(false);
            }
        }
    }
}