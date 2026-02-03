package com.example.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.feline.Cat;
import java.util.EnumSet;

public class TuxedoLocateTreasureGoal extends Goal {
    private final Cat cat;
    private long memoryExpiryTime = -1;
    private long MCCatMod$lastSearchAttemptTime = -1;
    private static final int MCCatMod$SEARCH_RADIUS_CHUNKS = 4; // 4 chunks = 64 blocks
    private static final int MCCatMod$MAX_DISTANCE_BLOCKS = 64;
    // In 1.21.11, the "tuxedo" cat variant id is `minecraft:black` (while `minecraft:all_black` is the solid black cat).
    private static final String MCCatMod$TUXEDO_VARIANT_ID = "minecraft:black";

    public TuxedoLocateTreasureGoal(Cat cat) {
        this.cat = cat;
        // Passive ability: just locate + notify. No movement overrides needed.
        this.setFlags(EnumSet.noneOf(Goal.Flag.class));
    }

    @Override
    public boolean canUse() {
        String variantId = cat.getVariant().unwrapKey().map(k -> k.identifier().toString()).orElse("");
        return cat.isTame() && variantId.equals(MCCatMod$TUXEDO_VARIANT_ID);
    }

    @Override
    public void tick() {
        if (!(cat.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        long currentTime = cat.level().getGameTime();

        if (currentTime >= this.memoryExpiryTime) {
            if (this.MCCatMod$lastSearchAttemptTime == -1 || currentTime - this.MCCatMod$lastSearchAttemptTime >= 200) {
                this.MCCatMod$lastSearchAttemptTime = currentTime;

                ServerPlayer owner = this.cat.getOwner() instanceof ServerPlayer p ? p : null;
                if (owner == null) {
                    return;
                }

                // "Runs /locate structure buried_treasure": use the server's structure locator around the player.
                BlockPos nearest = serverLevel.findNearestMapStructure(
                    StructureTags.ON_TREASURE_MAPS,
                    owner.blockPosition(),
                    MCCatMod$SEARCH_RADIUS_CHUNKS,
                    false
                );

                if (nearest != null) {
                    int dx = nearest.getX() - owner.getBlockX();
                    int dz = nearest.getZ() - owner.getBlockZ();
                    int max = MCCatMod$MAX_DISTANCE_BLOCKS;
                    boolean within = (dx * dx + dz * dz) <= (max * max);

                    if (!within) {
                        return;
                    }

                    this.memoryExpiryTime = currentTime + 24000;

                    String msg = this.cat.getName().getString()
                        + " found buried treasure at "
                        + nearest.getX()
                        + " ~ "
                        + nearest.getZ();
                    owner.sendSystemMessage(Component.literal(msg));
                    this.cat.playSound(SoundEvents.CAT_BEG_FOR_FOOD, 0.4f, 1.0f);

                    // Cat emits electric sparks when it successfully finds treasure nearby.
                    serverLevel.sendParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        this.cat.getX(),
                        this.cat.getY() + 0.8,
                        this.cat.getZ(),
                        12,
                        0.3,
                        0.3,
                        0.3,
                        0.0
                    );
                }
            }
        }

        // Passive ability: no per-tick behavior after notifying.
    }

    @Override
    public void stop() {
        // Keep memoryExpiryTime so it "remembers" even if goal stops
    }
}