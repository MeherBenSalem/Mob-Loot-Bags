package tn.naizo.moblootbags.procedures;

import tn.naizo.moblootbags.configuration.MainConfigFileConfiguration;

public class ReturnLengthOfEpicProcedure {
	public static double execute() {
		double length = 0;
		length = 0;
		for (String stringiterator : MainConfigFileConfiguration.EPIC_LT_NAME.get()) {
			length = length + 1;
		}
		return length;
	}
}