
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tn.naizo.moblootbags.init;

import tn.naizo.moblootbags.block.entity.LootBagRecycleBlockBlockEntity;
import tn.naizo.moblootbags.block.entity.LootBagOpenerBlockBlockEntity;
import tn.naizo.moblootbags.MobLootBagsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;

public class MobLootBagsModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MobLootBagsMod.MODID);
	public static final RegistryObject<BlockEntityType<?>> LOOT_BAG_RECYCLE_BLOCK = register("loot_bag_recycle_block", MobLootBagsModBlocks.LOOT_BAG_RECYCLE_BLOCK, LootBagRecycleBlockBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> LOOT_BAG_OPENER_BLOCK = register("loot_bag_opener_block", MobLootBagsModBlocks.LOOT_BAG_OPENER_BLOCK, LootBagOpenerBlockBlockEntity::new);

	// Start of user code block custom block entities
	// End of user code block custom block entities
	private static RegistryObject<BlockEntityType<?>> register(String registryname, RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}
}
