package tn.naizo.moblootbags.common.machine;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Machine menu: input slots, output slots, then the player inventory. Shift-click routes items sensibly. */
public abstract class MachineMenu extends AbstractContainerMenu {
    protected final Container container;
    protected final ContainerData data;
    private final int inputs;
    private final int machineSlots;

    protected MachineMenu(MenuType<?> type, int containerId, Container container, ContainerData data, int inputs) {
        super(type, containerId);
        checkContainerSize(container, container.getContainerSize());
        checkContainerDataCount(data, data.getCount());
        this.container = container;
        this.data = data;
        this.inputs = inputs;
        this.machineSlots = container.getContainerSize();
        addDataSlots(data);
    }

    protected void addInputSlot(int index, int x, int y) {
        addSlot(new Slot(container, index, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return container.canPlaceItem(index, stack);
            }
        });
    }

    protected void addOutputSlot(int index, int x, int y) {
        addSlot(new Slot(container, index, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (slotIndex < machineSlots) {
            if (!moveItemStackTo(stack, machineSlots, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (container.canPlaceItem(0, stack)) {
            if (!moveItemStackTo(stack, 0, inputs, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }
}
