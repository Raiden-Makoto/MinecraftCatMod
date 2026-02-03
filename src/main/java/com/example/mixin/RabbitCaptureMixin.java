package com.example.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class RabbitCaptureMixin {

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"), cancellable = true)
    private void mc_cat_mod$captureLootForCat(ServerLevel level, DamageSource source, CallbackInfo ci) {
        if (!((Object) this instanceof Rabbit)) {
            return;
        }
        if (source.getEntity() instanceof Cat cat) {
            if (cat.isTame()) {
                if (cat.getOwner() instanceof ServerPlayer owner) {
                    owner.sendSystemMessage(Component.literal("Cat caught a rabbit"));
                }
                cat.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.RABBIT));
                ci.cancel();
            }
        }
    }
}
