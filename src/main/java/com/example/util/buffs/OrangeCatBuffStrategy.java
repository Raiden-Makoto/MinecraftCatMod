package com.example.util.buffs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.feline.Cat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class OrangeCatBuffStrategy implements CatBuffStrategy {
    private static final List<UUID> MCCatMod$ORANGE_MSG_PLAYER_IDS = new ArrayList<>();
    private static final List<Long> MCCatMod$ORANGE_MSG_NEXT_ALLOWED_TICKS = new ArrayList<>();

    @Override
    public String id() {
        return "minecraft:red";
    }

    @Override
    public void apply(ServerPlayer player, Cat cat) {
        boolean changed = false;

        // Status effects check: these should appear on the player while an orange cat is nearby.
        if (!player.hasEffect(MobEffects.SPEED)) {
            changed |= player.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 0, true, true, true));
        }
        if (!player.hasEffect(MobEffects.JUMP_BOOST)) {
            changed |= player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 100, 0, true, true, true));
        }

        if (changed) {
            long now = player.level().getGameTime();
            UUID playerId = player.getUUID();
            int idx = -1;
            for (int i = 0; i < MCCatMod$ORANGE_MSG_PLAYER_IDS.size(); i++) {
                if (MCCatMod$ORANGE_MSG_PLAYER_IDS.get(i).equals(playerId)) {
                    idx = i;
                    break;
                }
            }
            if (idx == -1) {
                MCCatMod$ORANGE_MSG_PLAYER_IDS.add(playerId);
                MCCatMod$ORANGE_MSG_NEXT_ALLOWED_TICKS.add(0L);
                idx = MCCatMod$ORANGE_MSG_PLAYER_IDS.size() - 1;
            }

            long nextAllowed = MCCatMod$ORANGE_MSG_NEXT_ALLOWED_TICKS.get(idx);
            if (now >= nextAllowed) {
                MCCatMod$ORANGE_MSG_NEXT_ALLOWED_TICKS.set(idx, now + 20L);
                player.displayClientMessage(
                    Component.literal("Your Orange Cat has buffed your movement!").withStyle(ChatFormatting.GOLD),
                    true
                );
            }
        }
    }
}
