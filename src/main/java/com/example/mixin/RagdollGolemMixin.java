package com.example.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(IronGolem.class)
public abstract class RagdollGolemMixin {
    private static final String MCCatMod$RAGDOLL_VARIANT_ID = "minecraft:ragdoll";

    @Inject(method = "doHurtTarget", at = @At("HEAD"), cancellable = true)
    private void MCCatMod$ragdollPreventsGolemHittingPlayer(ServerLevel serverLevel, Entity victim, CallbackInfoReturnable<Boolean> cir) {
        IronGolem golem = (IronGolem) (Object) this;
        if (victim instanceof Player player) {
            AABB area = player.getBoundingBox().inflate(6.0);
            List<Cat> nearbyCats = serverLevel.getEntitiesOfClass(
                Cat.class,
                area,
                cat -> cat.isTame()
                    && cat.isOwnedBy(player)
                    && cat.getVariant().unwrapKey().map(k -> k.identifier().toString()).orElse("").equals(MCCatMod$RAGDOLL_VARIANT_ID)
            );

            if (!nearbyCats.isEmpty()) {
                golem.stopBeingAngry();
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void MCCatMod$dynamicRagdollTruce(CallbackInfo ci) {
        IronGolem golem = (IronGolem) (Object) this;
        if (golem.level().isClientSide()) {
            return;
        }

        LivingEntity target = golem.getTarget();

        // Only care if the Golem is currently targeting a Player
        if (target instanceof Player player) {
            // Search for tamed Ragdoll within 6 blocks (4 + 2 safety)
            AABB area = player.getBoundingBox().inflate(6.0);
            List<Cat> nearbyCats = golem.level().getEntitiesOfClass(Cat.class, area,
                cat -> cat.isTame()
                    && cat.isOwnedBy(player)
                    && cat.getVariant().unwrapKey().map(k -> k.identifier().toString()).orElse("").equals(MCCatMod$RAGDOLL_VARIANT_ID));

            if (!nearbyCats.isEmpty()) {
                // Cat is present: Force peace
                golem.stopBeingAngry();
                player.displayClientMessage(
                    Component.literal("Your Ragdoll has stopped the Golem from attacking you!").withStyle(ChatFormatting.GOLD),
                    true
                );
            }
            // If nearbyCats is empty, we do NOTHING. 
            // This allows the Golem's natural AI to keep its target or find a new one.
        }
    }
}