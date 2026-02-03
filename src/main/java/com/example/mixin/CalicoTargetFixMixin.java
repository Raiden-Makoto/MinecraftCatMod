package com.example.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class CalicoTargetFixMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void MCCatMod$clearDeadCalicoTarget(CallbackInfo ci) {
        if (!((Object) this instanceof Cat cat)) {
            return;
        }

        if (cat.level().isClientSide()) {
            return;
        }

        String variantId = cat.getVariant().unwrapKey().map(k -> k.identifier().toString()).orElse("");
        if (!variantId.equals("minecraft:calico")) {
            return;
        }

        LivingEntity target = cat.getTarget();
        if (target != null && (!target.isAlive() || target.isRemoved())) {
            cat.setTarget(null);
            cat.getNavigation().stop();
        }
    }
}
