package com.example.mixin.client;

import com.example.client.render.CatArmorFeatureRenderer;
import com.example.client.render.CatArmorRenderStateAccessor;
import com.example.entity.CatArmorAccessor;
import net.minecraft.client.model.animal.feline.CatModel;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatRenderer.class)
public abstract class CatRendererMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void MCCatMod$addArmorLayer(EntityRendererProvider.Context context, CallbackInfo ci) {
        RenderLayerParent<CatRenderState, CatModel> parent = (RenderLayerParent<CatRenderState, CatModel>) (Object) this;
        ((LivingEntityRendererInvoker) (Object) this).MCCatMod$callAddLayer(new CatArmorFeatureRenderer(parent, context.getModelSet()));
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void MCCatMod$copyArmorToRenderState(Cat cat, CatRenderState catRenderState, float f, CallbackInfo ci) {
        ItemStack armor = ((CatArmorAccessor) cat).MCCatMod$getArmor();
        ((CatArmorRenderStateAccessor) catRenderState).MCCatMod$setArmorStack(armor.copy());
    }
}

