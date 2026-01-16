package tn.naizo.moblootbags.procedures;

import tn.naizo.moblootbags.init.MobLootBagsModItems;
import tn.naizo.jauml.JaumlConfigLib;

import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.Nullable;

@EventBusSubscriber
public class OnMobDeathDropRateProcedure {
	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity(), event.getSource().getEntity());
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		execute(null, world, x, y, z, entity, sourceentity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		double itemDroped = 0;
		boolean continueDrop = false;
		continueDrop = false;
		if (sourceentity instanceof Player || sourceentity instanceof ServerPlayer) {
			if (JaumlConfigLib.getBooleanValue("mlb", "whitelist", "enable")) {
				continueDrop = false;
				if (JaumlConfigLib.stringExistsInArray("mlb", "whitelist", "mobs", (BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString()))) {
					continueDrop = true;
				}
			} else if (JaumlConfigLib.getBooleanValue("mlb", "blacklist", "enable")) {
				continueDrop = true;
				if (JaumlConfigLib.stringExistsInArray("mlb", "blacklist", "mobs", (BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString()))) {
					continueDrop = false;
				}
			} else {
				continueDrop = true;
			}
		}
		if (continueDrop) {
			if (Mth.nextInt(RandomSource.create(), 1, 100) <= JaumlConfigLib.getNumberValue("mlb", "drop_rates", "drop_chance_overall")) {
				itemDroped = Mth.nextDouble(RandomSource.create(), 1, 100);
				if (itemDroped <= JaumlConfigLib.getNumberValue("mlb", "drop_rates", "legendary_drop_rate")) {
					if (world instanceof ServerLevel _level) {
						ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.LEGENDARYLOOTBAG.get()));
						entityToSpawn.setPickUpDelay(10);
						_level.addFreshEntity(entityToSpawn);
					}
				} else if (itemDroped <= JaumlConfigLib.getNumberValue("mlb", "drop_rates", "epic_drop_rate")) {
					if (world instanceof ServerLevel _level) {
						ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.EPICLOOTBAG.get()));
						entityToSpawn.setPickUpDelay(10);
						_level.addFreshEntity(entityToSpawn);
					}
				} else if (itemDroped <= JaumlConfigLib.getNumberValue("mlb", "drop_rates", "rare_drop_rate")) {
					if (Mth.nextInt(RandomSource.create(), 1, 100) <= JaumlConfigLib.getNumberValue("mlb", "drop_rates", "timed_drop_rate")) {
						if (world instanceof ServerLevel _level) {
							ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.TIMED_LOOT_BAG.get()));
							entityToSpawn.setPickUpDelay(10);
							_level.addFreshEntity(entityToSpawn);
						}
					} else {
						if (world instanceof ServerLevel _level) {
							ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.RARELOOTBAG.get()));
							entityToSpawn.setPickUpDelay(10);
							_level.addFreshEntity(entityToSpawn);
						}
					}
				} else if (itemDroped <= JaumlConfigLib.getNumberValue("mlb", "drop_rates", "uncommon_drop_rate")) {
					if (world instanceof ServerLevel _level) {
						ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.UNCOMMONLOOTBAG.get()));
						entityToSpawn.setPickUpDelay(10);
						_level.addFreshEntity(entityToSpawn);
					}
				} else {
					if (world instanceof ServerLevel _level) {
						ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.COMMONLOOTBAG.get()));
						entityToSpawn.setPickUpDelay(10);
						_level.addFreshEntity(entityToSpawn);
					}
				}
				if (JaumlConfigLib.getBooleanValue("mlb", "special_bags", "enable_cursed_bag")) {
					if (Mth.nextInt(RandomSource.create(), 0, 100) <= JaumlConfigLib.getNumberValue("mlb", "drop_rates", "cursed_drop_rate")) {
						if (world instanceof ServerLevel _level) {
							ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.CURSED_LOOTBAG.get()));
							entityToSpawn.setPickUpDelay(10);
							_level.addFreshEntity(entityToSpawn);
						}
					}
				}
			}
		}
	}
}