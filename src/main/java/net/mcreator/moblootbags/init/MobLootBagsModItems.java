
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.moblootbags.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import net.mcreator.moblootbags.item.UncommonlootbagItem;
import net.mcreator.moblootbags.item.UncommonGiftItem;
import net.mcreator.moblootbags.item.RarelootbagItem;
import net.mcreator.moblootbags.item.RareGiftItem;
import net.mcreator.moblootbags.item.LegendarylootbagItem;
import net.mcreator.moblootbags.item.EpiclootbagItem;
import net.mcreator.moblootbags.item.EpicGiftItem;
import net.mcreator.moblootbags.item.CommonlootbagItem;
import net.mcreator.moblootbags.MobLootBagsMod;

public class MobLootBagsModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MobLootBagsMod.MODID);
	public static final RegistryObject<Item> COMMONLOOTBAG = REGISTRY.register("commonlootbag", () -> new CommonlootbagItem());
	public static final RegistryObject<Item> UNCOMMONLOOTBAG = REGISTRY.register("uncommonlootbag", () -> new UncommonlootbagItem());
	public static final RegistryObject<Item> RARELOOTBAG = REGISTRY.register("rarelootbag", () -> new RarelootbagItem());
	public static final RegistryObject<Item> EPICLOOTBAG = REGISTRY.register("epiclootbag", () -> new EpiclootbagItem());
	public static final RegistryObject<Item> LEGENDARYLOOTBAG = REGISTRY.register("legendarylootbag", () -> new LegendarylootbagItem());
	public static final RegistryObject<Item> LOOT_BAG_RECYCLE_BLOCK = block(MobLootBagsModBlocks.LOOT_BAG_RECYCLE_BLOCK);
	public static final RegistryObject<Item> LOOT_BAG_OPENER_BLOCK = block(MobLootBagsModBlocks.LOOT_BAG_OPENER_BLOCK);
	public static final RegistryObject<Item> RARE_GIFT = REGISTRY.register("rare_gift", () -> new RareGiftItem());
	public static final RegistryObject<Item> EPIC_GIFT = REGISTRY.register("epic_gift", () -> new EpicGiftItem());
	public static final RegistryObject<Item> UNCOMMON_GIFT = REGISTRY.register("uncommon_gift", () -> new UncommonGiftItem());

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
