package com.example.mixin;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class TuxedoEntityEventMixin {
    private static final List<UUID> MCCatMod$TUXEDO_MSG_PLAYER_IDS = new ArrayList<>();
    private static final List<Long> MCCatMod$TUXEDO_MSG_NEXT_ALLOWED_TICKS = new ArrayList<>();

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void MCCatMod$showTuxedoTreasureParticles(byte status, CallbackInfo ci) {
        if (!((Object) this instanceof Cat cat)) {
            return;
        }

        if (status != 10) {
            return;
        }

        if (!cat.level().isClientSide()) {
            return;
        }

        String variantId = cat.getVariant().unwrapKey().map(k -> k.identifier().toString()).orElse("");
        if (!variantId.equals("minecraft:tuxedo")) {
            return;
        }

        for (int i = 0; i < 5; i++) {
            cat.level().addParticle(
                ParticleTypes.ELECTRIC_SPARK,
                cat.getRandomX(0.5D),
                cat.getY() + 1.5D,
                cat.getRandomZ(0.5D),
                0.0D,
                0.0D,
                0.0D
            );
        }

        // Action-bar message for the owning player (throttled).
        if (cat.getOwner() instanceof Player player) {
            long now = cat.level().getGameTime();
            UUID playerId = player.getUUID();
            int idx = -1;
            for (int i = 0; i < MCCatMod$TUXEDO_MSG_PLAYER_IDS.size(); i++) {
                if (MCCatMod$TUXEDO_MSG_PLAYER_IDS.get(i).equals(playerId)) {
                    idx = i;
                    break;
                }
            }
            if (idx == -1) {
                MCCatMod$TUXEDO_MSG_PLAYER_IDS.add(playerId);
                MCCatMod$TUXEDO_MSG_NEXT_ALLOWED_TICKS.add(0L);
                idx = MCCatMod$TUXEDO_MSG_PLAYER_IDS.size() - 1;
            }

            long nextAllowed = MCCatMod$TUXEDO_MSG_NEXT_ALLOWED_TICKS.get(idx);
            if (now >= nextAllowed) {
                MCCatMod$TUXEDO_MSG_NEXT_ALLOWED_TICKS.set(idx, now + 20L);
                player.displayClientMessage(
                    Component.literal("Your Tuxedo has found treasure nearby!").withStyle(ChatFormatting.GOLD),
                    true
                );
            }
        }
    }
}
