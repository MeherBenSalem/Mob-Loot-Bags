package net.mcreator.moblootbags.procedures;

import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

import java.util.concurrent.atomic.AtomicInteger;

public class GiveEmptySlotNumberInBlockProcedure {
	public static double execute(LevelAccessor world, double x, double y, double z) {
		double emptySlotId = 0;
		double itterator = 0;
		emptySlotId = 99;
		itterator = 9;
		while (itterator <= 17) {
			if (new Object() {
				public int getAmount(LevelAccessor world, BlockPos pos, int slotid) {
					AtomicInteger _retval = new AtomicInteger(0);
					BlockEntity _ent = world.getBlockEntity(pos);
					if (_ent != null)
						_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> _retval.set(capability.getStackInSlot(slotid).getCount()));
					return _retval.get();
				}
			}.getAmount(world, BlockPos.containing(x, y, z), (int) itterator) <= 0) {
				emptySlotId = itterator;
				break;
			}
			itterator = itterator + 1;
		}
		return emptySlotId;
	}
}
