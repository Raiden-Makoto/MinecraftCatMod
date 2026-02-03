package com.example.mixin;
import com.example.util.revenge.CatRevengeStrategy;
import com.example.util.revenge.CatRevengeStrategyFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class CalicoRevengeMixin {
    private static final CatRevengeStrategyFactory MCCatMod$FACTORY = new CatRevengeStrategyFactory();

    @Inject(method = "actuallyHurt", at = @At("TAIL"))
    private void MCCatMod$triggerCalicoRevenge(ServerLevel serverLevel, DamageSource source, float amount, CallbackInfo ci) {
        if (!((Object) this instanceof ServerPlayer player)) {
            return;
        }
        if (!(source.getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        CatRevengeStrategy strategy = MCCatMod$FACTORY.forVariant("minecraft:calico");
        strategy.trigger(player, attacker);
    }
}
