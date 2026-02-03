package com.example.block.entity;

import com.example.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.feline.Cat;

import java.util.UUID;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public final class CatBedBlockEntity extends BlockEntity implements MenuProvider, Container {
    private static final int MCCatMod$SIZE = 27;
    private static final String MCCatMod$OCCUPYING_CAT_KEY = "OccupyingCat";
    private NonNullList<ItemStack> items = NonNullList.withSize(MCCatMod$SIZE, ItemStack.EMPTY);
    private UUID MCCatMod$occupyingCatId;

    public boolean MCCatMod$isClaimed() {
        return MCCatMod$occupyingCatId != null;
    }

    public boolean MCCatMod$isClaimedBy(Cat cat) {
        return MCCatMod$occupyingCatId != null && MCCatMod$occupyingCatId.equals(cat.getUUID());
    }

    public boolean MCCatMod$canBeClaimedBy(Cat cat) {
        return !MCCatMod$isClaimed() || MCCatMod$isClaimedBy(cat);
    }

    public void MCCatMod$claim(Cat cat) {
        MCCatMod$occupyingCatId = cat.getUUID();
        setChanged();
    }

    public void MCCatMod$release() {
        MCCatMod$occupyingCatId = null;
        setChanged();
    }

    @Nullable
    public Cat MCCatMod$getOccupyingCat() {
        if (MCCatMod$occupyingCatId == null || level == null) {
            return null;
        }
        net.minecraft.world.phys.AABB box = new net.minecraft.world.phys.AABB(worldPosition).inflate(2.0);
        for (Cat cat : level.getEntitiesOfClass(Cat.class, box)) {
            if (cat.getUUID().equals(MCCatMod$occupyingCatId)) {
                return cat;
            }
        }
        return null;
    }

    public CatBedBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CAT_BED, pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Cat Bed");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return ChestMenu.threeRows(syncId, inventory, this);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.items = NonNullList.withSize(MCCatMod$SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(valueInput, this.items);
        this.MCCatMod$occupyingCatId = valueInput.getString(MCCatMod$OCCUPYING_CAT_KEY)
            .filter(s -> !s.isEmpty())
            .map(s -> {
                try {
                    return UUID.fromString(s);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            })
            .orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        ContainerHelper.saveAllItems(valueOutput, this.items);
        if (MCCatMod$occupyingCatId != null) {
            valueOutput.putString(MCCatMod$OCCUPYING_CAT_KEY, MCCatMod$occupyingCatId.toString());
        }
    }

    @Override
    public int getContainerSize() {
        return MCCatMod$SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack split = ContainerHelper.removeItem(this.items, slot, amount);
        if (!split.isEmpty()) {
            this.setChanged();
        }
        return split;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level == null) {
            return false;
        }
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(
            this.worldPosition.getX() + 0.5,
            this.worldPosition.getY() + 0.5,
            this.worldPosition.getZ() + 0.5
        ) <= 64.0;
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }
}

