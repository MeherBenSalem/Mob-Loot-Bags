package net.mcreator.moblootbags.procedures;

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

import net.mcreator.moblootbags.init.MobLootBagsModItems;
import net.mcreator.moblootbags.configuration.MainConfigFileConfiguration;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class OnMobDeathDropRateProcedure {
	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getSource().getEntity());
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity sourceentity) {
		execute(null, world, x, y, z, sourceentity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity sourceentity) {
		if (sourceentity == null)
			return;
		double itemDroped = 0;
		if (sourceentity instanceof Player || sourceentity instanceof ServerPlayer) {
			if (Mth.nextInt(RandomSource.create(), 0, 100) <= (double) MainConfigFileConfiguration.DROP_CHANCE_OVERALL.get()) {
				itemDroped = Mth.nextDouble(RandomSource.create(), 1, 100);
				if (itemDroped <= (double) MainConfigFileConfiguration.LEGENDARY_DROP_RATE.get()) {
					if (world instanceof ServerLevel _level) {
						ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.LEGENDARYLOOTBAG.get()));
						entityToSpawn.setPickUpDelay(10);
						_level.addFreshEntity(entityToSpawn);
					}
				} else if (itemDroped <= (double) MainConfigFileConfiguration.EPIC_DROP_RATE.get()) {
					if (world instanceof ServerLevel _level) {
						ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.EPICLOOTBAG.get()));
						entityToSpawn.setPickUpDelay(10);
						_level.addFreshEntity(entityToSpawn);
					}
				} else if (itemDroped <= (double) MainConfigFileConfiguration.RARE_DROP_RATE.get()) {
					if (world instanceof ServerLevel _level) {
						ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(MobLootBagsModItems.RARELOOTBAG.get()));
						entityToSpawn.setPickUpDelay(10);
						_level.addFreshEntity(entityToSpawn);
					}
				} else if (itemDroped <= (double) MainConfigFileConfiguration.UNCOMMON_DROP_RATE.get()) {
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
			}
		}
	}
}
