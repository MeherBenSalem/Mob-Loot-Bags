package tn.naizo.moblootbags.procedures;

import tn.naizo.moblootbags.init.MobLootBagsModItems;
import tn.naizo.jauml.JaumlConfigLib;

import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

public class LootBagOpenerGiveToBlockProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		String loot_table_name = "";
		double lootTableChosen = 0;
		double slot_number = 0;
		slot_number = GiveFilledSlotNumberInBlockProcedure.execute(world, x, y, z);
		if (slot_number != 99) {
			if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.COMMONLOOTBAG.get()) {
				lootTableChosen = Mth.nextInt(RandomSource.create(), 0, (int) (JaumlConfigLib.getArrayLength("mlb", "loot_tables", "common_lt_name") - 1));
				loot_table_name = JaumlConfigLib.getArrayElement("mlb", "loot_tables", "common_lt_name", ((int) lootTableChosen));
			} else if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.UNCOMMONLOOTBAG.get()) {
				lootTableChosen = Mth.nextInt(RandomSource.create(), 0, (int) (JaumlConfigLib.getArrayLength("mlb", "loot_tables", "uncommon_lt_name") - 1));
				loot_table_name = JaumlConfigLib.getArrayElement("mlb", "loot_tables", "uncommon_lt_name", ((int) lootTableChosen));
			} else if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.RARELOOTBAG.get()) {
				lootTableChosen = Mth.nextInt(RandomSource.create(), 0, (int) (JaumlConfigLib.getArrayLength("mlb", "loot_tables", "rare_lt_name") - 1));
				loot_table_name = JaumlConfigLib.getArrayElement("mlb", "loot_tables", "rare_lt_name", ((int) lootTableChosen));
			} else if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.EPICLOOTBAG.get()) {
				lootTableChosen = Mth.nextInt(RandomSource.create(), 0, (int) (JaumlConfigLib.getArrayLength("mlb", "loot_tables", "epic_lt_name") - 1));
				loot_table_name = JaumlConfigLib.getArrayElement("mlb", "loot_tables", "epic_lt_name", ((int) lootTableChosen));
			} else if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.LEGENDARYLOOTBAG.get()) {
				lootTableChosen = Mth.nextInt(RandomSource.create(), 0, (int) (JaumlConfigLib.getArrayLength("mlb", "loot_tables", "legendary_lt_name") - 1));
				loot_table_name = JaumlConfigLib.getArrayElement("mlb", "loot_tables", "legendary_lt_name", ((int) lootTableChosen));
			} else if ((itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) slot_number).copy()).getItem() == MobLootBagsModItems.SUMMONING_LOOTBAGS.get()) {
				loot_table_name = JaumlConfigLib.getStringValue("mlb", "loot_tables", "summoning_lt_name");
			} else {
				return;
			}
			if ((loot_table_name).equals("")) {
				loot_table_name = JaumlConfigLib.getStringValue("mlb", "loot_tables", "default_lt_name");
			}
			if ((world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == Blocks.CHEST || (world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == Blocks.HOPPER) {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, (y + 1), z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot insert ~ ~ ~ loot " + loot_table_name));
			} else if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.CHEST || (world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.HOPPER) {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, (y - 1), z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot insert ~ ~ ~ loot " + loot_table_name));
			} else if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.CHEST || (world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.HOPPER) {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3((x + 1), y, z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot insert ~ ~ ~ loot " + loot_table_name));
			} else if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.CHEST || (world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.HOPPER) {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3((x - 1), y, z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot insert ~ ~ ~ loot " + loot_table_name));
			} else if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.CHEST || (world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.HOPPER) {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, (z + 1)), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot insert ~ ~ ~ loot " + loot_table_name));
			} else if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.CHEST || (world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.HOPPER) {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, (z - 1)), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot insert ~ ~ ~ loot " + loot_table_name));
			} else {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, (y + 1), z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot spawn ~ ~ ~ loot " + loot_table_name));
			}
			if (world instanceof ILevelExtension _ext && _ext.getCapability(Capabilities.ItemHandler.BLOCK, BlockPos.containing(x, y, z), null) instanceof IItemHandlerModifiable _itemHandlerModifiable) {
				int _slotid = (int) slot_number;
				ItemStack _stk = _itemHandlerModifiable.getStackInSlot(_slotid).copy();
				_stk.shrink(1);
				_itemHandlerModifiable.setStackInSlot(_slotid, _stk);
			}
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
}