package com.example.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.animal.feline.CatModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class CatHeldItemLayer extends RenderLayer<CatRenderState, CatModel> {

    private final ItemModelResolver itemModelResolver;

    public CatHeldItemLayer(RenderLayerParent<CatRenderState, CatModel> parent) {
        super(parent);
        this.itemModelResolver = Minecraft.getInstance().getItemModelResolver();
    }

    @Override
    public void submit(
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        int packedLight,
        CatRenderState catRenderState,
        float f,
        float g
    ) {
        ItemStack mainHand = ((CatRenderStateMainHandAccessor) catRenderState).MCCatMod$getMainHandStack();
        if (mainHand.isEmpty()) {
            return;
        }

        ItemStackRenderState renderState = new ItemStackRenderState();
        itemModelResolver.updateForTopItem(
            renderState,
            mainHand,
            ItemDisplayContext.GROUND,
            Minecraft.getInstance().level,
            null,
            0
        );
        if (renderState.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        CatModel model = getParentModel();
        model.root().getChild("head").translateAndRotate(poseStack);
        // Position in mouth: forward (nose) and down from head pivot
        poseStack.translate(0.0, -0.25, -0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0f));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        renderState.submit(poseStack, submitNodeCollector, packedLight, 0, 0);

        poseStack.popPose();
    }
}
