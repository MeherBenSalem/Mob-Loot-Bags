package tn.naizo.moblootbags.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

public class HandleCursedLootBagLogicProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		double lootTableChosen = 0;
		lootTableChosen = Mth.nextInt(RandomSource.create(), 0, (int) (JaumlConfigLib.getArrayLength("mlb", "special_bags", "events") - 1));
		if (world instanceof ServerLevel _level)
			_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
					JaumlConfigLib.getArrayElement("mlb", "special_bags", "events", ((int) lootTableChosen)));
		if (JaumlConfigLib.getBooleanValue("mlb", "special_bags", "enable_sound")) {
			lootTableChosen = Mth.nextInt(RandomSource.create(), 0, 2);
			if (lootTableChosen == 2) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("mob_loot_bags:dramatic")), SoundSource.RECORDS, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("mob_loot_bags:dramatic")), SoundSource.RECORDS, 1, 1, false);
					}
				}
			} else if (lootTableChosen == 1) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("mob_loot_bags:lootbag_sfx_1")), SoundSource.RECORDS, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("mob_loot_bags:lootbag_sfx_1")), SoundSource.RECORDS, 1, 1, false);
					}
				}
			} else {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("mob_loot_bags:lootbag_sfx_2")), SoundSource.RECORDS, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("mob_loot_bags:lootbag_sfx_2")), SoundSource.RECORDS, 1, 1, false);
					}
				}
			}
		}
		if (entity instanceof Player _player) {
			ItemStack _stktoremove = itemstack;
			_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
		}
	}
}
