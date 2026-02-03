package com.example.util.revenge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public final class NoopRevengeStrategy implements CatRevengeStrategy {
    @Override
    public String id() {
        return "noop";
    }

    @Override
    public void trigger(ServerPlayer player, LivingEntity attacker) {
    }
}
