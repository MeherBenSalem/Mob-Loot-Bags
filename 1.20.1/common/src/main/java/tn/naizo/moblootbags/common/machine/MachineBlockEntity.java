package tn.naizo.moblootbags.common.machine;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared container plumbing for the machines: input slots {@code [0, inputs)} accept items from every side but the
 * bottom, output slots {@code [inputs, size)} are taken from the bottom (hoppers, pipes).
 *
 * <p>Not a {@code BaseContainerBlockEntity}: Forge gives that class one unsided item handler for every face, which
 * would let pipes pull bags out of the inputs. Its custom name and container lock are handled here instead.
 */
public abstract class MachineBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, Nameable {
    private final int inputs;
    private final int[] inputSlots;
    private final int[] outputSlots;
    protected NonNullList<ItemStack> items;
    private LockCode lockKey = LockCode.NO_LOCK;
    @Nullable
    private Component name;

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

    protected abstract Component getDefaultName();

    protected abstract AbstractContainerMenu createMenu(int containerId, Inventory inventory);

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        lockKey = LockCode.fromTag(tag);
        name = tag.contains("CustomName", Tag.TAG_STRING) ? Component.Serializer.fromJson(tag.getString("CustomName")) : null;
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        lockKey.addToTag(tag);
        if (name != null) {
            tag.putString("CustomName", Component.Serializer.toJson(name));
        }
        ContainerHelper.saveAllItems(tag, items);
    }

    /** Called by {@link MachineBlock#onRemove} when the block is broken: drops the contents. */
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            Containers.dropContents(level, pos, this);
        }
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) {
            setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
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

    public void setCustomName(@Nullable Component name) {
        this.name = name;
    }

    @Override
    public Component getName() {
        return name != null ? name : getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return name;
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return BaseContainerBlockEntity.canUnlock(player, lockKey, getDisplayName()) ? createMenu(containerId, inventory) : null;
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
            if (ItemStack.isSameItemSameTags(existing, remaining)) {
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
            if (existing.isEmpty() || (ItemStack.isSameItemSameTags(existing, stack)
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
