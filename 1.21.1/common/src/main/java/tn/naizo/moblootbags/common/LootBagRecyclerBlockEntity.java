package tn.naizo.moblootbags.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class LootBagRecyclerBlockEntity extends RandomizableContainerBlockEntity {
    private static final int SIZE = 27;
    private static final String XP_TAG = "lootbags_blockxp";

    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private int storedXp;

    public LootBagRecyclerBlockEntity(BlockPos pos, BlockState state) {
        super(MobLootBagsRegistry.LOOT_BAG_RECYCLE_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.storedXp = Math.max(0, tag.getInt(XP_TAG));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.putInt(XP_TAG, this.storedXp);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    protected Component getDefaultName() {
        return Component.literal("Loot Bag Recycler");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return ChestMenu.threeRows(containerId, inventory, this);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return MobLootBagsLogic.getRecycleXpValue(stack) > 0;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack value = stack;
        if (this.level != null && !this.level.isClientSide && !stack.isEmpty()) {
            int xpValue = MobLootBagsLogic.getRecycleXpValue(stack);
            if (xpValue > 0) {
                value = stack.copy();
                value.shrink(1);
                this.storedXp += xpValue;
            }
        }
        super.setItem(slot, value);
    }

    public void addStoredXp(int xp) {
        if (xp <= 0) {
            return;
        }
        this.storedXp += xp;
        this.setChanged();
    }

    public int getStoredXp() {
        return this.storedXp;
    }

    public int takeStoredXp() {
        int current = this.storedXp;
        this.storedXp = 0;
        this.setChanged();
        return current;
    }
}
