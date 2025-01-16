package tn.naizo.moblootbags.procedures;

import tn.naizo.moblootbags.init.MobLootBagsModItems;
import tn.naizo.moblootbags.configuration.MainConfigFileConfiguration;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import java.util.concurrent.atomic.AtomicReference;

public class LootBagOpenerGiveToBlockProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		String loot_table_name = "";
		double lootTableChosen = 0;
		double counter = 0;
		double slot_number = 0;
		slot_number = GiveFilledSlotNumberInBlockProcedure.execute(world, x, y, z);
		lootTableChosen = 0;
		counter = 1;
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
				lootTableChosen = Mth.nextInt(RandomSource.create(), 1, (int) ReturnLengthOfCommunBagsProcedure.execute());
				for (String stringiterator : MainConfigFileConfiguration.COMMON_LT_NAME.get()) {
					if (counter == lootTableChosen) {
						loot_table_name = stringiterator;
						break;
					} else {
						counter = counter + 1;
					}
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
				lootTableChosen = Mth.nextInt(RandomSource.create(), 1, (int) ReturnLengthOfUnCommunProcedure.execute());
				for (String stringiterator : MainConfigFileConfiguration.UNCOMMON_LT_NAME.get()) {
					if (counter == lootTableChosen) {
						loot_table_name = stringiterator;
						break;
					} else {
						counter = counter + 1;
					}
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
				lootTableChosen = Mth.nextInt(RandomSource.create(), 1, (int) ReturnLengthOfRareProcedure.execute());
				for (String stringiterator : MainConfigFileConfiguration.RARE_LT_NAME.get()) {
					if (counter == lootTableChosen) {
						loot_table_name = stringiterator;
						break;
					} else {
						counter = counter + 1;
					}
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
				lootTableChosen = Mth.nextInt(RandomSource.create(), 1, (int) ReturnLengthOfEpicProcedure.execute());
				for (String stringiterator : MainConfigFileConfiguration.EPIC_LT_NAME.get()) {
					if (counter == lootTableChosen) {
						loot_table_name = stringiterator;
						break;
					} else {
						counter = counter + 1;
					}
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
				lootTableChosen = Mth.nextInt(RandomSource.create(), 1, (int) ReturnLengthOfLegendaryProcedure.execute());
				for (String stringiterator : MainConfigFileConfiguration.LEGENDARY_LT_NAME.get()) {
					if (counter == lootTableChosen) {
						loot_table_name = stringiterator;
						break;
					} else {
						counter = counter + 1;
					}
				}
			} else {
				return;
			}
			if ((loot_table_name).equals("")) {
				loot_table_name = MainConfigFileConfiguration.DEFAULT_LT_NAME.get();
			}
			if ((world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == Blocks.CHEST || (world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == Blocks.HOPPER) {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, (y + 1), z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot insert ~ ~ ~ loot " + loot_table_name));
			} else if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.CHEST || (world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.HOPPER) {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, (y - 1), z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot insert ~ ~ ~ loot " + loot_table_name));
			} else if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.CHEST || (world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.HOPPER) {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3((x + 1), y, z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot insert ~ ~ ~ loot " + loot_table_name));
			} else if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.CHEST || (world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.HOPPER) {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3((x - 1), y, z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot insert ~ ~ ~ loot " + loot_table_name));
			} else if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.CHEST || (world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.HOPPER) {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, (z + 1)), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot insert ~ ~ ~ loot " + loot_table_name));
			} else if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.CHEST || (world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.HOPPER) {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, (z - 1)), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot insert ~ ~ ~ loot " + loot_table_name));
			} else {
				if (world instanceof ServerLevel _level)
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, (y + 1), z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
							("loot spawn ~ ~ ~ loot " + loot_table_name));
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
