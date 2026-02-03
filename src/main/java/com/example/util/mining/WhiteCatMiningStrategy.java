package com.example.util.mining;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class WhiteCatMiningStrategy implements CatMiningStrategy {
    private static final double MCCatMod$RADIUS = 8.0;
    private static final float MCCatMod$CHANCE = 0.25f;
    private static final int MCCatMod$NIGHT_VISION_TICKS = 2400; // 2 minutes
    private static final List<TagKey<net.minecraft.world.level.block.Block>> MCCatMod$ORE_TAGS = List.of(
        BlockTags.COAL_ORES,
        BlockTags.COPPER_ORES,
        BlockTags.DIAMOND_ORES,
        BlockTags.EMERALD_ORES,
        BlockTags.GOLD_ORES,
        BlockTags.IRON_ORES,
        BlockTags.LAPIS_ORES,
        BlockTags.REDSTONE_ORES
    );

    @Override
    public String id() {
        return "minecraft:white";
    }

    @Override
    public void beforeBreak(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        boolean isOre = false;
        for (TagKey<net.minecraft.world.level.block.Block> tag : MCCatMod$ORE_TAGS) {
            if (state.is(tag)) {
                isOre = true;
                break;
            }
        }

        if (!isOre) {
            return;
        }

        AABB box = player.getBoundingBox().inflate(MCCatMod$RADIUS);
        List<Cat> cats = level.getEntitiesOfClass(
            Cat.class,
            box,
            cat -> cat.isTame()
                && cat.isOwnedBy(player)
                && cat.getVariant().unwrapKey().map(k -> k.identifier().toString()).orElse("").equals(this.id())
        );

        if (cats.isEmpty()) {
            return;
        }

        if (level.getRandom().nextFloat() < MCCatMod$CHANCE) {
            if (!player.hasEffect(MobEffects.NIGHT_VISION)) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, MCCatMod$NIGHT_VISION_TICKS, 0));
            }

            // Action-bar message (throttled per player) when the white cat proc triggers.
            long now = level.getGameTime();
            UUID playerId = player.getUUID();
            int idx = -1;
            for (int i = 0; i < MCCatMod$WHITE_MSG_PLAYER_IDS.size(); i++) {
                if (MCCatMod$WHITE_MSG_PLAYER_IDS.get(i).equals(playerId)) {
                    idx = i;
                    break;
                }
            }
            if (idx == -1) {
                MCCatMod$WHITE_MSG_PLAYER_IDS.add(playerId);
                MCCatMod$WHITE_MSG_NEXT_ALLOWED_TICKS.add(0L);
                idx = MCCatMod$WHITE_MSG_PLAYER_IDS.size() - 1;
            }

            long nextAllowed = MCCatMod$WHITE_MSG_NEXT_ALLOWED_TICKS.get(idx);
            if (now >= nextAllowed) {
                MCCatMod$WHITE_MSG_NEXT_ALLOWED_TICKS.set(idx, now + 20L);
                player.displayClientMessage(
                    Component.literal("Your White Cat has boosted your mining!").withStyle(ChatFormatting.GOLD),
                    true
                );
            }

            List<ItemStack> drops = Block.getDrops(state, level, pos, blockEntity, player, player.getMainHandItem());

            for (ItemStack stack : drops) {
                ItemEntity itemEntity = new ItemEntity(
                    level,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    stack.copy()
                );
                level.addFreshEntity(itemEntity);
            }

            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 1.5f);
        }
    }

    private static final List<UUID> MCCatMod$WHITE_MSG_PLAYER_IDS = new ArrayList<>();
    private static final List<Long> MCCatMod$WHITE_MSG_NEXT_ALLOWED_TICKS = new ArrayList<>();
}
