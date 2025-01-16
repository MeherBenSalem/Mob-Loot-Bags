
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package tn.naizo.moblootbags.init;

import tn.naizo.moblootbags.client.gui.LootbagRecyleBlockGUIScreen;
import tn.naizo.moblootbags.client.gui.LootbagOpenBlockGUIScreen;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.gui.screens.MenuScreens;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MobLootBagsModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(MobLootBagsModMenus.LOOTBAG_OPEN_BLOCK_GUI.get(), LootbagOpenBlockGUIScreen::new);
			MenuScreens.register(MobLootBagsModMenus.LOOTBAG_RECYLE_BLOCK_GUI.get(), LootbagRecyleBlockGUIScreen::new);
		});
	}
}
