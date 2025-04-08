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
	public static final ForgeConfigSpec.ConfigValue<String> SUMMONING_LT_NAME;
	public static final ForgeConfigSpec.ConfigValue<String> LOCKED_LT_NAME;
	public static final ForgeConfigSpec.ConfigValue<String> DEFAULT_LT_NAME;
	public static final ForgeConfigSpec.ConfigValue<Double> COMMON_DROP_RATE;
	public static final ForgeConfigSpec.ConfigValue<Double> UNCOMMON_DROP_RATE;
	public static final ForgeConfigSpec.ConfigValue<Double> RARE_DROP_RATE;
	public static final ForgeConfigSpec.ConfigValue<Double> EPIC_DROP_RATE;
	public static final ForgeConfigSpec.ConfigValue<Double> LEGENDARY_DROP_RATE;
	public static final ForgeConfigSpec.ConfigValue<Double> CURSED_DROP_RATE;
	public static final ForgeConfigSpec.ConfigValue<Double> TIMED_DROP_RATE;
	public static final ForgeConfigSpec.ConfigValue<Double> DROP_CHANCE_OVERALL;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_CURSED_BAG;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_SOUND;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LUCKY_LIST;
	public static final ForgeConfigSpec.ConfigValue<Double> COMMON_XP;
	public static final ForgeConfigSpec.ConfigValue<Double> UNCOMMON_XP;
	public static final ForgeConfigSpec.ConfigValue<Double> RARE_XP;
	public static final ForgeConfigSpec.ConfigValue<Double> EPIC_XP;
	public static final ForgeConfigSpec.ConfigValue<Double> LEGENDARY_XP;
	public static final ForgeConfigSpec.ConfigValue<Double> CURSED_XP;
	public static final ForgeConfigSpec.ConfigValue<Double> TIMED_XP;
	public static final ForgeConfigSpec.ConfigValue<Double> TIMED_ENCH_XP;
	public static final ForgeConfigSpec.ConfigValue<Double> LOCKED_XP;
	public static final ForgeConfigSpec.ConfigValue<Double> SUMMONING_XP;
	static {
		BUILDER.push("Loot Tables Rarity Config");
		COMMON_LT_NAME = BUILDER.defineList("common_lt_name", List.of("mob_loot_bags:lootbags/common"), entry -> true);
		UNCOMMON_LT_NAME = BUILDER.defineList("uncommon_lt_name", List.of("mob_loot_bags:un_common"), entry -> true);
		RARE_LT_NAME = BUILDER.defineList("rare_lt_name", List.of("mob_loot_bags:lootbags/rare"), entry -> true);
		EPIC_LT_NAME = BUILDER.defineList("epic_lt_name", List.of("mob_loot_bags:lootbags/epic"), entry -> true);
		LEGENDARY_LT_NAME = BUILDER.defineList("legendary_lt_name", List.of("mob_loot_bags:legendary_loot_table"), entry -> true);
		SUMMONING_LT_NAME = BUILDER.define("summoning_lt_name", "mob_loot_bags:summoning_loot_table");
		LOCKED_LT_NAME = BUILDER.define("locked_lt_name", "mob_loot_bags:locked_loot_table");
		DEFAULT_LT_NAME = BUILDER.comment("if you didn't setup any loot table this is the default one").define("default_lt_name", "minecraft:chests/end_city_treasure");
		BUILDER.pop();
		BUILDER.push("Loot Bags Drop Rate Config");
		COMMON_DROP_RATE = BUILDER.define("common_drop_rate", (double) 60);
		UNCOMMON_DROP_RATE = BUILDER.define("uncommon_drop_rate", (double) 25);
		RARE_DROP_RATE = BUILDER.define("rare_drop_rate", (double) 10);
		EPIC_DROP_RATE = BUILDER.define("epic_drop_rate", (double) 4);
		LEGENDARY_DROP_RATE = BUILDER.define("legendary_drop_rate", (double) 1);
		CURSED_DROP_RATE = BUILDER.define("cursed_drop_rate", (double) 1);
		TIMED_DROP_RATE = BUILDER.comment("this is calculated after the calculation of the rare lootbag drop rate").define("timed_drop_rate", (double) 20);
		DROP_CHANCE_OVERALL = BUILDER.define("drop_chance_overall", (double) 80);
		BUILDER.pop();
		BUILDER.push("Events Config");
		ENABLE_CURSED_BAG = BUILDER.comment("enables cursed lootbag drops").define("enable_cursed_bag", true);
		ENABLE_SOUND = BUILDER.define("enable_sound", true);
		LUCKY_LIST = BUILDER.defineList("lucky_list", List.of("summon minecraft:warden ~ ~ ~", "/place structure minecraft:ancient_city", "weather thunder", "tp @p ~ ~-40 ~", "/effect give @p minecraft:blindness 10",
				"summon minecraft:tnt ~ ~ ~ {Fuse:40}", "summon minecraft:wither ~ ~ ~", "summon minecraft:zombie ~ ~ ~ {IsBaby:1}", "summon minecraft:firework_rocket ~ ~ ~ {Life:20,LifeTime:40}", "xp add @p 50", "xp add @p 100", "xp add @p 500",
				"place structure minecraft:village_plains", "place structure minecraft:village_desert  ", "place structure minecraft:ruined_portal  ", "place structure minecraft:pillager_outpost  ", "place structure minecraft:jungle_temple  "),
				entry -> true);
		BUILDER.pop();
		BUILDER.push("Bag Recycler Settings");
		COMMON_XP = BUILDER.define("common_lb", (double) 10);
		UNCOMMON_XP = BUILDER.define("uncommon_lb", (double) 20);
		RARE_XP = BUILDER.define("rare_lb", (double) 40);
		EPIC_XP = BUILDER.define("epic_lb", (double) 80);
		LEGENDARY_XP = BUILDER.define("legendary_lb", (double) 200);
		CURSED_XP = BUILDER.define("cursed_lb", (double) 60);
		TIMED_XP = BUILDER.define("timed_lb", (double) 40);
		TIMED_ENCH_XP = BUILDER.define("enchanted_timed_lb", (double) 200);
		LOCKED_XP = BUILDER.define("locked_lb", (double) 40);
		SUMMONING_XP = BUILDER.define("summoning_lb", (double) 40);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
