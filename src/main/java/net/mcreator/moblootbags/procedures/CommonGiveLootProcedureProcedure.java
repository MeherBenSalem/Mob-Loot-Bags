package net.mcreator.moblootbags.procedures;

import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.moblootbags.init.MobLootBagsModItems;
import net.mcreator.moblootbags.configuration.MainConfigFileConfiguration;

public class CommonGiveLootProcedureProcedure {
	public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		String loot_table_name = "";
		boolean itemGiven = false;
		if (itemstack.getItem() == MobLootBagsModItems.COMMONLOOTBAG.get()) {
			for (String stringiterator : MainConfigFileConfiguration.COMMON_LT_NAME.get()) {
				if (Mth.nextInt(RandomSource.create(), 1, 10) <= 5) {
					loot_table_name = stringiterator;
					break;
				}
			}
		} else if (itemstack.getItem() == MobLootBagsModItems.UNCOMMONLOOTBAG.get()) {
			for (String stringiterator : MainConfigFileConfiguration.UNCOMMON_LT_NAME.get()) {
				if (Mth.nextInt(RandomSource.create(), 1, 10) <= 5) {
					loot_table_name = stringiterator;
					break;
				}
			}
		} else if (itemstack.getItem() == MobLootBagsModItems.RARELOOTBAG.get()) {
			for (String stringiterator : MainConfigFileConfiguration.RARE_LT_NAME.get()) {
				if (Mth.nextInt(RandomSource.create(), 1, 10) <= 5) {
					loot_table_name = stringiterator;
					break;
				}
			}
		} else if (itemstack.getItem() == MobLootBagsModItems.EPICLOOTBAG.get()) {
			for (String stringiterator : MainConfigFileConfiguration.EPIC_LT_NAME.get()) {
				if (Mth.nextInt(RandomSource.create(), 1, 10) <= 5) {
					loot_table_name = stringiterator;
					break;
				}
			}
		} else if (itemstack.getItem() == MobLootBagsModItems.LEGENDARYLOOTBAG.get()) {
			for (String stringiterator : MainConfigFileConfiguration.LEGENDARY_LT_NAME.get()) {
				if (Mth.nextInt(RandomSource.create(), 1, 10) <= 5) {
					loot_table_name = stringiterator;
					break;
				}
			}
		}
		if ((loot_table_name).equals("")) {
			loot_table_name = SecurityProcedureProcedure.execute(itemstack);
		}
		itemGiven = false;
		if (!world.isClientSide() && world.getServer() != null) {
			for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(new ResourceLocation((loot_table_name).toLowerCase(java.util.Locale.ENGLISH)))
					.getRandomItems(new LootParams.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY))) {
				if (Mth.nextDouble(RandomSource.create(), 0, 100) <= 20) {
					if (itemstackiterator.isStackable()) {
						if (entity instanceof Player _player) {
							ItemStack _setstack = itemstackiterator.copy();
							_setstack.setCount(Mth.nextInt(RandomSource.create(), 1, (int) IsItemValuableProcedure.execute(itemstackiterator)));
							ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
						}
					} else {
						if (entity instanceof Player _player) {
							ItemStack _setstack = itemstackiterator.copy();
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
						}
					}
					itemGiven = true;
					break;
				}
			}
		}
		if (!itemGiven) {
			if (!world.isClientSide() && world.getServer() != null) {
				for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(new ResourceLocation((loot_table_name).toLowerCase(java.util.Locale.ENGLISH)))
						.getRandomItems(new LootParams.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY))) {
					if (itemstackiterator.isStackable()) {
						if (entity instanceof Player _player) {
							ItemStack _setstack = itemstackiterator.copy();
							_setstack.setCount(Mth.nextInt(RandomSource.create(), 1, (int) IsItemValuableProcedure.execute(itemstackiterator)));
							ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
						}
					} else {
						if (entity instanceof Player _player) {
							ItemStack _setstack = itemstackiterator.copy();
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
						}
					}
					break;
				}
			}
		}
		if (entity instanceof Player _player) {
			ItemStack _stktoremove = itemstack;
			_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
		}
	}
}
