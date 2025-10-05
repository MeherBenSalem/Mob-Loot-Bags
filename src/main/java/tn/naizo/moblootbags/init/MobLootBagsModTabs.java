/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tn.naizo.moblootbags.init;

import tn.naizo.moblootbags.MobLootBagsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

public class MobLootBagsModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MobLootBagsMod.MODID);
	public static final RegistryObject<CreativeModeTab> LOOT_BAGS_CREATIVE_TAB = REGISTRY.register("loot_bags_creative_tab",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.mob_loot_bags.loot_bags_creative_tab")).icon(() -> new ItemStack(MobLootBagsModItems.COMMONLOOTBAG.get())).displayItems((parameters, tabData) -> {
				tabData.accept(MobLootBagsModItems.COMMONLOOTBAG.get());
				tabData.accept(MobLootBagsModItems.UNCOMMONLOOTBAG.get());
				tabData.accept(MobLootBagsModItems.RARELOOTBAG.get());
				tabData.accept(MobLootBagsModItems.EPICLOOTBAG.get());
				tabData.accept(MobLootBagsModItems.LEGENDARYLOOTBAG.get());
				tabData.accept(MobLootBagsModItems.CURSED_LOOTBAG.get());
				tabData.accept(MobLootBagsModItems.TIMED_LOOT_BAG.get());
				tabData.accept(MobLootBagsModItems.LOCKED_LOOTBAGS.get());
				tabData.accept(MobLootBagsModItems.SUMMONING_LOOTBAGS.get());
				tabData.accept(MobLootBagsModItems.DIAMOND_KEY.get());
				tabData.accept(MobLootBagsModBlocks.LOOT_BAG_RECYCLE_BLOCK.get().asItem());
				tabData.accept(MobLootBagsModBlocks.LOOT_BAG_OPENER_BLOCK.get().asItem());
			}).build());
}