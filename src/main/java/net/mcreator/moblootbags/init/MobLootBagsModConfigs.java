package net.mcreator.moblootbags.init;

import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.mcreator.moblootbags.configuration.MainConfigFileConfiguration;
import net.mcreator.moblootbags.MobLootBagsMod;

@Mod.EventBusSubscriber(modid = MobLootBagsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobLootBagsModConfigs {
	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MainConfigFileConfiguration.SPEC, "MobLootBags/MobsLootTables.toml");
		});
	}
}
