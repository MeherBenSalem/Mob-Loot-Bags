package tn.naizo.moblootbags.procedures;

import tn.naizo.moblootbags.init.MobLootBagsModItems;
import tn.naizo.moblootbags.configuration.MainConfigFileConfiguration;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

public class HandleNormalBagsLogicProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		String loot_table_name = "";
		double lootTableChosen = 0;
		double counter = 0;
		lootTableChosen = 0;
		counter = 1;
		if (itemstack.getItem() == MobLootBagsModItems.COMMONLOOTBAG.get()) {
			lootTableChosen = Mth.nextInt(RandomSource.create(), 1, (int) ReturnLengthOfCommunBagsProcedure.execute());
			for (String stringiterator : MainConfigFileConfiguration.COMMON_LT_NAME.get()) {
				if (counter == lootTableChosen) {
					loot_table_name = stringiterator;
					break;
				} else {
					counter = counter + 1;
				}
			}
		} else if (itemstack.getItem() == MobLootBagsModItems.UNCOMMONLOOTBAG.get()) {
			lootTableChosen = Mth.nextInt(RandomSource.create(), 1, (int) ReturnLengthOfUnCommunProcedure.execute());
			for (String stringiterator : MainConfigFileConfiguration.UNCOMMON_LT_NAME.get()) {
				if (counter == lootTableChosen) {
					loot_table_name = stringiterator;
					break;
				} else {
					counter = counter + 1;
				}
			}
		} else if (itemstack.getItem() == MobLootBagsModItems.RARELOOTBAG.get()) {
			lootTableChosen = Mth.nextInt(RandomSource.create(), 1, (int) ReturnLengthOfRareProcedure.execute());
			for (String stringiterator : MainConfigFileConfiguration.RARE_LT_NAME.get()) {
				if (counter == lootTableChosen) {
					loot_table_name = stringiterator;
					break;
				} else {
					counter = counter + 1;
				}
			}
		} else if (itemstack.getItem() == MobLootBagsModItems.EPICLOOTBAG.get()) {
			lootTableChosen = Mth.nextInt(RandomSource.create(), 1, (int) ReturnLengthOfEpicProcedure.execute());
			for (String stringiterator : MainConfigFileConfiguration.EPIC_LT_NAME.get()) {
				if (counter == lootTableChosen) {
					loot_table_name = stringiterator;
					break;
				} else {
					counter = counter + 1;
				}
			}
		} else if (itemstack.getItem() == MobLootBagsModItems.LEGENDARYLOOTBAG.get()) {
			lootTableChosen = Mth.nextInt(RandomSource.create(), 1, (int) ReturnLengthOfLegendaryProcedure.execute());
			for (String stringiterator : MainConfigFileConfiguration.LEGENDARY_LT_NAME.get()) {
				if (counter == lootTableChosen) {
					loot_table_name = stringiterator;
					break;
				} else {
					counter = counter + 1;
				}
			}
		} else if (itemstack.getItem() == MobLootBagsModItems.SUMMONING_LOOTBAGS.get()) {
			loot_table_name = MainConfigFileConfiguration.SUMMONING_LT_NAME.get();
		}
		if ((loot_table_name).equals("")) {
			loot_table_name = MainConfigFileConfiguration.DEFAULT_LT_NAME.get();
		}
		if (world instanceof ServerLevel _level)
			_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
					("loot spawn ~ ~ ~ loot " + loot_table_name));
		if (entity instanceof Player _player) {
			ItemStack _stktoremove = itemstack;
			_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
		}
	}
}
