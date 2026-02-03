package com.example.entity;

import net.minecraft.core.BlockPos;

import java.util.Optional;

public interface CatBedAccessor {
    Optional<BlockPos> MCCatMod$getCatBed();
    void MCCatMod$setCatBed(Optional<BlockPos> bed);
}
