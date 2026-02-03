package com.example.mixin;

import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Cat.class)
public interface CatRelaxStateAccessor {
    @Invoker("setRelaxStateOne")
    void MCCatMod$invokeSetRelaxStateOne(boolean value);
}
