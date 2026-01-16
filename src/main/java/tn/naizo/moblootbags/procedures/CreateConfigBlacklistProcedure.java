package tn.naizo.moblootbags.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import javax.annotation.Nullable;

@EventBusSubscriber
public class CreateConfigBlacklistProcedure {
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
		filename = "blacklist";
		if (JaumlConfigLib.createConfigFile(directory, filename)) {
			JaumlConfigLib.createConfigFile(directory, filename);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, directory, "enable")) {
			JaumlConfigLib.setBooleanValue(directory, filename, "enable", false);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "mobs")) {
			JaumlConfigLib.addStringToArray(directory, filename, "mobs", "minecraft:cow");
		}
	}
}