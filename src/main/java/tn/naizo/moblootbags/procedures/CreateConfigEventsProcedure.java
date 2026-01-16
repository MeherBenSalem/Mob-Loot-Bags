package tn.naizo.moblootbags.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import javax.annotation.Nullable;

@EventBusSubscriber
public class CreateConfigEventsProcedure {
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
		filename = "special_bags";
		if (JaumlConfigLib.createConfigFile(directory, filename)) {
			JaumlConfigLib.createConfigFile(directory, filename);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "enable_cursed_bag")) {
			JaumlConfigLib.setBooleanValue(directory, filename, "enable_cursed_bag", true);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "enable_sound")) {
			JaumlConfigLib.setBooleanValue(directory, filename, "enable_sound", true);
		}
		if (!JaumlConfigLib.arrayKeyExists(directory, filename, "events")) {
			JaumlConfigLib.addStringToArray(directory, filename, "events", "summon minecraft:warden ~ ~ ~");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "place structure minecraft:ancient_city");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "weather thunder");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "tp @p ~ ~-40 ~");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "effect give @p minecraft:blindness 10");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "summon minecraft:tnt ~ ~ ~ {Fuse:40}");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "summon minecraft:wither ~ ~ ~");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "summon minecraft:zombie ~ ~ ~ {IsBaby:1}");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "summon minecraft:firework_rocket ~ ~ ~ {Life:20,LifeTime:40}");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "xp add @p 50");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "xp add @p 100");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "xp add @p 500");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "place structure minecraft:village_plains");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "place structure minecraft:village_desert");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "place structure minecraft:ruined_portal");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "place structure minecraft:pillager_outpost");
			JaumlConfigLib.addStringToArray(directory, filename, "events", "place structure minecraft:jungle_temple");
		}
	}
}