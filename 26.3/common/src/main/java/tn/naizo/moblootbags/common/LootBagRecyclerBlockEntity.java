package tn.naizo.moblootbags.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public final class LootBagRecyclerBlockEntity extends RandomizableContainerBlockEntity {
    private static final int SIZE = 27;
    private static final String XP_TAG = "lootbags_blockxp";

    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private int storedXp;

    public LootBagRecyclerBlockEntity(BlockPos pos, BlockState state) {
        super(MobLootBagsRegistry.LOOT_BAG_RECYCLE_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.storedXp = Math.max(0, input.getIntOr(XP_TAG, 0));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt(XP_TAG, this.storedXp);
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
        if (this.level != null && !this.level.isClientSide() && !stack.isEmpty()) {
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

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        int xp = this.takeStoredXp();
        if (xp > 0 && this.level instanceof ServerLevel serverLevel) {
            ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), xp);
        }
    }
}
