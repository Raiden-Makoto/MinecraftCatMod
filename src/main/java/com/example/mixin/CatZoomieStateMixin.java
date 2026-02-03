package com.example.mixin;

import com.example.entity.ZoomieState;
import com.example.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cat.class)
public abstract class CatZoomieStateMixin implements ZoomieState {
    @Unique
    private static final EntityDataAccessor<Boolean> MCCatMod$IS_ZOOMING =
        SynchedEntityData.defineId(Cat.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void MCCatMod$defineZoomieData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(MCCatMod$IS_ZOOMING, false);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void MCCatMod$spawnZoomieParticles(CallbackInfo ci) {
        Cat cat = (Cat) (Object) this;
        if (!cat.level().isClientSide()) {
            return;
        }
        if (!this.isZooming()) {
            return;
        }
        if (cat.tickCount % 2 != 0) {
            return;
        }
        if (!cat.getNavigation().isInProgress()) {
            return;
        }
        if (!cat.onGround()) {
            return;
        }

        // Get the block the cat is currently standing on
        BlockPos pos = cat.getBlockPosBelowThatAffectsMyMovement();
        BlockState state = cat.level().getBlockState(pos);

        // Skip wool/block particles when on cat bed
        if (state.isAir() || state.is(ModBlocks.CAT_BED)) {
            return;
        }
        // This spawns the "sprint" particle of the specific block
        cat.level().addParticle(
            new BlockParticleOption(ParticleTypes.BLOCK, state),
            cat.getX(),
            cat.getY(),
            cat.getZ(),
            (cat.getRandom().nextDouble() - 0.5D) * 0.2D,
            0.1D,
            (cat.getRandom().nextDouble() - 0.5D) * 0.2D
        );
    }

    @Override
    public void setZooming(boolean zooming) {
        ((Cat) (Object) this).getEntityData().set(MCCatMod$IS_ZOOMING, zooming);
    }

    @Override
    public boolean isZooming() {
        return ((Cat) (Object) this).getEntityData().get(MCCatMod$IS_ZOOMING);
    }

    // Do NOT persist isZooming - it's transient. Loading with zooming=true can leave the cat
    // in a broken state (e.g. not clickable) until the zoomie goal stops.
}