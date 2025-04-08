
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tn.naizo.moblootbags.init;

import tn.naizo.moblootbags.item.UncommonlootbagItem;
import tn.naizo.moblootbags.item.TimedLootBagItem;
import tn.naizo.moblootbags.item.SummoningLootbagsItem;
import tn.naizo.moblootbags.item.RarelootbagItem;
import tn.naizo.moblootbags.item.LockedLootbagsItem;
import tn.naizo.moblootbags.item.LegendarylootbagItem;
import tn.naizo.moblootbags.item.EpiclootbagItem;
import tn.naizo.moblootbags.item.DiamondKeyItem;
import tn.naizo.moblootbags.item.CursedLootbagItem;
import tn.naizo.moblootbags.item.CommonlootbagItem;
import tn.naizo.moblootbags.MobLootBagsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

public class MobLootBagsModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MobLootBagsMod.MODID);
	public static final RegistryObject<Item> COMMONLOOTBAG = REGISTRY.register("commonlootbag", () -> new CommonlootbagItem());
	public static final RegistryObject<Item> UNCOMMONLOOTBAG = REGISTRY.register("uncommonlootbag", () -> new UncommonlootbagItem());
	public static final RegistryObject<Item> RARELOOTBAG = REGISTRY.register("rarelootbag", () -> new RarelootbagItem());
	public static final RegistryObject<Item> EPICLOOTBAG = REGISTRY.register("epiclootbag", () -> new EpiclootbagItem());
	public static final RegistryObject<Item> LEGENDARYLOOTBAG = REGISTRY.register("legendarylootbag", () -> new LegendarylootbagItem());
	public static final RegistryObject<Item> LOOT_BAG_RECYCLE_BLOCK = block(MobLootBagsModBlocks.LOOT_BAG_RECYCLE_BLOCK);
	public static final RegistryObject<Item> LOOT_BAG_OPENER_BLOCK = block(MobLootBagsModBlocks.LOOT_BAG_OPENER_BLOCK);
	public static final RegistryObject<Item> CURSED_LOOTBAG = REGISTRY.register("cursed_lootbag", () -> new CursedLootbagItem());
	public static final RegistryObject<Item> TIMED_LOOT_BAG = REGISTRY.register("timed_loot_bag", () -> new TimedLootBagItem());
	public static final RegistryObject<Item> DIAMOND_KEY = REGISTRY.register("diamond_key", () -> new DiamondKeyItem());
	public static final RegistryObject<Item> LOCKED_LOOTBAGS = REGISTRY.register("locked_lootbags", () -> new LockedLootbagsItem());
	public static final RegistryObject<Item> SUMMONING_LOOTBAGS = REGISTRY.register("summoning_lootbags", () -> new SummoningLootbagsItem());

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
