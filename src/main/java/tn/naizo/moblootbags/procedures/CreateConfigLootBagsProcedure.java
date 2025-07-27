package tn.naizo.moblootbags.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreateConfigLootBagsProcedure {
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		execute();
	}

	public static void execute() {
		execute(null);
	}

	private static void execute(@Nullable Event event) {
		String directory = "";
		String filename = "";
		directory = "mlb";
		filename = "loot_tables";
		if (JaumlConfigLib.createConfigFile(directory, filename)) {
			JaumlConfigLib.createConfigFile(directory, filename);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "common_lt_name")) {
			JaumlConfigLib.addStringToArray(directory, filename, "common_lt_name", "mob_loot_bags:lootbags/common");
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "uncommon_lt_name")) {
			JaumlConfigLib.addStringToArray(directory, filename, "uncommon_lt_name", "mob_loot_bags:un_common");
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "rare_lt_name")) {
			JaumlConfigLib.addStringToArray(directory, filename, "rare_lt_name", "mob_loot_bags:lootbags/rare");
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "epic_lt_name")) {
			JaumlConfigLib.addStringToArray(directory, filename, "epic_lt_name", "mob_loot_bags:lootbags/epic");
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "legendary_lt_name")) {
			JaumlConfigLib.addStringToArray(directory, filename, "legendary_lt_name", "mob_loot_bags:legendary_loot_table");
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "summoning_lt_name")) {
			JaumlConfigLib.setStringValue(directory, filename, "summoning_lt_name", "mob_loot_bags:summoning_loot_table");
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "locked_lt_name")) {
			JaumlConfigLib.setStringValue(directory, filename, "locked_lt_name", "mob_loot_bags:locked_loot_table");
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "default_lt_name")) {
			JaumlConfigLib.setStringValue(directory, filename, "default_lt_name", "minecraft:chests/end_city_treasure");
		}
	}
}
