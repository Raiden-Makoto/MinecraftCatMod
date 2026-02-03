package com.example.mixin;

import com.example.CatMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class PersianFurnaceMixin {
    private static final String MCCatMod$PERSIAN_VARIANT_ID = "minecraft:persian";
    private static final List<String> MCCatMod$PERSIAN_LOG_FURNACE_POS_KEYS = new ArrayList<>();
    private static final List<Long> MCCatMod$PERSIAN_LOG_NEXT_ALLOWED_TICKS = new ArrayList<>();
    private static final List<UUID> MCCatMod$PERSIAN_MSG_PLAYER_IDS = new ArrayList<>();
    private static final List<Long> MCCatMod$PERSIAN_MSG_NEXT_ALLOWED_TICKS = new ArrayList<>();

    @Inject(method = "serverTick", at = @At("TAIL"))
    private static void MCCatMod$doublePersianCooking(ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        // 1. Search for a tamed Persian within 8 blocks
        AABB area = new AABB(pos).inflate(8.0);
        List<Cat> nearbyCats = level.getEntitiesOfClass(
            Cat.class,
            area,
            cat -> cat.isTame()
                && cat.getVariant().unwrapKey().map(k -> k.identifier().toString()).orElse("").equals(MCCatMod$PERSIAN_VARIANT_ID)
        );

        if (!nearbyCats.isEmpty()) {
            long now = level.getGameTime();
            String posKey = pos.getX() + "," + pos.getY() + "," + pos.getZ();
            int idx = -1;
            for (int i = 0; i < MCCatMod$PERSIAN_LOG_FURNACE_POS_KEYS.size(); i++) {
                if (MCCatMod$PERSIAN_LOG_FURNACE_POS_KEYS.get(i).equals(posKey)) {
                    idx = i;
                    break;
                }
            }
            if (idx == -1) {
                MCCatMod$PERSIAN_LOG_FURNACE_POS_KEYS.add(posKey);
                MCCatMod$PERSIAN_LOG_NEXT_ALLOWED_TICKS.add(0L);
                idx = MCCatMod$PERSIAN_LOG_FURNACE_POS_KEYS.size() - 1;
            }

            // 2. Ensure it's not a Villager Job Site
            // We check if any villager in the area is 'linked' to this specific furnace position
            boolean isJobSite = !level.getEntitiesOfClass(
                Villager.class,
                area,
                v -> {
                    try {
                        var jobSite = v.getBrain().getMemory(MemoryModuleType.JOB_SITE);
                        return jobSite.isPresent() && jobSite.get().pos().equals(pos);
                    } catch (IllegalStateException ignored) {
                        return false;
                    }
                }
            ).isEmpty();

            if (!isJobSite) {
                // 3. Apply the 2x boost (Add 1 extra progress per tick)
                AbstractFurnaceBlockEntityAccessor accessor = (AbstractFurnaceBlockEntityAccessor) blockEntity;
                int cookingTimer = accessor.MCCatMod$getCookingTimer();
                int totalTime = accessor.MCCatMod$getCookingTotalTime();

                // Vanilla only finishes cooking when (cookingTimer == cookingTotalTime) inside the tick.
                // If we ever bump it to == totalTime here, vanilla will increment to (totalTime + 1) next tick and NEVER craft.
                // So: never allow our boost to overshoot the completion tick, and also recover if we already overshot.
                if (totalTime > 0 && cookingTimer >= totalTime) {
                    int recovered = totalTime - 1;
                    if (recovered < 0) {
                        recovered = 0;
                    }
                    accessor.MCCatMod$setCookingTimer(recovered);
                    cookingTimer = recovered;

                    long nextAllowed = MCCatMod$PERSIAN_LOG_NEXT_ALLOWED_TICKS.get(idx);
                    if (now >= nextAllowed) {
                        MCCatMod$PERSIAN_LOG_NEXT_ALLOWED_TICKS.set(idx, now + 20L);
                        CatMod.LOGGER.info(
                            "[PERSIAN_FURNACE] recovered stuck timer at {}: cookingTimer clamped to {}/{} (catsNearby={})",
                            posKey,
                            cookingTimer,
                            totalTime,
                            nearbyCats.size()
                        );
                    }
                }

                if (totalTime > 0 && cookingTimer > 0 && cookingTimer < totalTime - 1) {
                    accessor.MCCatMod$setCookingTimer(cookingTimer + 1);

                    long nextAllowed = MCCatMod$PERSIAN_LOG_NEXT_ALLOWED_TICKS.get(idx);
                    if (now >= nextAllowed) {
                        MCCatMod$PERSIAN_LOG_NEXT_ALLOWED_TICKS.set(idx, now + 20L);
                        CatMod.LOGGER.info(
                            "[PERSIAN_FURNACE] boosted at {}: cookingTimer {}/{} -> {}/{} (catsNearby={})",
                            posKey,
                            cookingTimer,
                            totalTime,
                            cookingTimer + 1,
                            totalTime,
                            nearbyCats.size()
                        );
                    }

                    // Action-bar message to the owning player when the boost applies (throttled).
                    for (Cat cat : nearbyCats) {
                        if (cat.getOwner() instanceof Player player) {
                            UUID playerId = player.getUUID();
                            int pIdx = -1;
                            for (int i = 0; i < MCCatMod$PERSIAN_MSG_PLAYER_IDS.size(); i++) {
                                if (MCCatMod$PERSIAN_MSG_PLAYER_IDS.get(i).equals(playerId)) {
                                    pIdx = i;
                                    break;
                                }
                            }
                            if (pIdx == -1) {
                                MCCatMod$PERSIAN_MSG_PLAYER_IDS.add(playerId);
                                MCCatMod$PERSIAN_MSG_NEXT_ALLOWED_TICKS.add(0L);
                                pIdx = MCCatMod$PERSIAN_MSG_PLAYER_IDS.size() - 1;
                            }

                            long pNextAllowed = MCCatMod$PERSIAN_MSG_NEXT_ALLOWED_TICKS.get(pIdx);
                            if (now >= pNextAllowed) {
                                MCCatMod$PERSIAN_MSG_NEXT_ALLOWED_TICKS.set(pIdx, now + 20L);
                                player.displayClientMessage(
                                    Component.literal("Your Persian is speeding up the furnace!").withStyle(ChatFormatting.GOLD),
                                    true
                                );
                            }
                        }
                    }
                } else {
                    long nextAllowed = MCCatMod$PERSIAN_LOG_NEXT_ALLOWED_TICKS.get(idx);
                    if (now >= nextAllowed) {
                        MCCatMod$PERSIAN_LOG_NEXT_ALLOWED_TICKS.set(idx, now + 20L);
                        CatMod.LOGGER.info(
                            "[PERSIAN_FURNACE] no-boost at {}: cookingTimer={}, totalTime={}, catsNearby={}",
                            posKey,
                            cookingTimer,
                            totalTime,
                            nearbyCats.size()
                        );
                    }
                }
            } else {
                long nextAllowed = MCCatMod$PERSIAN_LOG_NEXT_ALLOWED_TICKS.get(idx);
                if (now >= nextAllowed) {
                    MCCatMod$PERSIAN_LOG_NEXT_ALLOWED_TICKS.set(idx, now + 20L);
                    CatMod.LOGGER.info(
                        "[PERSIAN_FURNACE] blocked by job site at {} (catsNearby={})",
                        posKey,
                        nearbyCats.size()
                    );
                }
            }
        }
    }
}