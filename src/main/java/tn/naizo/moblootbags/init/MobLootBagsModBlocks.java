/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tn.naizo.moblootbags.init;

import tn.naizo.moblootbags.block.LootBagRecycleBlockBlock;
import tn.naizo.moblootbags.block.LootBagOpenerBlockBlock;
import tn.naizo.moblootbags.MobLootBagsMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class MobLootBagsModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(MobLootBagsMod.MODID);
	public static final DeferredBlock<Block> LOOT_BAG_RECYCLE_BLOCK;
	public static final DeferredBlock<Block> LOOT_BAG_OPENER_BLOCK;
	static {
		LOOT_BAG_RECYCLE_BLOCK = register("loot_bag_recycle_block", LootBagRecycleBlockBlock::new);
		LOOT_BAG_OPENER_BLOCK = register("loot_bag_opener_block", LootBagOpenerBlockBlock::new);
	}

	// Start of user code block custom blocks
	// End of user code block custom blocks
	private static <B extends Block> DeferredBlock<B> register(String name, Function<BlockBehaviour.Properties, ? extends B> supplier) {
		return REGISTRY.registerBlock(name, supplier);
	}
}