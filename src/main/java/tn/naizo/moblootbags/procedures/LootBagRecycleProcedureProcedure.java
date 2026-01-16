package tn.naizo.moblootbags.procedures;

import tn.naizo.moblootbags.init.MobLootBagsModItems;
import tn.naizo.jauml.JaumlConfigLib;

import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

public class LootBagRecycleProcedureProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, double slot) {
		double slot_number = 0;
		double addedExp = 0;
		slot_number = slot;
		if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.COMMONLOOTBAG.get()) {
			addedExp = JaumlConfigLib.getNumberValue("mlb", "bag_recycler", "common_lb");
		} else if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.UNCOMMONLOOTBAG.get()) {
			addedExp = JaumlConfigLib.getNumberValue("mlb", "bag_recycler", "uncommon_lb");
		} else if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.RARELOOTBAG.get()) {
			addedExp = JaumlConfigLib.getNumberValue("mlb", "bag_recycler", "rare_lb");
		} else if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.EPICLOOTBAG.get()) {
			addedExp = JaumlConfigLib.getNumberValue("mlb", "bag_recycler", "epic_lb");
		} else if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.LEGENDARYLOOTBAG.get()) {
			addedExp = JaumlConfigLib.getNumberValue("mlb", "bag_recycler", "legendary_lb");
		} else if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.CURSED_LOOTBAG.get()) {
			addedExp = JaumlConfigLib.getNumberValue("mlb", "bag_recycler", "cursed_lb");
		} else if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.TIMED_LOOT_BAG.get()) {
			if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING)) != 0) {
				addedExp = JaumlConfigLib.getNumberValue("mlb", "bag_recycler", "enchanted_timed_lb");
			} else {
				addedExp = JaumlConfigLib.getNumberValue("mlb", "bag_recycler", "timed_lb");
			}
		} else if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.LOCKED_LOOTBAGS.get()) {
			addedExp = JaumlConfigLib.getNumberValue("mlb", "bag_recycler", "locked_lb");
		} else if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.SUMMONING_LOOTBAGS.get()) {
			addedExp = JaumlConfigLib.getNumberValue("mlb", "bag_recycler", "summoning_lb");
		} else {
			return;
		}
		if (!world.isClientSide()) {
			BlockPos _bp = BlockPos.containing(x, y, z);
			BlockEntity _blockEntity = world.getBlockEntity(_bp);
			BlockState _bs = world.getBlockState(_bp);
			if (_blockEntity != null) {
				_blockEntity.getPersistentData().putDouble("lootbags_blockxp", (getBlockNBTNumber(world, BlockPos.containing(x, y, z), "lootbags_blockxp") + addedExp));
			}
			if (world instanceof Level _level)
				_level.sendBlockUpdated(_bp, _bs, _bs, 3);
		}
		if (world instanceof ILevelExtension _ext && _ext.getCapability(Capabilities.ItemHandler.BLOCK, BlockPos.containing(x, y, z), null) instanceof IItemHandlerModifiable _itemHandlerModifiable) {
			int _slotid = (int) slot_number;
			ItemStack _stk = _itemHandlerModifiable.getStackInSlot(_slotid).copy();
			_stk.shrink(1);
			_itemHandlerModifiable.setStackInSlot(_slotid, _stk);
		}
	}

	private static ItemStack itemFromBlockInventory(LevelAccessor world, BlockPos pos, int slot) {
		if (world instanceof ILevelExtension ext) {
			IItemHandler itemHandler = ext.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
			if (itemHandler != null)
				return itemHandler.getStackInSlot(slot);
		}
		return ItemStack.EMPTY;
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDoubleOr(tag, 0);
		return -1;
	}
}