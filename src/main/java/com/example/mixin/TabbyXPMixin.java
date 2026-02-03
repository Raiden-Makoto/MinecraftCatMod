package com.example.mixin;

import com.example.CatMod;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(ExperienceOrb.class)
public abstract class TabbyXPMixin {
    private static final List<UUID> MCCatMod$TABBY_LOG_PLAYER_IDS = new ArrayList<>();
    private static final List<Long> MCCatMod$TABBY_LOG_NEXT_ALLOWED_TICKS = new ArrayList<>();

    @Redirect(
        method = "playerTouch",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;giveExperiencePoints(I)V"
        )
    )
    private void MCCatMod$boostTabbyAwardedXp(Player player, int awardedXp) {
        // Only run logic on the server to prevent desync
        if (player.level().isClientSide()) {
            player.giveExperiencePoints(awardedXp);
            return;
        }

        // Check for tamed Tabby within 8 blocks
        AABB box = player.getBoundingBox().inflate(8.0);
        List<Cat> nearbyCats = player.level().getEntitiesOfClass(
            Cat.class,
            box,
            cat -> cat.isTame()
                && cat.isOwnedBy(player)
                && cat.getVariant().unwrapKey().map(k -> k.identifier().toString()).orElse("").equals("minecraft:tabby")
        );

        if (nearbyCats.isEmpty()) {
            player.giveExperiencePoints(awardedXp);
            return;
        }

        // Add +20% of the XP the player would actually gain (after Mending), rounded up.
        float base = (float) awardedXp;
        float bonusF = base * 0.2f;
        int bonus = (int) bonusF;
        if ((float) bonus < bonusF) {
            bonus += 1;
        }

        int newAwardedXp = awardedXp + bonus;

        // Debug logging: confirm when Tabby XP bonus applied (throttled per-player).
        long now = player.level().getGameTime();
        UUID playerId = player.getUUID();
        int idx = -1;
        for (int i = 0; i < MCCatMod$TABBY_LOG_PLAYER_IDS.size(); i++) {
            if (MCCatMod$TABBY_LOG_PLAYER_IDS.get(i).equals(playerId)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) {
            MCCatMod$TABBY_LOG_PLAYER_IDS.add(playerId);
            MCCatMod$TABBY_LOG_NEXT_ALLOWED_TICKS.add(0L);
            idx = MCCatMod$TABBY_LOG_PLAYER_IDS.size() - 1;
        }

        long nextAllowed = MCCatMod$TABBY_LOG_NEXT_ALLOWED_TICKS.get(idx);
        if (now >= nextAllowed) {
            MCCatMod$TABBY_LOG_NEXT_ALLOWED_TICKS.set(idx, now + 20L);
            CatMod.LOGGER.info(
                "[TABBY_XP] player={}, tabbiesNearby={}, awardedXP {} -> {} (+{})",
                player.getName().getString(),
                nearbyCats.size(),
                awardedXp,
                newAwardedXp,
                bonus
            );
        }

        player.giveExperiencePoints(newAwardedXp);
        player.displayClientMessage(
            Component.literal("Your Tabby has boosted your XP gain!").withStyle(ChatFormatting.GOLD),
            true
        );
    }
}