package com.example.mixin;

import com.example.entity.ai.TuxedoLocateTreasureGoal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cat.class)
public abstract class TuxedoGoalMixin extends Mob {
    protected TuxedoGoalMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void MCCatMod$addTuxedoTreasureGoal(CallbackInfo ci) {
        // Priority 5: runs before FollowOwnerGoal (priority 6).
        this.goalSelector.addGoal(5, new TuxedoLocateTreasureGoal((Cat) (Object) this));
    }
}