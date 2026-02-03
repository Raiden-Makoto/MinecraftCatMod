package com.example.util.revenge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public interface CatRevengeStrategy {
    String id();

    void trigger(ServerPlayer player, LivingEntity attacker);
}
