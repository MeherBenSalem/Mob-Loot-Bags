package tn.naizo.moblootbags.procedures;

import tn.naizo.moblootbags.init.MobLootBagsModItems;
import tn.naizo.jauml.JaumlConfigLib;

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
		if (itemstack.getItem() == MobLootBagsModItems.COMMONLOOTBAG.get()) {
			lootTableChosen = Mth.nextInt(RandomSource.create(), 0, (int) (JaumlConfigLib.getArrayLength("mlb", "loot_tables", "common_lt_name") - 1));
			loot_table_name = JaumlConfigLib.getArrayElement("mlb", "loot_tables", "common_lt_name", ((int) lootTableChosen));
		} else if (itemstack.getItem() == MobLootBagsModItems.UNCOMMONLOOTBAG.get()) {
			lootTableChosen = Mth.nextInt(RandomSource.create(), 0, (int) (JaumlConfigLib.getArrayLength("mlb", "loot_tables", "uncommon_lt_name") - 1));
			loot_table_name = JaumlConfigLib.getArrayElement("mlb", "loot_tables", "uncommon_lt_name", ((int) lootTableChosen));
		} else if (itemstack.getItem() == MobLootBagsModItems.RARELOOTBAG.get()) {
			lootTableChosen = Mth.nextInt(RandomSource.create(), 0, (int) (JaumlConfigLib.getArrayLength("mlb", "loot_tables", "rare_lt_name") - 1));
			loot_table_name = JaumlConfigLib.getArrayElement("mlb", "loot_tables", "rare_lt_name", ((int) lootTableChosen));
		} else if (itemstack.getItem() == MobLootBagsModItems.EPICLOOTBAG.get()) {
			lootTableChosen = Mth.nextInt(RandomSource.create(), 0, (int) (JaumlConfigLib.getArrayLength("mlb", "loot_tables", "epic_lt_name") - 1));
			loot_table_name = JaumlConfigLib.getArrayElement("mlb", "loot_tables", "epic_lt_name", ((int) lootTableChosen));
		} else if (itemstack.getItem() == MobLootBagsModItems.LEGENDARYLOOTBAG.get()) {
			lootTableChosen = Mth.nextInt(RandomSource.create(), 0, (int) (JaumlConfigLib.getArrayLength("mlb", "loot_tables", "legendary_lt_name") - 1));
			loot_table_name = JaumlConfigLib.getArrayElement("mlb", "loot_tables", "legendary_lt_name", ((int) lootTableChosen));
		} else if (itemstack.getItem() == MobLootBagsModItems.SUMMONING_LOOTBAGS.get()) {
			loot_table_name = JaumlConfigLib.getStringValue("mlb", "loot_tables", "summoning_lt_name");
		}
		if ((loot_table_name).equals("")) {
			loot_table_name = JaumlConfigLib.getStringValue("mlb", "loot_tables", "default_lt_name");
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