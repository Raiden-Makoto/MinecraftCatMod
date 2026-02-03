package com.example.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(Player.class)
public abstract class ShorthairFallMixin {
    private static final double MCCatMod$BUFF_RADIUS = 8.0;

    @ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true)
    private double MCCatMod$reduceShorthairFallDamage(double fallDistance) {
        Player player = (Player) (Object) this;

        AABB box = player.getBoundingBox().inflate(MCCatMod$BUFF_RADIUS);
        List<Cat> nearbyCats = player.level().getEntitiesOfClass(
            Cat.class,
            box,
            cat -> cat.isTame()
                && cat.isOwnedBy(player)
                && cat.getVariant().unwrapKey().get().identifier().toString().equals("minecraft:british_shorthair")
        );

        if (!nearbyCats.isEmpty()) {
            player.displayClientMessage(
                Component.literal("Your Shorthair has reduced your fall damage!").withStyle(ChatFormatting.GOLD),
                true
            );
            return fallDistance * 0.5;
        }

        return fallDistance;
    }
}
