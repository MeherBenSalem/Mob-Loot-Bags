
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.moblootbags.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.mcreator.moblootbags.MobLootBagsMod;

public class MobLootBagsModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MobLootBagsMod.MODID);
	public static final RegistryObject<CreativeModeTab> LOOT_BAGS_CREATIVE_TAB = REGISTRY.register("loot_bags_creative_tab",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.mob_loot_bags.loot_bags_creative_tab")).icon(() -> new ItemStack(MobLootBagsModItems.COMMONLOOTBAG.get())).displayItems((parameters, tabData) -> {
				tabData.accept(MobLootBagsModItems.COMMONLOOTBAG.get());
				tabData.accept(MobLootBagsModItems.UNCOMMONLOOTBAG.get());
				tabData.accept(MobLootBagsModItems.RARELOOTBAG.get());
				tabData.accept(MobLootBagsModItems.EPICLOOTBAG.get());
				tabData.accept(MobLootBagsModItems.LEGENDARYLOOTBAG.get());
				tabData.accept(MobLootBagsModBlocks.LOOT_BAG_RECYCLE_BLOCK.get().asItem());
				tabData.accept(MobLootBagsModBlocks.LOOT_BAG_OPENER_BLOCK.get().asItem());
				tabData.accept(MobLootBagsModItems.RARE_GIFT.get());
				tabData.accept(MobLootBagsModItems.EPIC_GIFT.get());
				tabData.accept(MobLootBagsModItems.UNCOMMON_GIFT.get());
			}).build());
}
