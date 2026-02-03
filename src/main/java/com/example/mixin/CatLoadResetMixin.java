package com.example.mixin;

import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cat.class)
public abstract class CatLoadResetMixin {

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void MCCatMod$resetStuckStateOnLoad(ValueInput input, CallbackInfo ci) {
        Cat cat = (Cat) (Object) this;
        cat.setOrderedToSit(false);
        cat.setLying(false);
    }
}
