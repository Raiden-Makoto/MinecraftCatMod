package com.example.mixin;

import com.example.entity.RagdollTradeCooldownAccessor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerRagdollTradeCooldownMixin implements RagdollTradeCooldownAccessor {

    @Unique
    private long MCCatMod$ragdollTradeNextAllowedTick = 0L;

    @Override
    public long MCCatMod$getRagdollTradeNextAllowedTick() {
        return this.MCCatMod$ragdollTradeNextAllowedTick;
    }

    @Override
    public void MCCatMod$setRagdollTradeNextAllowedTick(long tick) {
        this.MCCatMod$ragdollTradeNextAllowedTick = tick;
    }
}

