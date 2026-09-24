package tn.naizo.moblootbags.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class LootBagOpenerBlockEntity extends RandomizableContainerBlockEntity {
    private static final int SIZE = 27;
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public LootBagOpenerBlockEntity(BlockPos pos, BlockState state) {
        super(MobLootBagsRegistry.LOOT_BAG_OPENER_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
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
            if (ItemStack.isSameItemSameComponents(existing, stack) && existing.getCount() < existing.getMaxStackSize()) {
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
