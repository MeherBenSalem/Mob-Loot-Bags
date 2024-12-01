package net.mcreator.moblootbags.procedures;

import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.moblootbags.network.MobLootBagsModVariables;
import net.mcreator.moblootbags.init.MobLootBagsModItems;

import java.util.function.Supplier;
import java.util.Map;

public class SelectRewardTwiProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player _player) {
			ItemStack _setstack = (entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(1)).getItem() : ItemStack.EMPTY).copy();
			_setstack.setCount(new Object() {
				public int getAmount(int sltid) {
					if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
						ItemStack stack = ((Slot) _slots.get(sltid)).getItem();
						if (stack != null)
							return stack.getCount();
					}
					return 0;
				}
			}.getAmount(1));
			ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
		}
		if (entity instanceof Player _player)
			_player.closeContainer();
		if (entity instanceof Player _player) {
			ItemStack _stktoremove = new ItemStack(MobLootBagsModItems.CUSTOM_LOOT_BAG.get());
			_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
		}
		{
			boolean _setval = true;
			entity.getCapability(MobLootBagsModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.rewardGiven = _setval;
				capability.syncPlayerVariables(entity);
			});
		}
	}
}
