package tn.naizo.moblootbags.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class WhiteBlackListConfigConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_WHITE;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELIST;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_BLACK;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;
	static {
		BUILDER.push("Whitelist");
		ENABLE_WHITE = BUILDER.define("enable", false);
		WHITELIST = BUILDER.defineList("whitelist", List.of(" "), entry -> true);
		BUILDER.pop();
		BUILDER.push("Blacklist");
		ENABLE_BLACK = BUILDER.define("enable", false);
		BLACKLIST = BUILDER.defineList("blacklist", List.of(" "), entry -> true);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
