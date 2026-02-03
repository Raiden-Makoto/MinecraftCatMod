package com.example.entity.ai;

import com.example.block.entity.CatBedBlockEntity;
import com.example.entity.CatRestedAccessor;
import com.example.entity.ZoomieState;
import com.example.mixin.CatRelaxStateAccessor;
import com.example.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CatBedSleepGoal extends Goal {
    private static final int MCCatMod$HORIZONTAL_RANGE = 16;
    private static final int MCCatMod$VERTICAL_RANGE = 8;
    private static final double MCCatMod$REACH_DISTANCE = 1.5;
    private static final double MCCatMod$BED_TOP_OFFSET = 9.0 / 16.0;
    private static final int MCCatMod$HEAL_INTERVAL = 40;
    private static final float MCCatMod$HEAL_AMOUNT = 0.5f;
    private static final int MCCatMod$WAKE_CHECK_INTERVAL = 80;
    private static final float MCCatMod$WAKE_CHANCE = 0.05f;
    private static final long MCCatMod$SLEEP_START = 12000L;
    private static final long MCCatMod$WAKE_WINDOW_START = 22000L;
    private static final long MCCatMod$WAKE_WINDOW_END = 2000L;

    private final Cat cat;
    private final double speed;
    private BlockPos MCCatMod$targetBed;
    private BlockPos MCCatMod$claimedBedPos;
    private int MCCatMod$ticksOnBed;
    private boolean MCCatMod$wakingUp;

    public CatBedSleepGoal(Cat cat, double speed) {
        this.cat = cat;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    private boolean MCCatMod$isSleepTime() {
        long dayTime = cat.level().getDayTime() % 24000L;
        return dayTime >= MCCatMod$SLEEP_START || dayTime < MCCatMod$WAKE_WINDOW_END;
    }

    private boolean MCCatMod$isValidBed(LevelReader level, BlockPos pos) {
        if (!level.getBlockState(pos).is(ModBlocks.CAT_BED)) {
            return false;
        }
        if (!(level.getBlockEntity(pos) instanceof CatBedBlockEntity bedEntity)) {
            return false;
        }
        return bedEntity.MCCatMod$canBeClaimedBy(cat);
    }

    private BlockPos MCCatMod$findBed() {
        BlockPos origin = cat.blockPosition();
        AABB searchBox = new AABB(
            origin.getX() - MCCatMod$HORIZONTAL_RANGE,
            origin.getY() - MCCatMod$VERTICAL_RANGE,
            origin.getZ() - MCCatMod$HORIZONTAL_RANGE,
            origin.getX() + MCCatMod$HORIZONTAL_RANGE,
            origin.getY() + MCCatMod$VERTICAL_RANGE,
            origin.getZ() + MCCatMod$HORIZONTAL_RANGE
        );
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockPos closest = null;
        double closestDist = Double.MAX_VALUE;

        for (int x = (int) searchBox.minX; x <= searchBox.maxX; x++) {
            for (int y = (int) searchBox.minY; y <= searchBox.maxY; y++) {
                for (int z = (int) searchBox.minZ; z <= searchBox.maxZ; z++) {
                    mutable.set(x, y, z);
                    if (MCCatMod$isValidBed(cat.level(), mutable)) {
                        double dist = cat.blockPosition().distSqr(mutable);
                        if (closest == null || dist < closestDist) {
                            closest = mutable.immutable();
                            closestDist = dist;
                        }
                    }
                }
            }
        }
        return closest;
    }

    private void MCCatMod$releaseClaimedBed() {
        if (MCCatMod$claimedBedPos != null && cat.level().getBlockEntity(MCCatMod$claimedBedPos) instanceof CatBedBlockEntity bedEntity) {
            if (bedEntity.MCCatMod$isClaimedBy(cat)) {
                bedEntity.MCCatMod$release();
            }
        }
        MCCatMod$claimedBedPos = null;
    }

    @Override
    public boolean canUse() {
        if (!cat.isTame()) {
            return false;
        }
        if (!MCCatMod$isSleepTime() && !cat.level().isThundering()) {
            return false;
        }
        if (cat.isOrderedToSit()) {
            return false;
        }
        MCCatMod$targetBed = MCCatMod$findBed();
        return MCCatMod$targetBed != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (MCCatMod$wakingUp) {
            return false;
        }
        if (!MCCatMod$isSleepTime() && !cat.level().isThundering()) {
            return false;
        }
        if (MCCatMod$targetBed == null) {
            return false;
        }
        if (!cat.level().getBlockState(MCCatMod$targetBed).is(ModBlocks.CAT_BED)) {
            return false;
        }
        if (!(cat.level().getBlockEntity(MCCatMod$targetBed) instanceof CatBedBlockEntity bedEntity) || !bedEntity.MCCatMod$canBeClaimedBy(cat)) {
            return false;
        }
        return true;
    }

    @Override
    public void start() {
        MCCatMod$ticksOnBed = 0;
        MCCatMod$wakingUp = false;
        if (MCCatMod$targetBed != null && cat.level().getBlockEntity(MCCatMod$targetBed) instanceof CatBedBlockEntity bedEntity) {
            if (bedEntity.MCCatMod$canBeClaimedBy(cat)) {
                bedEntity.MCCatMod$claim(cat);
                MCCatMod$claimedBedPos = MCCatMod$targetBed.immutable();
            }
        }
        cat.getNavigation().moveTo(
            MCCatMod$targetBed.getX() + 0.5,
            MCCatMod$targetBed.getY() + MCCatMod$BED_TOP_OFFSET,
            MCCatMod$targetBed.getZ() + 0.5,
            speed
        );
    }

    @Override
    public void tick() {
        if (MCCatMod$targetBed == null) {
            return;
        }

        Vec3 bedCenter = Vec3.atCenterOf(MCCatMod$targetBed);
        double distSq = cat.position().distanceToSqr(bedCenter);

        if (distSq <= MCCatMod$REACH_DISTANCE * MCCatMod$REACH_DISTANCE) {
            cat.getNavigation().stop();
            double centerX = MCCatMod$targetBed.getX() + 0.5;
            double centerZ = MCCatMod$targetBed.getZ() + 0.5;
            double centerY = MCCatMod$targetBed.getY() + MCCatMod$BED_TOP_OFFSET;
            cat.setPos(centerX, centerY, centerZ);

            cat.setInSittingPose(false);
            cat.setOrderedToSit(false);
            if (!cat.isLying()) {
                cat.setLying(true);
                ((CatRelaxStateAccessor) cat).MCCatMod$invokeSetRelaxStateOne(true);
            }

            MCCatMod$ticksOnBed++;
            long dayTime = cat.level().getDayTime() % 24000L;
            boolean inWakeWindow = dayTime >= MCCatMod$WAKE_WINDOW_START || dayTime < MCCatMod$WAKE_WINDOW_END;
            if (MCCatMod$ticksOnBed > 0 && inWakeWindow
                && MCCatMod$ticksOnBed % MCCatMod$WAKE_CHECK_INTERVAL == 0
                && cat.getRandom().nextFloat() < MCCatMod$WAKE_CHANCE) {
                MCCatMod$wakingUp = true;
                ((ZoomieState) cat).setZooming(true);
                ((CatRelaxStateAccessor) cat).MCCatMod$invokeSetRelaxStateOne(false);
                cat.setLying(false);
            }
            if (MCCatMod$ticksOnBed % MCCatMod$HEAL_INTERVAL == 0 && cat.getHealth() < cat.getMaxHealth()) {
                cat.heal(MCCatMod$HEAL_AMOUNT);
                cat.level().broadcastEntityEvent(cat, (byte) 7);
            }
            if (MCCatMod$ticksOnBed >= 100) {
                ((CatRestedAccessor) cat).MCCatMod$setRested(true);
                cat.level().broadcastEntityEvent(cat, (byte) 7);
                MCCatMod$ticksOnBed = 0;
            }
        } else {
            MCCatMod$ticksOnBed = 0;
            if (!cat.getNavigation().isInProgress()) {
                cat.getNavigation().moveTo(
                    MCCatMod$targetBed.getX() + 0.5,
                    MCCatMod$targetBed.getY() + MCCatMod$BED_TOP_OFFSET,
                    MCCatMod$targetBed.getZ() + 0.5,
                    speed
                );
            }
        }
    }

    @Override
    public void stop() {
        MCCatMod$releaseClaimedBed();
        MCCatMod$targetBed = null;
        ((CatRelaxStateAccessor) cat).MCCatMod$invokeSetRelaxStateOne(false);
        cat.setLying(false);
        MCCatMod$wakingUp = false;
        cat.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
