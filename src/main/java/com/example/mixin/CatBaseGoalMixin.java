package com.example.mixin;

import com.example.entity.ai.CatBedSleepGoal;
import com.example.entity.ai.CatLongRangeHuntGoal;
import com.example.entity.ai.CatZoomiesGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cat.class)
public abstract class CatBaseGoalMixin extends Mob {
    protected CatBaseGoalMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"))
    private void MCCatMod$injectSleepGoalFirst(CallbackInfo ci) {
        this.goalSelector.addGoal(0, new CatBedSleepGoal((Cat) (Object) this, 1.2));
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void MCCatMod$injectGoals(CallbackInfo ci) {
        this.goalSelector.removeAllGoals(g -> {
            String name = g.getClass().getName();
            return name.contains("RelaxOnOwner") || name.contains("LieOnBed") || name.contains("SitOnBlock") || name.contains("OcelotAttackGoal");
        });

        this.goalSelector.addGoal(4, new CatZoomiesGoal((Cat) (Object) this));
        this.goalSelector.addGoal(5, new MeleeAttackGoal((Cat) (Object) this, 1.2, true));
        this.targetSelector.addGoal(5, new CatLongRangeHuntGoal((Cat) (Object) this));
    }
}