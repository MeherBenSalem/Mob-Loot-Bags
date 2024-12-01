
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.moblootbags.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

import net.mcreator.moblootbags.world.inventory.LootbagRecyleBlockGUIMenu;
import net.mcreator.moblootbags.world.inventory.LootbagOpenBlockGUIMenu;
import net.mcreator.moblootbags.world.inventory.ChooseRewardGuiMenu;
import net.mcreator.moblootbags.MobLootBagsMod;

public class MobLootBagsModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MobLootBagsMod.MODID);
	public static final RegistryObject<MenuType<LootbagOpenBlockGUIMenu>> LOOTBAG_OPEN_BLOCK_GUI = REGISTRY.register("lootbag_open_block_gui", () -> IForgeMenuType.create(LootbagOpenBlockGUIMenu::new));
	public static final RegistryObject<MenuType<LootbagRecyleBlockGUIMenu>> LOOTBAG_RECYLE_BLOCK_GUI = REGISTRY.register("lootbag_recyle_block_gui", () -> IForgeMenuType.create(LootbagRecyleBlockGUIMenu::new));
	public static final RegistryObject<MenuType<ChooseRewardGuiMenu>> CHOOSE_REWARD_GUI = REGISTRY.register("choose_reward_gui", () -> IForgeMenuType.create(ChooseRewardGuiMenu::new));
}
