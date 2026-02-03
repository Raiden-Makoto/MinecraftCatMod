package com.example.util.loot;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class SiameseLootContext {
    private SiameseLootContext() {
    }

    private static LivingEntity MCCatMod$victim;
    private static Player MCCatMod$killer;
    private static DamageSource MCCatMod$source;

    public static void MCCatMod$set(LivingEntity victim, Player killer, DamageSource source) {
        MCCatMod$victim = victim;
        MCCatMod$killer = killer;
        MCCatMod$source = source;
    }

    public static void MCCatMod$clear() {
        MCCatMod$victim = null;
        MCCatMod$killer = null;
        MCCatMod$source = null;
    }

    public static LivingEntity MCCatMod$getVictim() {
        return MCCatMod$victim;
    }

    public static Player MCCatMod$getKiller() {
        return MCCatMod$killer;
    }

    public static DamageSource MCCatMod$getSource() {
        return MCCatMod$source;
    }
}
