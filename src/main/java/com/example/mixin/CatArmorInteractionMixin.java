package com.example.mixin;

import com.example.entity.CatArmorAccessor;
import com.example.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cat.class)
public abstract class CatArmorInteractionMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void MCCatMod$handleArmorInteractions(
        Player player,
        InteractionHand hand,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        Cat cat = (Cat) (Object) this;
        ItemStack stack = player.getItemInHand(hand);
        CatArmorAccessor accessor = (CatArmorAccessor) cat;
        ItemStack currentArmor = accessor.MCCatMod$getArmor();

        // 1. EQUIP: Right-click with Cat Armor
        if (stack.is(ModItems.CAT_ARMOR) && cat.isTame() && currentArmor.isEmpty()) {
            if (cat.level().isClientSide()) {
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }

            accessor.MCCatMod$setArmor(stack.copy());

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            player.swing(hand);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }

        // 2. REMOVE: Right-click with Shears
        else if (stack.is(Items.SHEARS) && cat.isTame() && !currentArmor.isEmpty()) {
            if (cat.level().isClientSide()) {
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }

            ServerLevel level = (ServerLevel) cat.level();
            cat.spawnAtLocation(level, currentArmor.copy());
            accessor.MCCatMod$setArmor(ItemStack.EMPTY);

            player.swing(hand);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }

        // 3. REPAIR: Right-click with String
        else if (stack.is(Items.STRING) && cat.isTame() && !currentArmor.isEmpty() && currentArmor.isDamaged()) {
            if (cat.level().isClientSide()) {
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }

            int newDamage = Math.max(0, currentArmor.getDamageValue() - 10);
            currentArmor.setDamageValue(newDamage);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            player.swing(hand);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }

        // 4. EMPTY HAND: Toggle sit/stand (ensures this works after load when vanilla path may fail)
        else if (stack.isEmpty() && cat.isTame() && cat.isOwnedBy(player)) {
            if (cat.level().isClientSide()) {
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }
            cat.setOrderedToSit(!cat.isOrderedToSit());
            cat.setLying(false);
            player.swing(hand);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}