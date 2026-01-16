package tn.naizo.moblootbags.procedures;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;

public class GiveFilledSlotNumberInBlockProcedure {
	public static double execute(LevelAccessor world, double x, double y, double z) {
		double emptySlotId = 0;
		double itterator = 0;
		double FilledSlot = 0;
		FilledSlot = 99;
		itterator = 0;
		while (itterator <= 23) {
			if (itemFromBlockInventory(world, BlockPos.containing(x, y, z), (int) itterator).getCount() > 0) {
				FilledSlot = itterator;
				break;
			}
			itterator = itterator + 1;
		}
		return FilledSlot;
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