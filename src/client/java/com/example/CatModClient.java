package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.world.item.DyeColor;
import com.example.block.CatBedBlock;
import com.example.registry.ModBlocks;

public class CatModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ColorProviderRegistry.BLOCK.register(
			(state, world, pos, tintIndex) -> {
				if (tintIndex != 0) {
					return -1;
				}
				DyeColor color = state.getValue(CatBedBlock.COLOR);
				return color.getTextureDiffuseColor();
			},
			ModBlocks.CAT_BED
		);
	}
}