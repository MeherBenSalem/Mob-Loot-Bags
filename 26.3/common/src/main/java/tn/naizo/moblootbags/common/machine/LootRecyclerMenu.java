package tn.naizo.moblootbags.common.machine;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import tn.naizo.moblootbags.common.content.ModContent;

public class LootRecyclerMenu extends MachineMenu {
    public static final int BUTTON_COLLECT = 0;

    public LootRecyclerMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(LootRecyclerBlockEntity.INPUTS + LootRecyclerBlockEntity.OUTPUTS) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return slot < LootRecyclerBlockEntity.INPUTS;
            }
        }, new SimpleContainerData(6));
    }

    public LootRecyclerMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(ModContent.LOOT_RECYCLER_MENU.get(), containerId, container, data, LootRecyclerBlockEntity.INPUTS);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addInputSlot(row * 3 + col, 8 + col * 18, 18 + row * 18);
            }
        }
        for (int i = 0; i < LootRecyclerBlockEntity.OUTPUTS; i++) {
            addOutputSlot(LootRecyclerBlockEntity.INPUTS + i, 116, 18 + i * 18);
        }
        addStandardInventorySlots(inventory, 8, 84);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == BUTTON_COLLECT && container instanceof LootRecyclerBlockEntity recycler && player instanceof ServerPlayer serverPlayer) {
            recycler.collectXp(serverPlayer);
            return true;
        }
        return false;
    }

    public int points() {
        return data.get(0);
    }

    public int pointsPerBag() {
        return Math.max(1, data.get(1));
    }

    public int storedXp() {
        return data.get(2) | (data.get(3) << 15);
    }

    public int progress() {
        return data.get(4);
    }

    public int ticksPerItem() {
        return Math.max(1, data.get(5));
    }
}
