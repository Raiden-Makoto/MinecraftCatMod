package com.example.mixin;

import com.example.util.loot.SiameseLootContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class SiameseLootMixin {
    @Inject(
        method = "dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;ZLnet/minecraft/resources/ResourceKey;Ljava/util/function/Consumer;)V",
        at = @At("HEAD")
    )
    private void MCCatMod$beginSiameseLootContext(
        ServerLevel serverLevel,
        DamageSource source,
        boolean hitByPlayer,
        ResourceKey<LootTable> lootTable,
        Consumer<ItemStack> consumer,
        CallbackInfo ci
    ) {
        LivingEntity victim = (LivingEntity) (Object) this;
        Player killer = source.getEntity() instanceof Player p ? p : null;
        SiameseLootContext.MCCatMod$set(victim, killer, source);
    }

    @Inject(
        method = "dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;ZLnet/minecraft/resources/ResourceKey;Ljava/util/function/Consumer;)V",
        at = @At("RETURN")
    )
    private void MCCatMod$endSiameseLootContext(
        ServerLevel serverLevel,
        DamageSource source,
        boolean hitByPlayer,
        ResourceKey<LootTable> lootTable,
        Consumer<ItemStack> consumer,
        CallbackInfo ci
    ) {
        SiameseLootContext.MCCatMod$clear();
    }
}
