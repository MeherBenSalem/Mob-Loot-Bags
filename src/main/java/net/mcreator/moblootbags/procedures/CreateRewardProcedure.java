package net.mcreator.moblootbags.procedures;

import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.moblootbags.configuration.MainConfigFileConfiguration;

import java.util.function.Supplier;
import java.util.Map;

public class CreateRewardProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		String loot_table_name = "";
		if (!(((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getString("mlb_table")).equals(""))) {
			loot_table_name = (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getString("mlb_table");
		} else {
			loot_table_name = MainConfigFileConfiguration.CUSTOM_LT_NAME.get();
		}
		if (!world.isClientSide() && world.getServer() != null) {
			for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(new ResourceLocation((loot_table_name).toLowerCase(java.util.Locale.ENGLISH)))
					.getRandomItems(new LootParams.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY))) {
				if (Mth.nextInt(RandomSource.create(), 1, 10) <= 3) {
					if (itemstackiterator.isStackable()) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = itemstackiterator.copy();
							_setstack.setCount(Mth.nextInt(RandomSource.create(), 1, 10));
							((Slot) _slots.get(0)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = itemstackiterator.copy();
							_setstack.setCount(1);
							((Slot) _slots.get(0)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					}
					break;
				}
			}
		}
		if (!world.isClientSide() && world.getServer() != null) {
			for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(new ResourceLocation((loot_table_name).toLowerCase(java.util.Locale.ENGLISH)))
					.getRandomItems(new LootParams.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY))) {
				if (Mth.nextInt(RandomSource.create(), 1, 10) <= 3) {
					if (itemstackiterator.isStackable()) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = itemstackiterator.copy();
							_setstack.setCount(Mth.nextInt(RandomSource.create(), 1, 10));
							((Slot) _slots.get(1)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = itemstackiterator.copy();
							_setstack.setCount(1);
							((Slot) _slots.get(1)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					}
					break;
				}
			}
		}
		if (!world.isClientSide() && world.getServer() != null) {
			for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(new ResourceLocation((loot_table_name).toLowerCase(java.util.Locale.ENGLISH)))
					.getRandomItems(new LootParams.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY))) {
				if (Mth.nextInt(RandomSource.create(), 1, 10) <= 3) {
					if (itemstackiterator.isStackable()) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = itemstackiterator.copy();
							_setstack.setCount(Mth.nextInt(RandomSource.create(), 1, 10));
							((Slot) _slots.get(1)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = itemstackiterator.copy();
							_setstack.setCount(1);
							((Slot) _slots.get(2)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					}
					break;
				}
			}
		}
		if (new Object() {
			public int getAmount(int sltid) {
				if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
					ItemStack stack = ((Slot) _slots.get(sltid)).getItem();
					if (stack != null)
						return stack.getCount();
				}
				return 0;
			}
		}.getAmount(0) <= 0) {
			if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
				ItemStack _setstack = new ItemStack(Items.DIAMOND).copy();
				_setstack.setCount(Mth.nextInt(RandomSource.create(), 1, 10));
				((Slot) _slots.get(0)).set(_setstack);
				_player.containerMenu.broadcastChanges();
			}
		}
		if (new Object() {
			public int getAmount(int sltid) {
				if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
					ItemStack stack = ((Slot) _slots.get(sltid)).getItem();
					if (stack != null)
						return stack.getCount();
				}
				return 0;
			}
		}.getAmount(1) <= 0) {
			if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
				ItemStack _setstack = new ItemStack(Items.DIAMOND).copy();
				_setstack.setCount(Mth.nextInt(RandomSource.create(), 1, 10));
				((Slot) _slots.get(1)).set(_setstack);
				_player.containerMenu.broadcastChanges();
			}
		}
		if (new Object() {
			public int getAmount(int sltid) {
				if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
					ItemStack stack = ((Slot) _slots.get(sltid)).getItem();
					if (stack != null)
						return stack.getCount();
				}
				return 0;
			}
		}.getAmount(2) <= 0) {
			if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
				ItemStack _setstack = new ItemStack(Items.DIAMOND).copy();
				_setstack.setCount(Mth.nextInt(RandomSource.create(), 1, 10));
				((Slot) _slots.get(2)).set(_setstack);
				_player.containerMenu.broadcastChanges();
			}
		}
	}
}
