package tn.naizo.moblootbags.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreateRecycleBlockConfigProcedure {
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
		filename = "bag_recycler";
		if (JaumlConfigLib.createConfigFile(directory, filename)) {
			JaumlConfigLib.createConfigFile(directory, filename);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "common_lb")) {
			JaumlConfigLib.setNumberValue(directory, filename, "common_lb", 10);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "uncommon_lb")) {
			JaumlConfigLib.setNumberValue(directory, filename, "uncommon_lb", 20);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "rare_lb")) {
			JaumlConfigLib.setNumberValue(directory, filename, "rare_lb", 40);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "epic_lb")) {
			JaumlConfigLib.setNumberValue(directory, filename, "epic_lb", 80);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "legendary_lb")) {
			JaumlConfigLib.setNumberValue(directory, filename, "legendary_lb", 200);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "cursed_lb")) {
			JaumlConfigLib.setNumberValue(directory, filename, "cursed_lb", 60);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "timed_lb")) {
			JaumlConfigLib.setNumberValue(directory, filename, "timed_lb", 40);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "enchanted_timed_lb")) {
			JaumlConfigLib.setNumberValue(directory, filename, "enchanted_timed_lb", 200);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "locked_lb")) {
			JaumlConfigLib.setNumberValue(directory, filename, "locked_lb", 40);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "summoning_lb")) {
			JaumlConfigLib.setNumberValue(directory, filename, "summoning_lb", 40);
		}
	}
}