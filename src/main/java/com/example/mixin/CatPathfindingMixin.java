package com.example.mixin;

import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.level.pathfinder.PathType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cat.class)
public abstract class CatPathfindingMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mc_cat_mod$hardenPathfinding(CallbackInfo ci) {
        Cat cat = (Cat) (Object) this;
        // Make cats treat Magma and Fire as completely impassable (avoidance)
        cat.setPathfindingMalus(PathType.DAMAGE_FIRE, 16.0F);
        cat.setPathfindingMalus(PathType.DANGER_FIRE, 16.0F);
        cat.setPathfindingMalus(PathType.DAMAGE_OTHER, 16.0F); // Covers berry bushes/cactus
        
        // Cats should hate water unless they are the Tuxedo (Treasure Hunter)
        if (!cat.getVariant().getRegisteredName().equals("minecraft:tuxedo")) {
            cat.setPathfindingMalus(PathType.WATER, 8.0F);
        }
    }
}