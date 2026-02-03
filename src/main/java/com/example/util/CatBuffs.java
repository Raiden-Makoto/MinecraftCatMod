package com.example.util;

import com.example.util.buffs.CatBuffStrategy;
import com.example.util.buffs.CatBuffStrategyFactory;
import com.example.util.mining.CatMiningStrategyFactory;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public final class CatBuffs {
    private CatBuffs() {
    }

    public static void registerCatBuffs() {
        ServerTickEvents.END_SERVER_TICK.register(CatBuffs::MCCatMod$onServerTick);

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!(world instanceof ServerLevel serverLevel)) {
                return true;
            }
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return true;
            }

            BlockEntity be = blockEntity;
            MCCatMod$MINING_FACTORY.forVariant("minecraft:white").beforeBreak(serverLevel, serverPlayer, pos, state, be);
            return true;
        });
    }

    private static final double MCCatMod$BUFF_RADIUS = 8.0;
    private static final int MCCatMod$TICK_INTERVAL = 20;
    private static final CatBuffStrategyFactory MCCatMod$FACTORY = new CatBuffStrategyFactory();
    private static final CatMiningStrategyFactory MCCatMod$MINING_FACTORY = new CatMiningStrategyFactory();

    private static void MCCatMod$onServerTick(MinecraftServer server) {
        List<ServerPlayer> players = server.getPlayerList().getPlayers();

        for (ServerPlayer player : players) {
            if (player.tickCount % MCCatMod$TICK_INTERVAL != 0) {
                continue;
            }

            List<Cat> nearbyCats = player.level().getEntitiesOfClass(
                Cat.class,
                player.getBoundingBox().inflate(MCCatMod$BUFF_RADIUS),
                cat -> cat.isTame() && cat.isOwnedBy(player)
            );

            for (Cat cat : nearbyCats) {
                String variantId = cat.getVariant()
                    .unwrapKey()
                    .map(key -> key.identifier().toString())
                    .orElse("unregistered");

                CatBuffStrategy strategy = MCCatMod$FACTORY.forVariant(variantId);
                strategy.apply(player, cat);
            }
        }
    }
}