package com.example.util.buffs;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.feline.Cat;

public interface CatBuffStrategy {
    String id();

    void apply(ServerPlayer player, Cat cat);
}
