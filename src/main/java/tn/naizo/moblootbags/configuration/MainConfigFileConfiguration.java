package tn.naizo.moblootbags.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class MainConfigFileConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> COMMON_LT_NAME;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> UNCOMMON_LT_NAME;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> RARE_LT_NAME;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EPIC_LT_NAME;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LEGENDARY_LT_NAME;
	public static final ForgeConfigSpec.ConfigValue<String> DEFAULT_LT_NAME;
	public static final ForgeConfigSpec.ConfigValue<Double> COMMON_DROP_RATE;
	public static final ForgeConfigSpec.ConfigValue<Double> UNCOMMON_DROP_RATE;
	public static final ForgeConfigSpec.ConfigValue<Double> RARE_DROP_RATE;
	public static final ForgeConfigSpec.ConfigValue<Double> EPIC_DROP_RATE;
	public static final ForgeConfigSpec.ConfigValue<Double> LEGENDARY_DROP_RATE;
	public static final ForgeConfigSpec.ConfigValue<Double> DROP_CHANCE_OVERALL;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LUCKY_LIST;
	static {
		BUILDER.push("Loot Tables Rarity Config");
		COMMON_LT_NAME = BUILDER.defineList("common_lt_name", List.of("mob_loot_bags:lootbags/common"), entry -> true);
		UNCOMMON_LT_NAME = BUILDER.defineList("uncommon_lt_name", List.of("mob_loot_bags:un_common"), entry -> true);
		RARE_LT_NAME = BUILDER.defineList("rare_lt_name", List.of("mob_loot_bags:lootbags/rare"), entry -> true);
		EPIC_LT_NAME = BUILDER.defineList("epic_lt_name", List.of("mob_loot_bags:lootbags/epic"), entry -> true);
		LEGENDARY_LT_NAME = BUILDER.defineList("legendary_lt_name", List.of("mob_loot_bags:legendary_loot_table"), entry -> true);
		DEFAULT_LT_NAME = BUILDER.comment("if you didn't setup any loot table this is the default one").define("default_lt_name", "minecraft:chests/end_city_treasure");
		BUILDER.pop();
		BUILDER.push("Loot Bags Drop Rate Config");
		COMMON_DROP_RATE = BUILDER.define("common_drop_rate", (double) 60);
		UNCOMMON_DROP_RATE = BUILDER.define("uncommon_drop_rate", (double) 25);
		RARE_DROP_RATE = BUILDER.define("rare_drop_rate", (double) 10);
		EPIC_DROP_RATE = BUILDER.define("epic_drop_rate", (double) 4);
		LEGENDARY_DROP_RATE = BUILDER.define("legendary_drop_rate", (double) 1);
		DROP_CHANCE_OVERALL = BUILDER.define("drop_chance_overall", (double) 80);
		BUILDER.pop();
		BUILDER.push("Events Config");
		LUCKY_LIST = BUILDER.defineList("lucky_list", List.of("summon minecraft:warden ~ ~ ~", "/place structure minecraft:ancient_city", "weather thunder", "tp @p ~ ~-40 ~", "/effect give @p minecraft:blindness 10",
				"summon minecraft:tnt ~ ~ ~ {Fuse:40}", "summon minecraft:wither ~ ~ ~", "summon minecraft:zombie ~ ~ ~ {IsBaby:1}", "summon minecraft:firework_rocket ~ ~ ~ {Life:20,LifeTime:40}", "xp add @p 50", "xp add @p 100", "xp add @p 500",
				"place structure minecraft:village_plains", "place structure minecraft:village_desert  ", "place structure minecraft:ruined_portal  ", "place structure minecraft:pillager_outpost  ", "place structure minecraft:jungle_temple  "),
				entry -> true);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
