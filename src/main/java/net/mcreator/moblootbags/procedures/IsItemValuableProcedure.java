package net.mcreator.moblootbags.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.ItemStack;

import net.mcreator.moblootbags.configuration.ItemsConfigConfiguration;

public class IsItemValuableProcedure {
	public static double execute(ItemStack itemstackiterator) {
		double returnVal = 0;
		returnVal = (double) ItemsConfigConfiguration.MAX_AMOUNT_COMMUN.get();
		for (String stringiterator : ItemsConfigConfiguration.VALUABLE_ITEMS.get()) {
			if ((ForgeRegistries.ITEMS.getKey(itemstackiterator.getItem()).toString()).contains(stringiterator)) {
				returnVal = (double) ItemsConfigConfiguration.MAX_AMOUNT_RARE.get();
				break;
			}
		}
		return returnVal;
	}
}
