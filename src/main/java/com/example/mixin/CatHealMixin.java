package com.example.mixin;

import net.minecraft.core.particles.ParticleTypes; // Mojang name
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cat.class) // Mojang targets 'Cat', not 'CatEntity'
public class CatHealMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void MCCatMod$autoHeal(CallbackInfo ci) {
        Cat cat = (Cat) (Object) this;

        if (cat.level().isClientSide()) {
            return;
        }
        
        // Heal tamed cats while they're sitting.
        if (cat.isTame() && cat.isInSittingPose() && cat.getHealth() < cat.getMaxHealth()) {
            
            // Heal every 5 seconds (100 ticks)
            if (cat.tickCount % 100 == 0) {
                cat.heal(1.0f); 
                
                ServerLevel level = (ServerLevel) cat.level();
                level.sendParticles(
                    ParticleTypes.HEART,
                    cat.getX(),
                    cat.getY() + 0.5,
                    cat.getZ(),
                    1,
                    0.2,
                    0.2,
                    0.2,
                    0.0
                );
            }
        }
    }
}