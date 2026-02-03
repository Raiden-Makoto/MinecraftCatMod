package com.example.mixin;

import com.example.entity.CatArmorAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class CatArmorDamageMixin {
    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
    private float MCCatMod$applyArmorAndEnchantmentReductions(float amount, ServerLevel serverLevel, DamageSource source) {
        if (!((Object) this instanceof CatArmorAccessor accessor)) {
            return amount;
        }

        ItemStack armor = accessor.MCCatMod$getArmor();

        // If the cat isn't wearing armor, don't do anything
        if (armor.isEmpty()) {
            return amount;
        }

        float multiplier = 1.0f;
        float baseMultiplier = 1.0f;
        float envMultiplier = 1.0f;

        boolean isFire = source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.LAVA);
        boolean isBlast = source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION);

        // 1. Base mob/player damage (50% reduction)
        if (source.getEntity() != null) {
            baseMultiplier = 0.50f;
            multiplier *= baseMultiplier;
        }

        Registry<Enchantment> enchantments = serverLevel.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder.Reference<Enchantment> fireProtection =
            enchantments.get(Enchantments.FIRE_PROTECTION.identifier()).orElseThrow();
        Holder.Reference<Enchantment> blastProtection =
            enchantments.get(Enchantments.BLAST_PROTECTION.identifier()).orElseThrow();

        int fireProtLevel = EnchantmentHelper.getItemEnchantmentLevel(fireProtection, armor);
        float fireReduction = 0.25f + (fireProtLevel * 0.08f);
        float fireMultiplier = 1.0f;
        if (isFire) {
            fireMultiplier = (1.0f - fireReduction);
            multiplier *= fireMultiplier;
        }

        int blastProtLevel = EnchantmentHelper.getItemEnchantmentLevel(blastProtection, armor);
        float blastReduction = Math.min(0.32f, blastProtLevel * 0.08f);
        float blastMultiplier = 1.0f;
        if (isBlast) {
            blastMultiplier = (1.0f - blastReduction);
            multiplier *= blastMultiplier;
        }

        // 4. Environment damage (30% reduction)
        if (
            source.is(DamageTypes.CACTUS) ||
            source.is(DamageTypes.DROWN) ||
            source.is(DamageTypes.FREEZE) ||
            source.is(DamageTypes.IN_WALL) ||
            source.is(DamageTypes.SWEET_BERRY_BUSH) ||
            source.is(DamageTypes.STALAGMITE) ||
            source.is(DamageTypes.WITHER) ||
            source.is(DamageTypes.MAGIC)
        ) {
            envMultiplier = 0.70f;
            multiplier *= envMultiplier;
        }

        // 5. Unbreaking logic (handled by the engine) + durability loss for the armor stack
        if (multiplier < 1.0f) {
            LivingEntity self = (LivingEntity) (Object) this;

            // When the cat armor runs out of durability, clear it and play the vanilla item break sound.
            armor.hurtAndBreak(1, serverLevel, null, item -> {
                accessor.MCCatMod$setArmor(ItemStack.EMPTY);
                self.playSound(SoundEvents.ITEM_BREAK.value(), 1.0f, 1.0f);
            });
        }

        return amount * multiplier;
    }
}