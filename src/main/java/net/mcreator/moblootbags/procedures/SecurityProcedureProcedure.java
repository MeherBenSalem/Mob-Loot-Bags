package net.mcreator.moblootbags.procedures;

import net.minecraft.world.item.ItemStack;

import net.mcreator.moblootbags.init.MobLootBagsModItems;
import net.mcreator.moblootbags.configuration.MainConfigFileConfiguration;

public class SecurityProcedureProcedure {
	public static String execute(ItemStack itemstack) {
		String loot_table_name = "";
		loot_table_name = "minecraft:chests/end_city_treasure";
		if (itemstack.getItem() == MobLootBagsModItems.COMMONLOOTBAG.get()) {
			for (String stringiterator : MainConfigFileConfiguration.COMMON_LT_NAME.get()) {
				loot_table_name = stringiterator;
				break;
			}
		} else if (itemstack.getItem() == MobLootBagsModItems.UNCOMMONLOOTBAG.get()) {
			for (String stringiterator : MainConfigFileConfiguration.UNCOMMON_LT_NAME.get()) {
				loot_table_name = stringiterator;
				break;
			}
		} else if (itemstack.getItem() == MobLootBagsModItems.RARELOOTBAG.get()) {
			for (String stringiterator : MainConfigFileConfiguration.RARE_LT_NAME.get()) {
				loot_table_name = stringiterator;
				break;
			}
		} else if (itemstack.getItem() == MobLootBagsModItems.EPICLOOTBAG.get()) {
			for (String stringiterator : MainConfigFileConfiguration.EPIC_LT_NAME.get()) {
				loot_table_name = stringiterator;
				break;
			}
		} else if (itemstack.getItem() == MobLootBagsModItems.LEGENDARYLOOTBAG.get()) {
			for (String stringiterator : MainConfigFileConfiguration.LEGENDARY_LT_NAME.get()) {
				loot_table_name = stringiterator;
				break;
			}
		}
		return loot_table_name;
	}
}
