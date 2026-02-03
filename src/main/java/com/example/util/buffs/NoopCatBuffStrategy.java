package com.example.util.buffs;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.feline.Cat;

public final class NoopCatBuffStrategy implements CatBuffStrategy {
    @Override
    public String id() {
        return "noop";
    }

    @Override
    public void apply(ServerPlayer player, Cat cat) {
    }
}
