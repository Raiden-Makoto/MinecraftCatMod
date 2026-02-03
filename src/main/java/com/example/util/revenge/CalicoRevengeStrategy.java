package com.example.util.revenge;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class CalicoRevengeStrategy implements CatRevengeStrategy {
    private static final double MCCatMod$RADIUS = 8.0;
    private static final float MCCatMod$CHANCE = 0.3f;
    private static final List<UUID> MCCatMod$CALICO_MSG_PLAYER_IDS = new ArrayList<>();
    private static final List<Long> MCCatMod$CALICO_MSG_NEXT_ALLOWED_TICKS = new ArrayList<>();

    @Override
    public String id() {
        return "minecraft:calico";
    }

    @Override
    public void trigger(ServerPlayer player, LivingEntity attacker) {
        AABB box = player.getBoundingBox().inflate(MCCatMod$RADIUS);
        List<Cat> cats = player.level().getEntitiesOfClass(
            Cat.class,
            box,
            cat -> cat.isTame()
                && cat.isOwnedBy(player)
                && cat.getVariant().unwrapKey().get().identifier().toString().equals(this.id())
        );

        float roll = player.getRandom().nextFloat();
        boolean triggered = roll < MCCatMod$CHANCE;

        // Stacking behavior: when the effect triggers, ALL nearby calico cats attack the same target.
        if (triggered && !cats.isEmpty()) {
            // Action-bar message (throttled per player) when calicos start targeting.
            long now = player.level().getGameTime();
            UUID playerId = player.getUUID();
            int idx = -1;
            for (int i = 0; i < MCCatMod$CALICO_MSG_PLAYER_IDS.size(); i++) {
                if (MCCatMod$CALICO_MSG_PLAYER_IDS.get(i).equals(playerId)) {
                    idx = i;
                    break;
                }
            }
            if (idx == -1) {
                MCCatMod$CALICO_MSG_PLAYER_IDS.add(playerId);
                MCCatMod$CALICO_MSG_NEXT_ALLOWED_TICKS.add(0L);
                idx = MCCatMod$CALICO_MSG_PLAYER_IDS.size() - 1;
            }

            long nextAllowed = MCCatMod$CALICO_MSG_NEXT_ALLOWED_TICKS.get(idx);
            if (now >= nextAllowed) {
                MCCatMod$CALICO_MSG_NEXT_ALLOWED_TICKS.set(idx, now + 20L);
                player.displayClientMessage(
                    Component.literal("Your Calico is protecting you!").withStyle(ChatFormatting.GOLD),
                    true
                );
            }

            for (Cat cat : cats) {
                cat.setTarget(attacker);
            }
        }
    }
}
