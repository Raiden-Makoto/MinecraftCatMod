package com.example.mixin;
import com.example.util.loot.SiameseLootContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(EnchantmentHelper.class)
public abstract class SiameseLootingLevelMixin {
    private static final double MCCatMod$BUFF_RADIUS = 8.0;
    private static final List<UUID> MCCatMod$SIAMESE_LOOT_LOG_PLAYER_IDS = new ArrayList<>();
    private static final List<Long> MCCatMod$SIAMESE_LOOT_LOG_NEXT_ALLOWED_TICKS = new ArrayList<>();

    @Inject(method = "getEnchantmentLevel", at = @At("RETURN"), cancellable = true)
    private static void MCCatMod$boostSiameseLootingLevel(
        Holder<Enchantment> enchantment,
        LivingEntity livingEntity,
        CallbackInfoReturnable<Integer> cir
    ) {
        LivingEntity victim = SiameseLootContext.MCCatMod$getVictim();
        if (!(victim instanceof Animal)) {
            return;
        }

        Player killer = SiameseLootContext.MCCatMod$getKiller();
        if (!(livingEntity instanceof Player player) || killer != player) {
            return;
        }

        String enchantmentId = enchantment.unwrapKey().map(key -> key.identifier().toString()).orElse("");
        if (!enchantmentId.equals("minecraft:looting")) {
            return;
        }

        AABB box = player.getBoundingBox().inflate(MCCatMod$BUFF_RADIUS);
        List<Cat> nearbyCats = player.level().getEntitiesOfClass(
            Cat.class,
            box,
            cat -> cat.isTame()
                && cat.isOwnedBy(player)
                && cat.getVariant().unwrapKey().map(k -> k.identifier().toString()).orElse("").equals("minecraft:siamese")
        );

        if (nearbyCats.isEmpty()) {
            return;
        }

        cir.setReturnValue(cir.getReturnValue() + 1);

        // Action-bar message when the Siamese bonus applies (throttled per player).
        if (!player.level().isClientSide()) {
            long now = player.level().getGameTime();
            UUID playerId = player.getUUID();
            int idx = -1;
            for (int i = 0; i < MCCatMod$SIAMESE_LOOT_LOG_PLAYER_IDS.size(); i++) {
                if (MCCatMod$SIAMESE_LOOT_LOG_PLAYER_IDS.get(i).equals(playerId)) {
                    idx = i;
                    break;
                }
            }
            if (idx == -1) {
                MCCatMod$SIAMESE_LOOT_LOG_PLAYER_IDS.add(playerId);
                MCCatMod$SIAMESE_LOOT_LOG_NEXT_ALLOWED_TICKS.add(0L);
                idx = MCCatMod$SIAMESE_LOOT_LOG_PLAYER_IDS.size() - 1;
            }

            long nextAllowed = MCCatMod$SIAMESE_LOOT_LOG_NEXT_ALLOWED_TICKS.get(idx);
            if (now >= nextAllowed) {
                MCCatMod$SIAMESE_LOOT_LOG_NEXT_ALLOWED_TICKS.set(idx, now + 20L);
                player.displayClientMessage(
                    Component.literal("Your Siamese has improved your loot!").withStyle(ChatFormatting.GOLD),
                    true
                );
            }
        }
    }
}
