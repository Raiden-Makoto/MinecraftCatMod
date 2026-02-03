package com.example.item;

import com.example.entity.ZoomieState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CatnipItem extends Item {
    public CatnipItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof Cat cat && cat.isTame() && cat.isOwnedBy(player)) {
            if (!cat.level().isClientSide()) {
                // Prevent catnip from being interpreted as a "sit/stand" interaction.
                cat.setOrderedToSit(false);

                // Trigger Zoomies immediately
                ((ZoomieState) cat).setZooming(true);

                // Consume the item
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                // Visual feedback
                cat.level().broadcastEntityEvent(cat, (byte) 7); // Heart particles
            }

            // IMPORTANT: On server we must CONSUME so vanilla cat interaction doesn't run (and toggle sitting).
            return cat.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}