package tn.naizo.moblootbags.common;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

public final class LootBagOpenerBlockEntity extends RandomizableContainerBlockEntity {
    private static final int SIZE = 27;
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public LootBagOpenerBlockEntity(BlockPos pos, BlockState state) {
        super(MobLootBagsRegistry.LOOT_BAG_OPENER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
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
        return Component.literal("Loot Bag Opener");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return ChestMenu.threeRows(containerId, inventory, this);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return MobLootBagsLogic.isOpenerSupportedBag(stack);
    }

    public boolean addBag(ItemStack stack) {
        if (!this.canPlaceItem(0, stack)) {
            return false;
        }

        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack existing = this.getItem(i);
            if (existing.isEmpty()) {
                this.setItem(i, stack.copy());
                this.setChanged();
                return true;
            }
            if (ItemStack.isSameItemSameTags(existing, stack) && existing.getCount() < existing.getMaxStackSize()) {
                existing.grow(1);
                this.setItem(i, existing);
                this.setChanged();
                return true;
            }
        }
        return false;
    }

    public void processNextBag(ServerLevel level, BlockPos pos) {
        if (MobLootBagsLogic.processNextBag(level, pos, this)) {
            this.setChanged();
        }
    }
}
