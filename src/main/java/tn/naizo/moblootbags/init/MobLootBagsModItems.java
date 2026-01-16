/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tn.naizo.moblootbags.init;

import tn.naizo.moblootbags.item.*;
import tn.naizo.moblootbags.block.LootBagRecycleBlockBlock;
import tn.naizo.moblootbags.block.LootBagOpenerBlockBlock;
import tn.naizo.moblootbags.MobLootBagsMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

import net.minecraft.world.item.Item;

import java.util.function.Function;

public class MobLootBagsModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(MobLootBagsMod.MODID);
	public static final DeferredItem<Item> COMMONLOOTBAG;
	public static final DeferredItem<Item> UNCOMMONLOOTBAG;
	public static final DeferredItem<Item> RARELOOTBAG;
	public static final DeferredItem<Item> EPICLOOTBAG;
	public static final DeferredItem<Item> LEGENDARYLOOTBAG;
	public static final DeferredItem<Item> LOOT_BAG_RECYCLE_BLOCK;
	public static final DeferredItem<Item> LOOT_BAG_OPENER_BLOCK;
	public static final DeferredItem<Item> CURSED_LOOTBAG;
	public static final DeferredItem<Item> TIMED_LOOT_BAG;
	public static final DeferredItem<Item> DIAMOND_KEY;
	public static final DeferredItem<Item> LOCKED_LOOTBAGS;
	public static final DeferredItem<Item> SUMMONING_LOOTBAGS;
	static {
		COMMONLOOTBAG = register("commonlootbag", CommonlootbagItem::new);
		UNCOMMONLOOTBAG = register("uncommonlootbag", UncommonlootbagItem::new);
		RARELOOTBAG = register("rarelootbag", RarelootbagItem::new);
		EPICLOOTBAG = register("epiclootbag", EpiclootbagItem::new);
		LEGENDARYLOOTBAG = register("legendarylootbag", LegendarylootbagItem::new);
		LOOT_BAG_RECYCLE_BLOCK = register("loot_bag_recycle_block", LootBagRecycleBlockBlock.Item::new);
		LOOT_BAG_OPENER_BLOCK = register("loot_bag_opener_block", LootBagOpenerBlockBlock.Item::new);
		CURSED_LOOTBAG = register("cursed_lootbag", CursedLootbagItem::new);
		TIMED_LOOT_BAG = register("timed_loot_bag", TimedLootBagItem::new);
		DIAMOND_KEY = register("diamond_key", DiamondKeyItem::new);
		LOCKED_LOOTBAGS = register("locked_lootbags", LockedLootbagsItem::new);
		SUMMONING_LOOTBAGS = register("summoning_lootbags", SummoningLootbagsItem::new);
	}

	// Start of user code block custom items
	// End of user code block custom items
	private static <I extends Item> DeferredItem<I> register(String name, Function<Item.Properties, ? extends I> supplier) {
		return REGISTRY.registerItem(name, supplier, new Item.Properties());
	}
}