package com.example.entity.ai;

import com.example.entity.CatRestedAccessor;
import com.example.entity.LongRangeHuntState;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.feline.Cat;

import java.util.EnumSet;

public class CatLongRangeHuntGoal extends NearestAttackableTargetGoal<Rabbit> {
    // 0=6am. Hunting 21:00-23:00 (9pm-11pm)
    private static final long HUNT_START_TICKS = 15000L;
    private static final long HUNT_END_TICKS = 17000L;
    private static final double HUNT_RANGE = 12.0;

    private final Cat cat;

    public CatLongRangeHuntGoal(Cat cat) {
        super(cat, Rabbit.class, 10, true, false, null);
        this.cat = cat;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    protected double getFollowDistance() {
        return HUNT_RANGE;
    }

    @Override
    public boolean canUse() {
        // 1. Hunt window: 9pm–10:30pm only (cats get home before bedtime)
        long time = cat.level().getDayTime() % 24000;
        if (time < HUNT_START_TICKS || time >= HUNT_END_TICKS) return false;

        // 2. Tame, must belong to player, and rested (have cat bed to drop loot)
        if (!cat.isTame() || cat.getOwner() == null || !((CatRestedAccessor) cat).MCCatMod$isRested()) return false;

        return super.canUse();
    }

    @Override
    public void start() {
        ((LongRangeHuntState) this.cat).MCCatMod$setLongRangeHunting(true);
        this.cat.setSprinting(true);
        super.start();
    }

    @Override
    public void stop() {
        ((LongRangeHuntState) this.cat).MCCatMod$setLongRangeHunting(false);
        this.cat.setSprinting(false);
        super.stop();
    }
}
