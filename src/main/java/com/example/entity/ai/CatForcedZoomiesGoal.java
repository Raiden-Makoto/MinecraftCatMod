package com.example.entity.ai;

import com.example.entity.ZoomieState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * High-priority "forced" zoomies that only runs when something (catnip) has already
 * marked the cat as zooming. This bypasses the normal time window / random chance.
 */
public final class CatForcedZoomiesGoal extends Goal {
    private static final int MCCatMod$ZOOMIES_BASE_TICKS = 100;
    private static final int MCCatMod$ZOOMIES_RANDOM_EXTRA_TICKS = 100;

    private final Cat cat;
    private int zoomieTicks;

    public CatForcedZoomiesGoal(Cat cat) {
        this.cat = cat;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!(this.cat.level() instanceof ServerLevel)) {
            return false;
        }
        return ((ZoomieState) this.cat).isZooming();
    }

    @Override
    public void start() {
        int extra = 0;
        if (MCCatMod$ZOOMIES_RANDOM_EXTRA_TICKS > 0) {
            extra = this.cat.getRandom().nextInt(MCCatMod$ZOOMIES_RANDOM_EXTRA_TICKS);
        }
        int duration = MCCatMod$ZOOMIES_BASE_TICKS + extra;
        if (duration <= 0) {
            this.zoomieTicks = 0;
            return;
        }

        this.zoomieTicks = duration;

        BlockPos pos = this.cat.blockPosition();
        this.cat.level().playSound(null, pos, SoundEvents.CAT_BEG_FOR_FOOD, SoundSource.NEUTRAL, 0.5f, 1.5f);
    }

    @Override
    public boolean canContinueToUse() {
        return this.zoomieTicks > 0 && ((ZoomieState) this.cat).isZooming();
    }

    @Override
    public void tick() {
        this.zoomieTicks--;

        if (this.zoomieTicks % 10 == 0) {
            Vec3 target = DefaultRandomPos.getPos(this.cat, 10, 7);
            if (target != null) {
                this.cat.getNavigation().moveTo(target.x, target.y, target.z, 1.35);
            }
        }
    }

    @Override
    public void stop() {
        ((ZoomieState) this.cat).setZooming(false);
        this.zoomieTicks = 0;
        this.cat.getNavigation().stop();
    }
}

