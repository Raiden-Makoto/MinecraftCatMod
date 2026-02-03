package com.example.registry;

import com.example.CatMod;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public final class ModAttachments {
    private static final Codec<Optional<BlockPos>> CAT_BED_CODEC = Codec.list(Codec.INT).xmap(
        list -> list.size() == 3 ? Optional.of(new BlockPos(list.get(0), list.get(1), list.get(2))) : Optional.empty(),
        opt -> opt.map(pos -> List.of(pos.getX(), pos.getY(), pos.getZ())).orElse(List.of())
    );

    public static final net.fabricmc.fabric.api.attachment.v1.AttachmentType<Optional<BlockPos>> CAT_BED =
        AttachmentRegistry.createPersistent(
            Identifier.fromNamespaceAndPath(CatMod.MOD_ID, "cat_bed"),
            CAT_BED_CODEC
        );

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
