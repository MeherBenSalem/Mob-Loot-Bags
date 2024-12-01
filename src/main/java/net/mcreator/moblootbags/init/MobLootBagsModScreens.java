
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.moblootbags.init;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.gui.screens.MenuScreens;

import net.mcreator.moblootbags.client.gui.LootbagRecyleBlockGUIScreen;
import net.mcreator.moblootbags.client.gui.LootbagOpenBlockGUIScreen;
import net.mcreator.moblootbags.client.gui.ChooseRewardGuiScreen;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MobLootBagsModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(MobLootBagsModMenus.LOOTBAG_OPEN_BLOCK_GUI.get(), LootbagOpenBlockGUIScreen::new);
			MenuScreens.register(MobLootBagsModMenus.LOOTBAG_RECYLE_BLOCK_GUI.get(), LootbagRecyleBlockGUIScreen::new);
			MenuScreens.register(MobLootBagsModMenus.CHOOSE_REWARD_GUI.get(), ChooseRewardGuiScreen::new);
		});
	}
}
