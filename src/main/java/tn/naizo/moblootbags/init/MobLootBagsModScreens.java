/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package tn.naizo.moblootbags.init;

import tn.naizo.moblootbags.client.gui.LootbagRecyleBlockGUIScreen;
import tn.naizo.moblootbags.client.gui.LootbagOpenBlockGUIScreen;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(Dist.CLIENT)
public class MobLootBagsModScreens {
	@SubscribeEvent
	public static void clientLoad(RegisterMenuScreensEvent event) {
		event.register(MobLootBagsModMenus.LOOTBAG_OPEN_BLOCK_GUI.get(), LootbagOpenBlockGUIScreen::new);
		event.register(MobLootBagsModMenus.LOOTBAG_RECYLE_BLOCK_GUI.get(), LootbagRecyleBlockGUIScreen::new);
	}

	public interface ScreenAccessor {
		void updateMenuState(int elementType, String name, Object elementState);
	}
}