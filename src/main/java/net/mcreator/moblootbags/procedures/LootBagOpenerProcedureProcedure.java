package net.mcreator.moblootbags.procedures;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import net.mcreator.moblootbags.init.MobLootBagsModItems;
import net.mcreator.moblootbags.configuration.MainConfigFileConfiguration;

import java.util.concurrent.atomic.AtomicReference;

public class LootBagOpenerProcedureProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		String loot_table_name = "";
		boolean itemGiven = false;
		double slot_number = 0;
		double emptySlot = 0;
		slot_number = GiveFilledSlotNumberInBlockProcedure.execute(world, x, y, z);
		if (slot_number != 99) {
			if ((new Object() {
				public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
					AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
					BlockEntity _ent = world.getBlockEntity(pos);
					if (_ent != null)
						_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> _retval.set(capability.getStackInSlot(slotid).copy()));
					return _retval.get();
				}
			}.getItemStack(world, BlockPos.containing(x, y, z), (int) slot_number)).getItem() == MobLootBagsModItems.COMMONLOOTBAG.get()) {
				for (String stringiterator : MainConfigFileConfiguration.COMMON_LT_NAME.get()) {
					loot_table_name = stringiterator;
					break;
				}
			} else if ((new Object() {
				public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
					AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
					BlockEntity _ent = world.getBlockEntity(pos);
					if (_ent != null)
						_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> _retval.set(capability.getStackInSlot(slotid).copy()));
					return _retval.get();
				}
			}.getItemStack(world, BlockPos.containing(x, y, z), (int) slot_number)).getItem() == MobLootBagsModItems.UNCOMMONLOOTBAG.get()) {
				for (String stringiterator : MainConfigFileConfiguration.UNCOMMON_LT_NAME.get()) {
					loot_table_name = stringiterator;
					break;
				}
			} else if ((new Object() {
				public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
					AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
					BlockEntity _ent = world.getBlockEntity(pos);
					if (_ent != null)
						_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> _retval.set(capability.getStackInSlot(slotid).copy()));
					return _retval.get();
				}
			}.getItemStack(world, BlockPos.containing(x, y, z), (int) slot_number)).getItem() == MobLootBagsModItems.RARELOOTBAG.get()) {
				for (String stringiterator : MainConfigFileConfiguration.RARE_LT_NAME.get()) {
					loot_table_name = stringiterator;
					break;
				}
			} else if ((new Object() {
				public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
					AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
					BlockEntity _ent = world.getBlockEntity(pos);
					if (_ent != null)
						_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> _retval.set(capability.getStackInSlot(slotid).copy()));
					return _retval.get();
				}
			}.getItemStack(world, BlockPos.containing(x, y, z), (int) slot_number)).getItem() == MobLootBagsModItems.EPICLOOTBAG.get()) {
				for (String stringiterator : MainConfigFileConfiguration.EPIC_LT_NAME.get()) {
					loot_table_name = stringiterator;
					break;
				}
			} else if ((new Object() {
				public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
					AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
					BlockEntity _ent = world.getBlockEntity(pos);
					if (_ent != null)
						_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> _retval.set(capability.getStackInSlot(slotid).copy()));
					return _retval.get();
				}
			}.getItemStack(world, BlockPos.containing(x, y, z), (int) slot_number)).getItem() == MobLootBagsModItems.LEGENDARYLOOTBAG.get()) {
				for (String stringiterator : MainConfigFileConfiguration.LEGENDARY_LT_NAME.get()) {
					loot_table_name = stringiterator;
					break;
				}
			} else {
				return;
			}
			itemGiven = false;
			emptySlot = GiveEmptySlotNumberInBlockProcedure.execute(world, x, y, z);
			if (emptySlot != 99) {
				if (!world.isClientSide() && world.getServer() != null) {
					for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(new ResourceLocation((loot_table_name).toLowerCase(java.util.Locale.ENGLISH)))
							.getRandomItems(new LootParams.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY))) {
						if (Mth.nextDouble(RandomSource.create(), 0, 100) <= 20) {
							if (itemstackiterator.isStackable()) {
								{
									BlockEntity _ent = world.getBlockEntity(BlockPos.containing(x, y, z));
									if (_ent != null) {
										final int _slotid = (int) emptySlot;
										final ItemStack _setstack = itemstackiterator.copy();
										_setstack.setCount(Mth.nextInt(RandomSource.create(), 1, (int) IsItemValuableProcedure.execute(itemstackiterator)));
										_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
											if (capability instanceof IItemHandlerModifiable)
												((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _setstack);
										});
									}
								}
							} else {
								{
									BlockEntity _ent = world.getBlockEntity(BlockPos.containing(x, y, z));
									if (_ent != null) {
										final int _slotid = (int) emptySlot;
										final ItemStack _setstack = itemstackiterator.copy();
										_setstack.setCount(1);
										_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
											if (capability instanceof IItemHandlerModifiable)
												((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _setstack);
										});
									}
								}
							}
							itemGiven = true;
							break;
						}
					}
				}
				if (!itemGiven) {
					if (!world.isClientSide() && world.getServer() != null) {
						for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(new ResourceLocation((loot_table_name).toLowerCase(java.util.Locale.ENGLISH)))
								.getRandomItems(new LootParams.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY))) {
							if (itemstackiterator.isStackable()) {
								{
									BlockEntity _ent = world.getBlockEntity(BlockPos.containing(x, y, z));
									if (_ent != null) {
										final int _slotid = (int) emptySlot;
										final ItemStack _setstack = itemstackiterator.copy();
										_setstack.setCount(Mth.nextInt(RandomSource.create(), 1, (int) IsItemValuableProcedure.execute(itemstackiterator)));
										_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
											if (capability instanceof IItemHandlerModifiable)
												((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _setstack);
										});
									}
								}
							} else {
								{
									BlockEntity _ent = world.getBlockEntity(BlockPos.containing(x, y, z));
									if (_ent != null) {
										final int _slotid = (int) emptySlot;
										final ItemStack _setstack = itemstackiterator.copy();
										_setstack.setCount(1);
										_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
											if (capability instanceof IItemHandlerModifiable)
												((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _setstack);
										});
									}
								}
							}
							break;
						}
					}
				}
				{
					BlockEntity _ent = world.getBlockEntity(BlockPos.containing(x, y, z));
					if (_ent != null) {
						final int _slotid = (int) slot_number;
						final int _amount = 1;
						_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
							if (capability instanceof IItemHandlerModifiable) {
								ItemStack _stk = capability.getStackInSlot(_slotid).copy();
								_stk.shrink(_amount);
								((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _stk);
							}
						});
					}
				}
			}
		}
	}
}
