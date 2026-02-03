package com.example.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.animal.feline.CatModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class CatArmorFeatureRenderer extends RenderLayer<CatRenderState, CatModel> {
    // Reuse the existing item texture so the layer renders in-game without adding new assets.
    private static final Identifier ARMOR_TEXTURE =
        Identifier.fromNamespaceAndPath("mc_cat_mod", "textures/item/cat_armor.png");

    private final CatModel adultModel;
    private final CatModel babyModel;

    public CatArmorFeatureRenderer(RenderLayerParent<CatRenderState, CatModel> parent, EntityModelSet modelSet) {
        super(parent);
        this.adultModel = new CatModel(modelSet.bakeLayer(ModelLayers.CAT));
        this.babyModel = new CatModel(modelSet.bakeLayer(ModelLayers.CAT_BABY));
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
        ItemStack armorStack = ((CatArmorRenderStateAccessor) catRenderState).MCCatMod$getArmorStack();

        if (!armorStack.isEmpty()) {
            CatModel model = catRenderState.isBaby ? this.babyModel : this.adultModel;
            coloredCutoutModelCopyLayerRender(model, ARMOR_TEXTURE, poseStack, submitNodeCollector, packedLight, catRenderState, 0xFFFFFF, 1);
        }
    }
}

