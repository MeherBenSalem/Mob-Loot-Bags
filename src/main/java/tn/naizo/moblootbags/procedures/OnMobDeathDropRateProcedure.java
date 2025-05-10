package tn.naizo.moblootbags.procedures;

import tn.naizo.moblootbags.init.MobLootBagsModItems;
import tn.naizo.moblootbags.configuration.WhiteBlackListConfigConfiguration;
import tn.naizo.moblootbags.configuration.MainConfigFileConfiguration;
import tn.naizo.moblootbags.MobLootBagsMod;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class OnMobDeathDropRateProcedure {
	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event != null && event.getEntity() != null) {
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
			if (WhiteBlackListConfigConfiguration.ENABLE_WHITE.get()) {
				for (String stringiterator : WhiteBlackListConfigConfiguration.WHITELIST.get()) {
					if ((ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString()).equals(stringiterator)) {
						continueDrop = true;
						break;
					}
				}
			} else if (WhiteBlackListConfigConfiguration.ENABLE_BLACK.get()) {
				continueDrop = true;
				for (String stringiterator : WhiteBlackListConfigConfiguration.BLACKLIST.get()) {
					if ((ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString()).equals(stringiterator)) {
						continueDrop = false;
						break;
					}
				}
			} else {
				continueDrop = true;
			}
		}
		MobLootBagsMod.LOGGER.info(("" + continueDrop));
		if (continueDrop) {
			if (Mth.nextInt(RandomSource.create(), 1, 100) <= (double) MainConfigFileConfiguration.DROP_CHANCE_OVERALL.get()) {
				itemDroped = Mth.nextDouble(RandomSource.create(), 1, 100);
				if (itemDroped <= (double) MainConfigFileConfiguration.LEGENDARY_DROP_RATE.get()) {
					MobLootBagsMod.LOGGER.info("legendary");
					if (world instanceof ServerLevel _level) {
						ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.LEGENDARYLOOTBAG.get()));
						entityToSpawn.setPickUpDelay(10);
						_level.addFreshEntity(entityToSpawn);
					}
				} else if (itemDroped <= (double) MainConfigFileConfiguration.EPIC_DROP_RATE.get()) {
					MobLootBagsMod.LOGGER.info("epic");
					if (world instanceof ServerLevel _level) {
						ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.EPICLOOTBAG.get()));
						entityToSpawn.setPickUpDelay(10);
						_level.addFreshEntity(entityToSpawn);
					}
				} else if (itemDroped <= (double) MainConfigFileConfiguration.RARE_DROP_RATE.get()) {
					if (Mth.nextInt(RandomSource.create(), 1, 100) <= (double) MainConfigFileConfiguration.TIMED_DROP_RATE.get()) {
						MobLootBagsMod.LOGGER.info("timed");
						if (world instanceof ServerLevel _level) {
							ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.TIMED_LOOT_BAG.get()));
							entityToSpawn.setPickUpDelay(10);
							_level.addFreshEntity(entityToSpawn);
						}
					} else {
						MobLootBagsMod.LOGGER.info("rare");
						if (world instanceof ServerLevel _level) {
							ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.RARELOOTBAG.get()));
							entityToSpawn.setPickUpDelay(10);
							_level.addFreshEntity(entityToSpawn);
						}
					}
				} else if (itemDroped <= (double) MainConfigFileConfiguration.UNCOMMON_DROP_RATE.get()) {
					MobLootBagsMod.LOGGER.info("uncommon");
					if (world instanceof ServerLevel _level) {
						ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.UNCOMMONLOOTBAG.get()));
						entityToSpawn.setPickUpDelay(10);
						_level.addFreshEntity(entityToSpawn);
					}
				} else {
					MobLootBagsMod.LOGGER.info("common");
					if (world instanceof ServerLevel _level) {
						ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.COMMONLOOTBAG.get()));
						entityToSpawn.setPickUpDelay(10);
						_level.addFreshEntity(entityToSpawn);
					}
				}
				if (MainConfigFileConfiguration.ENABLE_CURSED_BAG.get()) {
					if (Mth.nextInt(RandomSource.create(), 0, 100) <= (double) MainConfigFileConfiguration.CURSED_DROP_RATE.get()) {
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
