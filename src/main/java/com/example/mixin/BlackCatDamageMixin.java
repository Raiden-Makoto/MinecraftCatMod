package com.example.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class BlackCatDamageMixin {
    @Unique
    private long MCCatMod$lastBlackCatStrike = 0L;

    private static final double MCCatMod$BUFF_RADIUS = 8.0;

    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
    private float MCCatMod$applyBlackCatDoubleDamage(float amount, ServerLevel serverLevel, DamageSource source) {
        LivingEntity victim = (LivingEntity) (Object) this;
        if (!(victim instanceof Monster)) {
            return amount;
        }

        if (!(source.getEntity() instanceof Player player)) {
            return amount;
        }

        long currentTime = serverLevel.getGameTime();
        long sinceLast = currentTime - this.MCCatMod$lastBlackCatStrike;
        if (sinceLast < 100L) {
            return amount;
        }

        AABB box = player.getBoundingBox().inflate(MCCatMod$BUFF_RADIUS);
        List<Cat> nearbyCats = player.level().getEntitiesOfClass(
            Cat.class,
            box,
            cat -> cat.isTame()
                && cat.isOwnedBy(player)
                && cat.getVariant().unwrapKey().get().identifier().toString().equals("minecraft:all_black")
        );

        if (!nearbyCats.isEmpty()) {
            this.MCCatMod$lastBlackCatStrike = currentTime;
            player.displayClientMessage(
                Component.literal("Your Black Cat has buffed your combat!").withStyle(ChatFormatting.GOLD),
                true
            );
            return amount * 2.0f;
        }

        return amount;
    }
}
