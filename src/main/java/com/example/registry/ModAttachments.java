package com.example.registry;

import com.example.CatMod;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public final class ModAttachments {
    public static final net.fabricmc.fabric.api.attachment.v1.AttachmentType<Boolean> RESTED =
        AttachmentRegistry.createPersistent(
            Identifier.fromNamespaceAndPath(CatMod.MOD_ID, "rested"),
            Codec.BOOL
        );

    public static final net.fabricmc.fabric.api.attachment.v1.AttachmentType<ItemStack> ARMOR =
        AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(CatMod.MOD_ID, "armor"),
            builder -> builder
                .initializer(() -> ItemStack.EMPTY)
                .persistent(ItemStack.STRICT_CODEC)
                .syncWith(ItemStack.STREAM_CODEC, AttachmentSyncPredicate.all())
        );

    public static void init() {
    }

    private ModAttachments() {
    }
}
