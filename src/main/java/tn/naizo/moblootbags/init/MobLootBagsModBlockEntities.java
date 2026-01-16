/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tn.naizo.moblootbags.init;

import tn.naizo.moblootbags.block.entity.LootBagRecycleBlockBlockEntity;
import tn.naizo.moblootbags.block.entity.LootBagOpenerBlockBlockEntity;
import tn.naizo.moblootbags.MobLootBagsMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;

@EventBusSubscriber
public class MobLootBagsModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MobLootBagsMod.MODID);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootBagRecycleBlockBlockEntity>> LOOT_BAG_RECYCLE_BLOCK = register("loot_bag_recycle_block", MobLootBagsModBlocks.LOOT_BAG_RECYCLE_BLOCK, LootBagRecycleBlockBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootBagOpenerBlockBlockEntity>> LOOT_BAG_OPENER_BLOCK = register("loot_bag_opener_block", MobLootBagsModBlocks.LOOT_BAG_OPENER_BLOCK, LootBagOpenerBlockBlockEntity::new);

	// Start of user code block custom block entities
	// End of user code block custom block entities
	private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String registryname, DeferredHolder<Block, Block> block, BlockEntityType.BlockEntitySupplier<T> supplier) {
		return REGISTRY.register(registryname, () -> new BlockEntityType(supplier, block.get()));
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, LOOT_BAG_RECYCLE_BLOCK.get(), SidedInvWrapper::new);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, LOOT_BAG_OPENER_BLOCK.get(), SidedInvWrapper::new);
	}
}