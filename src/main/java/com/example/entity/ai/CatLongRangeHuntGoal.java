package com.example.entity.ai;

import com.example.entity.CatRestedAccessor;
import com.example.entity.LongRangeHuntState;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class CatLongRangeHuntGoal extends NearestAttackableTargetGoal<Rabbit> {
    private final Cat cat;

    public CatLongRangeHuntGoal(Cat cat) {
        // 10 is the reciprocal of the chance to check for target per tick
        // 64.0D is the 4-chunk (64 block) follow range
        super(cat, Rabbit.class, 10, true, false, null);
        this.cat = cat;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        // 1. Daytime check: (0 to 13000 ticks)
        long time = cat.level().getDayTime() % 24000;
        if (time > 13000) return false;

        // 2. Tame & Rested check
        if (!cat.isTame() || !((CatRestedAccessor) cat).MCCatMod$isRested()) {
            return false;
        }

        // 3. Distance to Owner check: 32 blocks (1024 sq distance) - cat must be left behind
        Player owner = (Player) cat.getOwner();
        if (owner == null || cat.distanceToSqr(owner) < 1024.0D) {
            return false;
        }

        // 4. Sitting check: Only cats that were told to stay (sitting) and left behind get the hunt urge.
        //    Non-sitting cats would follow/teleport to owner; sitting cats stay home and may hunt instead.
        if (!cat.isOrderedToSit()) return false;

        return super.canUse();
    }

    @Override
    public void start() {
        ((LongRangeHuntState) this.cat).MCCatMod$setLongRangeHunting(true);
        // Get up from sitting so the cat can move and hunt
        this.cat.setOrderedToSit(false);
        this.cat.setSprinting(true);
        super.start();
    }

    @Override
    public void stop() {
        ((LongRangeHuntState) this.cat).MCCatMod$setLongRangeHunting(false);
        this.cat.setSprinting(false);
        // Sit back down when done hunting (stays at home)
        this.cat.setOrderedToSit(true);
        super.stop();
    }
}
