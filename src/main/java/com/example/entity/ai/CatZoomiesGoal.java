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

public final class CatZoomiesGoal extends Goal {
    private static final float MCCatMod$ZOOMIES_CHANCE_OTHER = 0.008f;
    private static final float MCCatMod$ZOOMIES_CHANCE_RED = 0.02f;
    private static final String MCCatMod$RED_VARIANT_ID = "minecraft:red";
    // If both are 0, zoomies are effectively disabled.
    private static final int MCCatMod$ZOOMIES_BASE_TICKS = 100;
    private static final int MCCatMod$ZOOMIES_RANDOM_EXTRA_TICKS = 100;
    // 0=6am. Zoomies 06:00-07:00
    private static final long MCCatMod$ZOOMIES_START_TIME = 0L;
    private static final long MCCatMod$ZOOMIES_END_TIME = 1000L;

    private final Cat cat;
    private int zoomieTicks;

    public CatZoomiesGoal(Cat cat) {
        this.cat = cat;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!(this.cat.level() instanceof ServerLevel)) {
            return false;
        }
        if (!this.cat.isTame()) {
            return false;
        }
        if (this.cat.isOrderedToSit()) {
            return false;
        }
        if (this.cat.getOwner() == null) {
            return false;
        }

        if (((ZoomieState) this.cat).isZooming()) {
            return true;
        }

        long dayTime = this.cat.level().getDayTime() % 24000L;
        boolean inZoomieWindow = dayTime >= MCCatMod$ZOOMIES_START_TIME && dayTime < MCCatMod$ZOOMIES_END_TIME;
        if (!inZoomieWindow) {
            return false;
        }

        String variantId = this.cat.getVariant().unwrapKey().map(k -> k.identifier().toString()).orElse("");
        float chance = variantId.equals(MCCatMod$RED_VARIANT_ID) ? MCCatMod$ZOOMIES_CHANCE_RED : MCCatMod$ZOOMIES_CHANCE_OTHER;
        return this.cat.getRandom().nextFloat() < chance;
    }

    @Override
    public void start() {
        if (!((ZoomieState) this.cat).isZooming()) {
            ((ZoomieState) this.cat).setZooming(true);
        }
        int extra = 0;
        if (MCCatMod$ZOOMIES_RANDOM_EXTRA_TICKS > 0) {
            extra = this.cat.getRandom().nextInt(MCCatMod$ZOOMIES_RANDOM_EXTRA_TICKS);
        }
        this.zoomieTicks = MCCatMod$ZOOMIES_BASE_TICKS + extra;

        BlockPos pos = this.cat.blockPosition();
        this.cat.level().playSound(null, pos, SoundEvents.CAT_BEG_FOR_FOOD, SoundSource.NEUTRAL, 1.0f, 1.5f);
    }

    @Override
    public boolean canContinueToUse() {
        return this.zoomieTicks > 0;
    }

    @Override
    public void tick() {
        this.zoomieTicks--;

        // Simple movement: periodically pick a random nearby spot.
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