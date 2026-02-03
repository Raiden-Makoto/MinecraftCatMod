package com.example.mixin;

import com.example.entity.ZoomieState;
import com.example.registry.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cat.class)
public abstract class CatnipInteractionMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void MCCatMod$handleCatnip(
        Player player,
        InteractionHand hand,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        Cat cat = (Cat) (Object) this;
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.is(ModItems.CATNIP)) {
            return;
        }
        if (!cat.isTame() || !cat.isOwnedBy(player)) {
            return;
        }

        // IMPORTANT: Consume interaction so vanilla "toggle sitting" doesn't run.
        if (cat.level().isClientSide()) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        cat.setOrderedToSit(false);
        ((ZoomieState) cat).setZooming(true);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        cat.level().broadcastEntityEvent(cat, (byte) 7); // Heart particles
        player.swing(hand);

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}

