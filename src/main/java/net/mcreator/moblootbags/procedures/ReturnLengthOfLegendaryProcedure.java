package net.mcreator.moblootbags.procedures;

import net.mcreator.moblootbags.configuration.MainConfigFileConfiguration;

public class ReturnLengthOfLegendaryProcedure {
	public static double execute() {
		double length = 0;
		length = 0;
		for (String stringiterator : MainConfigFileConfiguration.LEGENDARY_LT_NAME.get()) {
			length = length + 1;
		}
		return length;
	}
}