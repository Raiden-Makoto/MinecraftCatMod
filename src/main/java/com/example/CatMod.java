package com.example;

import com.example.registry.ModAttachments;
import com.example.registry.ModBlockEntities;
import com.example.registry.ModBlocks;
import com.example.registry.ModItems;
import com.example.util.CatBuffs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatMod implements ModInitializer {
	public static final String MOD_ID = "mc_cat_mod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModAttachments.init();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlockEntities();
		ModItems.registerModItems();
		CatBuffs.registerCatBuffs();

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!(entity instanceof Cat cat) || !cat.isTame() || !cat.isOwnedBy(player)) {
				return InteractionResult.PASS;
			}
			if (!player.getItemInHand(hand).isEmpty()) {
				return InteractionResult.PASS;
			}
			if (player.isSpectator()) {
				return InteractionResult.PASS;
			}
			if (world.isClientSide()) {
				return InteractionResult.SUCCESS;
			}
			cat.setOrderedToSit(!cat.isOrderedToSit());
			cat.setLying(false);
			player.swing(hand);
			return InteractionResult.SUCCESS;
		});

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> {
			content.accept(ModItems.CAT_ARMOR);
		});

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(content -> {
			content.accept(ModItems.CATNIP);
		});

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> {
			content.accept(ModBlocks.CAT_BED);
		});
	}
}