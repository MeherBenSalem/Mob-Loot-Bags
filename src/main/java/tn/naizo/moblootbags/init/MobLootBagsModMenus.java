/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package tn.naizo.moblootbags.init;

import tn.naizo.moblootbags.world.inventory.LootbagRecyleBlockGUIMenu;
import tn.naizo.moblootbags.world.inventory.LootbagOpenBlockGUIMenu;
import tn.naizo.moblootbags.network.MenuStateUpdateMessage;
import tn.naizo.moblootbags.MobLootBagsMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.client.Minecraft;

import java.util.Map;

public class MobLootBagsModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, MobLootBagsMod.MODID);
	public static final DeferredHolder<MenuType<?>, MenuType<LootbagOpenBlockGUIMenu>> LOOTBAG_OPEN_BLOCK_GUI = REGISTRY.register("lootbag_open_block_gui", () -> IMenuTypeExtension.create(LootbagOpenBlockGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<LootbagRecyleBlockGUIMenu>> LOOTBAG_RECYLE_BLOCK_GUI = REGISTRY.register("lootbag_recyle_block_gui", () -> IMenuTypeExtension.create(LootbagRecyleBlockGUIMenu::new));

	public interface MenuAccessor {
		Map<String, Object> getMenuState();

		Map<Integer, Slot> getSlots();

		default void sendMenuStateUpdate(Player player, int elementType, String name, Object elementState, boolean needClientUpdate) {
			getMenuState().put(elementType + ":" + name, elementState);
			if (player instanceof ServerPlayer serverPlayer) {
				PacketDistributor.sendToPlayer(serverPlayer, new MenuStateUpdateMessage(elementType, name, elementState));
			} else if (player.level().isClientSide) {
				if (Minecraft.getInstance().screen instanceof MobLootBagsModScreens.ScreenAccessor accessor && needClientUpdate)
					accessor.updateMenuState(elementType, name, elementState);
				ClientPacketDistributor.sendToServer(new MenuStateUpdateMessage(elementType, name, elementState));
			}
		}

		default <T> T getMenuState(int elementType, String name, T defaultValue) {
			try {
				return (T) getMenuState().getOrDefault(elementType + ":" + name, defaultValue);
			} catch (ClassCastException e) {
				return defaultValue;
			}
		}
	}
}