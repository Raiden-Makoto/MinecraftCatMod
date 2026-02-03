package com.example.entity.ai;

import com.example.block.entity.CatBedBlockEntity;
import com.example.entity.CatBedAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CatDeliverLootGoal extends Goal {
    private static final int MCCatMod$GIFT_SLOTS = 3;
    private static final double MCCatMod$SPEED = 1.5;
    private static final double MCCatMod$REACH_DIST_SQ = 2.25;

    private final Cat cat;
    private BlockPos targetBed;

    public CatDeliverLootGoal(Cat cat) {
        this.cat = cat;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cat.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            return false;
        }
        this.targetBed = ((CatBedAccessor) cat).MCCatMod$getCatBed().orElse(null);
        return this.targetBed != null;
    }

    @Override
    public void tick() {
        double centerX = targetBed.getX() + 0.5;
        double centerY = targetBed.getY();
        double centerZ = targetBed.getZ() + 0.5;

        cat.getNavigation().moveTo(centerX, centerY, centerZ, MCCatMod$SPEED);

        if (cat.position().distanceToSqr(Vec3.atCenterOf(targetBed)) < MCCatMod$REACH_DIST_SQ) {
            BlockEntity be = cat.level().getBlockEntity(targetBed);

            if (be instanceof CatBedBlockEntity bed) {
                ItemStack heldItem = cat.getItemInHand(InteractionHand.MAIN_HAND);

                for (int i = 0; i < MCCatMod$GIFT_SLOTS; i++) {
                    if (bed.getItem(i).isEmpty()) {
                        bed.setItem(i, heldItem.copy());
                        cat.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                        break;
                    }
                }
            }
        }
    }
}
