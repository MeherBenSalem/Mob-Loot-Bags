package net.mcreator.moblootbags.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ItemsConfigConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	public static final ForgeConfigSpec.ConfigValue<Double> MAX_AMOUNT_RARE;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> VALUABLE_ITEMS;
	public static final ForgeConfigSpec.ConfigValue<Double> MAX_AMOUNT_COMMUN;
	static {
		BUILDER.push("Valuable");
		MAX_AMOUNT_RARE = BUILDER.define("max_amount_rare", (double) 5);
		VALUABLE_ITEMS = BUILDER.defineList("valuable_items", List.of("gold_ingot", "emerald", "diamond", "smithing_template"), entry -> true);
		MAX_AMOUNT_COMMUN = BUILDER.define("max_amount_commun", (double) 20);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
