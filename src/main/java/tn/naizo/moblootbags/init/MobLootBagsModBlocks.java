
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tn.naizo.moblootbags.init;

import tn.naizo.moblootbags.block.LootBagRecycleBlockBlock;
import tn.naizo.moblootbags.block.LootBagOpenerBlockBlock;
import tn.naizo.moblootbags.MobLootBagsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

public class MobLootBagsModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, MobLootBagsMod.MODID);
	public static final RegistryObject<Block> LOOT_BAG_RECYCLE_BLOCK = REGISTRY.register("loot_bag_recycle_block", () -> new LootBagRecycleBlockBlock());
	public static final RegistryObject<Block> LOOT_BAG_OPENER_BLOCK = REGISTRY.register("loot_bag_opener_block", () -> new LootBagOpenerBlockBlock());
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
