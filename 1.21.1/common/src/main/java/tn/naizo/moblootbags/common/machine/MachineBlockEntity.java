package tn.naizo.moblootbags.common.machine;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Shared container plumbing for the machines: input slots {@code [0, inputs)} accept items from every side but the
 * bottom, output slots {@code [inputs, size)} are taken from the bottom (hoppers, pipes).
 */
public abstract class MachineBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    private final int inputs;
    private final int[] inputSlots;
    private final int[] outputSlots;
    protected NonNullList<ItemStack> items;

    protected MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inputs, int outputs) {
        super(type, pos, state);
        this.inputs = inputs;
        this.items = NonNullList.withSize(inputs + outputs, ItemStack.EMPTY);
        this.inputSlots = new int[inputs];
        Arrays.setAll(inputSlots, i -> i);
        this.outputSlots = new int[outputs];
        Arrays.setAll(outputSlots, i -> inputs + i);
    }

    public abstract void serverTick(ServerLevel level, BlockPos pos, BlockState state);

    public int inputCount() {
        return inputs;
    }

    public boolean isInput(int slot) {
        return slot < inputs;
    }

    /** Whether {@code stack} may go into an input slot. */
    public abstract boolean acceptsInput(ItemStack stack);

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    /**
     * Drops the contents when the block is broken or replaced. 1.21.1 block entities have no removal hook of their own,
     * so {@link MachineBlock#onRemove} calls this; machines holding more than their slots drop that too.
     */
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            Containers.dropContents(level, pos, this);
        }
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return isInput(slot) && acceptsInput(stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? outputSlots : inputSlots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return side != Direction.DOWN && canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return side == Direction.DOWN && !isInput(slot);
    }

    /** Moves as much of {@code stack} as fits into the output slots; returns what is left. */
    protected ItemStack insertOutput(ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (int slot : outputSlots) {
            if (remaining.isEmpty()) {
                break;
            }
            ItemStack existing = items.get(slot);
            if (existing.isEmpty()) {
                items.set(slot, remaining);
                return ItemStack.EMPTY;
            }
            if (ItemStack.isSameItemSameComponents(existing, remaining)) {
                int move = Math.min(remaining.getCount(), existing.getMaxStackSize() - existing.getCount());
                if (move > 0) {
                    existing.grow(move);
                    remaining.shrink(move);
                }
            }
        }
        return remaining;
    }

    protected int freeOutputSlots() {
        int free = 0;
        for (int slot : outputSlots) {
            if (items.get(slot).isEmpty()) {
                free++;
            }
        }
        return free;
    }

    protected boolean outputAccepts(ItemStack stack) {
        for (int slot : outputSlots) {
            ItemStack existing = items.get(slot);
            if (existing.isEmpty() || (ItemStack.isSameItemSameComponents(existing, stack)
                    && existing.getCount() + stack.getCount() <= existing.getMaxStackSize())) {
                return true;
            }
        }
        return false;
    }

    protected void setActive(ServerLevel level, BlockPos pos, BlockState state, boolean active) {
        if (state.getValue(MachineBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(MachineBlock.ACTIVE, active), 3);
        }
    }
}
