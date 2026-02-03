package com.example.mixin;

import com.example.entity.RagdollTradeCooldownAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Villager.class)
public abstract class RagdollTradeMixin {

    @Unique
    private static final String MCCatMod$RAGDOLL_VARIANT_ID = "minecraft:ragdoll";

    @Unique
    private static final long MCCatMod$EFFECT_DURATION_TICKS = 6000L; // 5 minutes

    @Unique
    private static final long MCCatMod$COOLDOWN_AFTER_EFFECT_TICKS = 6000L; // 5 minutes

    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void MCCatMod$applyRagdollHeroEffect(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.level().isClientSide()) {
            return;
        }

        long currentTime = player.level().getGameTime();
        RagdollTradeCooldownAccessor accessor = (RagdollTradeCooldownAccessor) player;
        long nextAllowed = accessor.MCCatMod$getRagdollTradeNextAllowedTick();

        if (currentTime < nextAllowed) {
            return;
        }

        // Check for tamed Ragdoll within 8 blocks
        AABB box = player.getBoundingBox().inflate(8.0);
        List<Cat> nearbyCats = player.level().getEntitiesOfClass(
            Cat.class,
            box,
            cat -> cat.isTame()
                && cat.isOwnedBy(player)
                && cat.getVariant().unwrapKey().map(k -> k.identifier().toString()).orElse("").equals(MCCatMod$RAGDOLL_VARIANT_ID)
        );

        if (!nearbyCats.isEmpty()) {
            player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, (int) MCCatMod$EFFECT_DURATION_TICKS, 0, true, true, true));
            player.displayClientMessage(
                Component.literal("Your Ragdoll has brought you good fortune!").withStyle(ChatFormatting.GOLD),
                true
            );

            accessor.MCCatMod$setRagdollTradeNextAllowedTick(
                currentTime + MCCatMod$EFFECT_DURATION_TICKS + MCCatMod$COOLDOWN_AFTER_EFFECT_TICKS
            );
        }
    }
}