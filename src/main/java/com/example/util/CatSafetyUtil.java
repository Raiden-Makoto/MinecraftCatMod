package com.example.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class CatSafetyUtil {
    public static boolean isSafe(Level level, BlockPos pos) {
        // 1. Check for immediate lava or fire
        if (level.getBlockState(pos).is(Blocks.LAVA) || level.getBlockState(pos).is(Blocks.FIRE)) return false;
        
        // 2. Check the block below (must be solid and NOT dangerous)
        BlockPos below = pos.below();
        if (level.getBlockState(below).is(Blocks.LAVA) || level.getBlockState(below).is(Blocks.MAGMA_BLOCK) || level.getBlockState(below).is(Blocks.CAMPFIRE)) return false;
        if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) return false;

        // 3. Ensure there is enough headspace (2 blocks of air/non-solid)
        if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) return false;
        BlockPos above = pos.above();
        if (!level.getBlockState(above).getCollisionShape(level, above).isEmpty()) return false;

        return true;
    }
}