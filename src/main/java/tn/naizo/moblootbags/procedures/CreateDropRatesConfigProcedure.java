package tn.naizo.moblootbags.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import javax.annotation.Nullable;

@EventBusSubscriber
public class CreateDropRatesConfigProcedure {
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
		filename = "drop_rates";
		if (JaumlConfigLib.createConfigFile(directory, filename)) {
			JaumlConfigLib.createConfigFile(directory, filename);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "drop_chance_overall")) {
			JaumlConfigLib.setNumberValue(directory, filename, "drop_chance_overall", 80);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "common_drop_rate")) {
			JaumlConfigLib.setNumberValue(directory, filename, "common_drop_rate", 60);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "uncommon_drop_rate")) {
			JaumlConfigLib.setNumberValue(directory, filename, "uncommon_drop_rate", 25);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "rare_drop_rate")) {
			JaumlConfigLib.setNumberValue(directory, filename, "rare_drop_rate", 10);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "epic_drop_rate")) {
			JaumlConfigLib.setNumberValue(directory, filename, "epic_drop_rate", 4);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "legendary_drop_rate")) {
			JaumlConfigLib.setNumberValue(directory, filename, "legendary_drop_rate", 1);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "cursed_drop_rate")) {
			JaumlConfigLib.setNumberValue(directory, filename, "cursed_drop_rate", 1);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "timed_drop_rate")) {
			JaumlConfigLib.setNumberValue(directory, filename, "timed_drop_rate", 20);
		}
	}
}