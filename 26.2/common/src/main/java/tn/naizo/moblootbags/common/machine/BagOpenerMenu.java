package tn.naizo.moblootbags.common.machine;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import tn.naizo.moblootbags.common.content.ModContent;

public class BagOpenerMenu extends MachineMenu {
    public static final int BUTTON_MODE = 0;

    public BagOpenerMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(BagOpenerBlockEntity.INPUTS + BagOpenerBlockEntity.OUTPUTS) {
            @Override
            public boolean canPlaceItem(int slot, net.minecraft.world.item.ItemStack stack) {
                return slot < BagOpenerBlockEntity.INPUTS && BagOpenerBlockEntity.canOpen(stack);
            }
        }, new SimpleContainerData(4));
    }

    public BagOpenerMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(ModContent.BAG_OPENER_MENU.get(), containerId, container, data, BagOpenerBlockEntity.INPUTS);
        for (int i = 0; i < BagOpenerBlockEntity.INPUTS; i++) {
            addInputSlot(i, 8 + i * 18, 18);
        }
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 9; col++) {
                addOutputSlot(BagOpenerBlockEntity.INPUTS + row * 9 + col, 8 + col * 18, 50 + row * 18);
            }
        }
        addStandardInventorySlots(inventory, 8, 102);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == BUTTON_MODE) {
            data.set(2, data.get(2) + 1);
            return true;
        }
        return false;
    }

    public int progress() {
        return data.get(0);
    }

    public int ticksPerBag() {
        return Math.max(1, data.get(1));
    }

    public int mode() {
        return data.get(2);
    }

    public int status() {
        return data.get(3);
    }
}
