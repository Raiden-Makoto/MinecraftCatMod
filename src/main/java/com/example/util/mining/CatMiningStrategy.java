package com.example.util.mining;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface CatMiningStrategy {
    String id();

    void beforeBreak(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, BlockEntity blockEntity);
}
