
package net.mcreator.moblootbags.world.inventory;

import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.mcreator.moblootbags.network.LootbagRecyleBlockGUISlotMessage;
import net.mcreator.moblootbags.init.MobLootBagsModMenus;
import net.mcreator.moblootbags.MobLootBagsMod;

import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;

public class LootbagRecyleBlockGUIMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
	public final static HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final Player entity;
	public int x, y, z;
	private ContainerLevelAccess access = ContainerLevelAccess.NULL;
	private IItemHandler internal;
	private final Map<Integer, Slot> customSlots = new HashMap<>();
	private boolean bound = false;
	private Supplier<Boolean> boundItemMatcher = null;
	private Entity boundEntity = null;
	private BlockEntity boundBlockEntity = null;

	public LootbagRecyleBlockGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(MobLootBagsModMenus.LOOTBAG_RECYLE_BLOCK_GUI.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.internal = new ItemStackHandler(27);
		BlockPos pos = null;
		if (extraData != null) {
			pos = extraData.readBlockPos();
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			access = ContainerLevelAccess.create(world, pos);
		}
		if (pos != null) {
			if (extraData.readableBytes() == 1) { // bound to item
				byte hand = extraData.readByte();
				ItemStack itemstack = hand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem();
				this.boundItemMatcher = () -> itemstack == (hand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem());
				itemstack.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
					this.internal = capability;
					this.bound = true;
				});
			} else if (extraData.readableBytes() > 1) { // bound to entity
				extraData.readByte(); // drop padding
				boundEntity = world.getEntity(extraData.readVarInt());
				if (boundEntity != null)
					boundEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
						this.internal = capability;
						this.bound = true;
					});
			} else { // might be bound to block
				boundBlockEntity = this.world.getBlockEntity(pos);
				if (boundBlockEntity != null)
					boundBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
						this.internal = capability;
						this.bound = true;
					});
			}
		}
		this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 8, 11) {
			private final int slot = 0;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(0, 0, 0);
			}
		}));
		this.customSlots.put(1, this.addSlot(new SlotItemHandler(internal, 1, 26, 11) {
			private final int slot = 1;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(1, 0, 0);
			}
		}));
		this.customSlots.put(2, this.addSlot(new SlotItemHandler(internal, 2, 44, 11) {
			private final int slot = 2;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(2, 0, 0);
			}
		}));
		this.customSlots.put(3, this.addSlot(new SlotItemHandler(internal, 3, 62, 11) {
			private final int slot = 3;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(3, 0, 0);
			}
		}));
		this.customSlots.put(4, this.addSlot(new SlotItemHandler(internal, 4, 80, 11) {
			private final int slot = 4;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(4, 0, 0);
			}
		}));
		this.customSlots.put(5, this.addSlot(new SlotItemHandler(internal, 5, 98, 11) {
			private final int slot = 5;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(5, 0, 0);
			}
		}));
		this.customSlots.put(6, this.addSlot(new SlotItemHandler(internal, 6, 116, 11) {
			private final int slot = 6;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(6, 0, 0);
			}
		}));
		this.customSlots.put(7, this.addSlot(new SlotItemHandler(internal, 7, 134, 11) {
			private final int slot = 7;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(7, 0, 0);
			}
		}));
		this.customSlots.put(8, this.addSlot(new SlotItemHandler(internal, 8, 152, 11) {
			private final int slot = 8;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(8, 0, 0);
			}
		}));
		this.customSlots.put(9, this.addSlot(new SlotItemHandler(internal, 9, 8, 29) {
			private final int slot = 9;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(9, 0, 0);
			}
		}));
		this.customSlots.put(10, this.addSlot(new SlotItemHandler(internal, 10, 26, 29) {
			private final int slot = 10;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(10, 0, 0);
			}
		}));
		this.customSlots.put(11, this.addSlot(new SlotItemHandler(internal, 11, 44, 29) {
			private final int slot = 11;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(11, 0, 0);
			}
		}));
		this.customSlots.put(12, this.addSlot(new SlotItemHandler(internal, 12, 62, 29) {
			private final int slot = 12;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(12, 0, 0);
			}
		}));
		this.customSlots.put(13, this.addSlot(new SlotItemHandler(internal, 13, 80, 29) {
			private final int slot = 13;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(13, 0, 0);
			}
		}));
		this.customSlots.put(14, this.addSlot(new SlotItemHandler(internal, 14, 98, 29) {
			private final int slot = 14;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(14, 0, 0);
			}
		}));
		this.customSlots.put(15, this.addSlot(new SlotItemHandler(internal, 15, 116, 29) {
			private final int slot = 15;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(15, 0, 0);
			}
		}));
		this.customSlots.put(16, this.addSlot(new SlotItemHandler(internal, 16, 134, 29) {
			private final int slot = 16;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(16, 0, 0);
			}
		}));
		this.customSlots.put(17, this.addSlot(new SlotItemHandler(internal, 17, 152, 29) {
			private final int slot = 17;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(17, 0, 0);
			}
		}));
		this.customSlots.put(18, this.addSlot(new SlotItemHandler(internal, 18, 8, 47) {
			private final int slot = 18;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(18, 0, 0);
			}
		}));
		this.customSlots.put(19, this.addSlot(new SlotItemHandler(internal, 19, 26, 47) {
			private final int slot = 19;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(19, 0, 0);
			}
		}));
		this.customSlots.put(20, this.addSlot(new SlotItemHandler(internal, 20, 44, 47) {
			private final int slot = 20;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(20, 0, 0);
			}
		}));
		this.customSlots.put(21, this.addSlot(new SlotItemHandler(internal, 21, 62, 47) {
			private final int slot = 21;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(21, 0, 0);
			}
		}));
		this.customSlots.put(22, this.addSlot(new SlotItemHandler(internal, 22, 80, 47) {
			private final int slot = 22;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(22, 0, 0);
			}
		}));
		this.customSlots.put(23, this.addSlot(new SlotItemHandler(internal, 23, 98, 47) {
			private final int slot = 23;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(23, 0, 0);
			}
		}));
		this.customSlots.put(24, this.addSlot(new SlotItemHandler(internal, 24, 116, 47) {
			private final int slot = 24;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(24, 0, 0);
			}
		}));
		this.customSlots.put(25, this.addSlot(new SlotItemHandler(internal, 25, 134, 47) {
			private final int slot = 25;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(25, 0, 0);
			}
		}));
		this.customSlots.put(26, this.addSlot(new SlotItemHandler(internal, 26, 152, 47) {
			private final int slot = 26;

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(26, 0, 0);
			}
		}));
		for (int si = 0; si < 3; ++si)
			for (int sj = 0; sj < 9; ++sj)
				this.addSlot(new Slot(inv, sj + (si + 1) * 9, 0 + 8 + sj * 18, 0 + 84 + si * 18));
		for (int si = 0; si < 9; ++si)
			this.addSlot(new Slot(inv, si, 0 + 8 + si * 18, 0 + 142));
	}

	@Override
	public boolean stillValid(Player player) {
		if (this.bound) {
			if (this.boundItemMatcher != null)
				return this.boundItemMatcher.get();
			else if (this.boundBlockEntity != null)
				return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
			else if (this.boundEntity != null)
				return this.boundEntity.isAlive();
		}
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < 27) {
				if (!this.moveItemStackTo(itemstack1, 27, this.slots.size(), true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(itemstack1, itemstack);
			} else if (!this.moveItemStackTo(itemstack1, 0, 27, false)) {
				if (index < 27 + 27) {
					if (!this.moveItemStackTo(itemstack1, 27 + 27, this.slots.size(), true))
						return ItemStack.EMPTY;
				} else {
					if (!this.moveItemStackTo(itemstack1, 27, 27 + 27, false))
						return ItemStack.EMPTY;
				}
				return ItemStack.EMPTY;
			}
			if (itemstack1.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			if (itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(playerIn, itemstack1);
		}
		return itemstack;
	}

	@Override
	protected boolean moveItemStackTo(ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
		boolean flag = false;
		int i = p_38905_;
		if (p_38907_) {
			i = p_38906_ - 1;
		}
		if (p_38904_.isStackable()) {
			while (!p_38904_.isEmpty()) {
				if (p_38907_) {
					if (i < p_38905_) {
						break;
					}
				} else if (i >= p_38906_) {
					break;
				}
				Slot slot = this.slots.get(i);
				ItemStack itemstack = slot.getItem();
				if (slot.mayPlace(itemstack) && !itemstack.isEmpty() && ItemStack.isSameItemSameTags(p_38904_, itemstack)) {
					int j = itemstack.getCount() + p_38904_.getCount();
					int maxSize = Math.min(slot.getMaxStackSize(), p_38904_.getMaxStackSize());
					if (j <= maxSize) {
						p_38904_.setCount(0);
						itemstack.setCount(j);
						slot.set(itemstack);
						flag = true;
					} else if (itemstack.getCount() < maxSize) {
						p_38904_.shrink(maxSize - itemstack.getCount());
						itemstack.setCount(maxSize);
						slot.set(itemstack);
						flag = true;
					}
				}
				if (p_38907_) {
					--i;
				} else {
					++i;
				}
			}
		}
		if (!p_38904_.isEmpty()) {
			if (p_38907_) {
				i = p_38906_ - 1;
			} else {
				i = p_38905_;
			}
			while (true) {
				if (p_38907_) {
					if (i < p_38905_) {
						break;
					}
				} else if (i >= p_38906_) {
					break;
				}
				Slot slot1 = this.slots.get(i);
				ItemStack itemstack1 = slot1.getItem();
				if (itemstack1.isEmpty() && slot1.mayPlace(p_38904_)) {
					if (p_38904_.getCount() > slot1.getMaxStackSize()) {
						slot1.setByPlayer(p_38904_.split(slot1.getMaxStackSize()));
					} else {
						slot1.setByPlayer(p_38904_.split(p_38904_.getCount()));
					}
					slot1.setChanged();
					flag = true;
					break;
				}
				if (p_38907_) {
					--i;
				} else {
					++i;
				}
			}
		}
		return flag;
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		if (!bound && playerIn instanceof ServerPlayer serverPlayer) {
			if (!serverPlayer.isAlive() || serverPlayer.hasDisconnected()) {
				for (int j = 0; j < internal.getSlots(); ++j) {
					if (j == 0)
						continue;
					if (j == 1)
						continue;
					if (j == 2)
						continue;
					if (j == 3)
						continue;
					if (j == 4)
						continue;
					if (j == 5)
						continue;
					if (j == 6)
						continue;
					if (j == 7)
						continue;
					if (j == 8)
						continue;
					if (j == 9)
						continue;
					if (j == 10)
						continue;
					if (j == 11)
						continue;
					if (j == 12)
						continue;
					if (j == 13)
						continue;
					if (j == 14)
						continue;
					if (j == 15)
						continue;
					if (j == 16)
						continue;
					if (j == 17)
						continue;
					if (j == 18)
						continue;
					if (j == 19)
						continue;
					if (j == 20)
						continue;
					if (j == 21)
						continue;
					if (j == 22)
						continue;
					if (j == 23)
						continue;
					if (j == 24)
						continue;
					if (j == 25)
						continue;
					if (j == 26)
						continue;
					playerIn.drop(internal.extractItem(j, internal.getStackInSlot(j).getCount(), false), false);
				}
			} else {
				for (int i = 0; i < internal.getSlots(); ++i) {
					if (i == 0)
						continue;
					if (i == 1)
						continue;
					if (i == 2)
						continue;
					if (i == 3)
						continue;
					if (i == 4)
						continue;
					if (i == 5)
						continue;
					if (i == 6)
						continue;
					if (i == 7)
						continue;
					if (i == 8)
						continue;
					if (i == 9)
						continue;
					if (i == 10)
						continue;
					if (i == 11)
						continue;
					if (i == 12)
						continue;
					if (i == 13)
						continue;
					if (i == 14)
						continue;
					if (i == 15)
						continue;
					if (i == 16)
						continue;
					if (i == 17)
						continue;
					if (i == 18)
						continue;
					if (i == 19)
						continue;
					if (i == 20)
						continue;
					if (i == 21)
						continue;
					if (i == 22)
						continue;
					if (i == 23)
						continue;
					if (i == 24)
						continue;
					if (i == 25)
						continue;
					if (i == 26)
						continue;
					playerIn.getInventory().placeItemBackInInventory(internal.extractItem(i, internal.getStackInSlot(i).getCount(), false));
				}
			}
		}
	}

	private void slotChanged(int slotid, int ctype, int meta) {
		if (this.world != null && this.world.isClientSide()) {
			MobLootBagsMod.PACKET_HANDLER.sendToServer(new LootbagRecyleBlockGUISlotMessage(slotid, x, y, z, ctype, meta));
			LootbagRecyleBlockGUISlotMessage.handleSlotAction(entity, slotid, ctype, meta, x, y, z);
		}
	}

	public Map<Integer, Slot> get() {
		return customSlots;
	}
}
