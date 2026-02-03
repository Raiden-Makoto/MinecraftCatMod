package com.example.util.buffs;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.feline.Cat;

/**
 * Marker strategy: the actual "all_black" buff is handled in {@code BlackCatDamageMixin}.
 * This exists so debug logs don't show "noop" for all-black cats.
 */
public final class AllBlackCatBuffStrategy implements CatBuffStrategy {
    @Override
    public String id() {
        return "minecraft:all_black";
    }

    @Override
    public void apply(ServerPlayer player, Cat cat) {
    }
}
