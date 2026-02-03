package com.example.util.buffs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.feline.Cat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class JellieCatBuffStrategy implements CatBuffStrategy {
    private static final List<UUID> MCCatMod$JELLIE_MSG_PLAYER_IDS = new ArrayList<>();
    private static final List<Long> MCCatMod$JELLIE_MSG_NEXT_ALLOWED_TICKS = new ArrayList<>();

    @Override
    public String id() {
        return "minecraft:jellie";
    }

    @Override
    public void apply(ServerPlayer player, Cat cat) {
        if (player.tickCount % 2400 == 0 && player.getHealth() < player.getMaxHealth()) {
            player.heal(2.0f);

            long now = player.level().getGameTime();
            UUID playerId = player.getUUID();
            int idx = -1;
            for (int i = 0; i < MCCatMod$JELLIE_MSG_PLAYER_IDS.size(); i++) {
                if (MCCatMod$JELLIE_MSG_PLAYER_IDS.get(i).equals(playerId)) {
                    idx = i;
                    break;
                }
            }
            if (idx == -1) {
                MCCatMod$JELLIE_MSG_PLAYER_IDS.add(playerId);
                MCCatMod$JELLIE_MSG_NEXT_ALLOWED_TICKS.add(0L);
                idx = MCCatMod$JELLIE_MSG_PLAYER_IDS.size() - 1;
            }

            long nextAllowed = MCCatMod$JELLIE_MSG_NEXT_ALLOWED_TICKS.get(idx);
            if (now >= nextAllowed) {
                MCCatMod$JELLIE_MSG_NEXT_ALLOWED_TICKS.set(idx, now + 20L);
                player.displayClientMessage(
                    Component.literal("Your Jellie is healing you!").withStyle(ChatFormatting.GOLD),
                    true
                );
            }
        }
    }
}
