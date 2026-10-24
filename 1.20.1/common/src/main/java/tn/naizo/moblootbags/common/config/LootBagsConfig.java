package tn.naizo.moblootbags.common.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.content.BagType;

/** Loads and holds the four config files under {@code config/mob_loot_bags/}. */
public final class LootBagsConfig {
    private static DropsConfig drops = DropsConfig.from(new JsonObject());
    private static BagsConfig bags = BagsConfig.from(new JsonObject());
    private static MachinesConfig machines = MachinesConfig.from(new JsonObject());
    private static ClientConfig client = ClientConfig.from(new JsonObject());
    private static Path dir = Path.of("config", "mob_loot_bags");

    private LootBagsConfig() {
    }

    public static DropsConfig drops() {
        return drops;
    }

    public static BagsConfig bags() {
        return bags;
    }

    public static MachinesConfig machines() {
        return machines;
    }

    public static ClientConfig client() {
        return client;
    }

    /** Loads (or creates) every config file. Safe to call again to reload. */
    public static synchronized void load(Path configRoot) {
        dir = configRoot.resolve(MobLootBagsCommon.MOD_ID);
        Path legacy = configRoot.resolve("mlb");
        boolean migrate = !Files.exists(dir) && Files.isDirectory(legacy);

        CommentedJson dropsSpec = DropsConfig.spec();
        CommentedJson bagsSpec = BagsConfig.spec();
        CommentedJson machinesSpec = MachinesConfig.spec();
        if (migrate) {
            LegacyMigration.migrate(legacy, dir, dropsSpec, bagsSpec, machinesSpec);
        }
        drops = DropsConfig.from(dropsSpec.load(dir.resolve("drops.json")));
        bags = BagsConfig.from(bagsSpec.load(dir.resolve("bags.json")));
        machines = MachinesConfig.from(machinesSpec.load(dir.resolve("machines.json")));
        client = ClientConfig.from(ClientConfig.spec().load(dir.resolve("client.json")));
        MobLootBagsCommon.LOG.info("Loaded config from {}", dir);
    }

    public static Path dir() {
        return dir;
    }

    /** Reads 1.x {@code config/mlb/} files and writes their customised values into the 2.0 files, once. */
    static final class LegacyMigration {
        private LegacyMigration() {
        }

        static void migrate(Path legacy, Path target, CommentedJson dropsSpec, CommentedJson bagsSpec, CommentedJson machinesSpec) {
            try {
                JsonObject drops = dropsSpec.defaults();
                JsonObject bagsJson = bagsSpec.defaults();
                JsonObject machinesJson = machinesSpec.defaults();
                boolean changed = false;

                JsonObject rates = read(legacy.resolve("drop_rates.json"));
                if (rates != null && !matches(rates, legacyRateDefaults())) {
                    double overall = Json.num(rates, "drop_chance_overall", 80) / 100.0;
                    drops.addProperty("hostile_chance", clamp01(overall));
                    drops.addProperty("passive_chance", clamp01(overall));
                    // 1.x thresholds are cumulative percentages: legendary <= 1, epic <= 4, rare <= 10, uncommon <= 25.
                    double legendary = Json.num(rates, "legendary_drop_rate", 1);
                    double epic = Math.max(0, Json.num(rates, "epic_drop_rate", 4) - legendary);
                    double rare = Math.max(0, Json.num(rates, "rare_drop_rate", 10) - legendary - epic);
                    double uncommon = Math.max(0, Json.num(rates, "uncommon_drop_rate", 25) - legendary - epic - rare);
                    double common = Math.max(0, 100 - legendary - epic - rare - uncommon);
                    JsonObject weights = Json.obj("common", common * 10, "uncommon", uncommon * 10, "rare", rare * 10,
                            "epic", epic * 10, "legendary", legendary * 10, "mythic", 0);
                    drops.add("tier_weights", weights);
                    JsonObject specials = drops.getAsJsonObject("specials");
                    specials.addProperty("cursed", clamp01(Json.num(rates, "cursed_drop_rate", 1) / 100.0));
                    double rareShare = (rare + 0.0) / 100.0;
                    specials.addProperty("hourglass", clamp01(Json.num(rates, "timed_drop_rate", 20) / 100.0 * rareShare));
                    changed = true;
                }

                JsonObject whitelist = read(legacy.resolve("whitelist.json"));
                JsonObject blacklist = read(legacy.resolve("blacklist.json"));
                if (whitelist != null && Json.bool(whitelist, "enable", false)) {
                    drops.add("filter", Json.obj("mode", "whitelist", "mobs", whitelist.get("mobs").deepCopy()));
                    drops.addProperty("passive_chance", drops.get("hostile_chance").getAsDouble());
                    changed = true;
                } else if (blacklist != null && Json.bool(blacklist, "enable", false)) {
                    drops.add("filter", Json.obj("mode", "blacklist", "mobs", blacklist.get("mobs").deepCopy()));
                    changed = true;
                }

                JsonObject special = read(legacy.resolve("special_bags.json"));
                if (special != null) {
                    JsonObject cursed = bagsJson.getAsJsonObject("cursed");
                    if (!Json.bool(special, "enable_cursed_bag", true)) {
                        drops.getAsJsonObject("specials").addProperty("cursed", 0.0);
                        changed = true;
                    }
                    if (!Json.bool(special, "enable_sound", true)) {
                        cursed.addProperty("sound", false);
                        changed = true;
                    }
                    List<String> events = Json.strings(special, "events");
                    if (!events.isEmpty() && !events.equals(LEGACY_EVENTS)) {
                        cursed.add("chaos_commands", special.get("events").deepCopy());
                        changed = true;
                    }
                }

                JsonObject tables = read(legacy.resolve("loot_tables.json"));
                if (tables != null) {
                    JsonObject overrides = bagsJson.getAsJsonObject("loot_tables");
                    changed |= copyTable(tables, "common_lt_name", "mob_loot_bags:lootbags/common", overrides, BagType.COMMON);
                    changed |= copyTable(tables, "uncommon_lt_name", "mob_loot_bags:un_common", overrides, BagType.UNCOMMON);
                    changed |= copyTable(tables, "rare_lt_name", "mob_loot_bags:lootbags/rare", overrides, BagType.RARE);
                    changed |= copyTable(tables, "epic_lt_name", "mob_loot_bags:lootbags/epic", overrides, BagType.EPIC);
                    changed |= copyTable(tables, "legendary_lt_name", "mob_loot_bags:legendary_loot_table", overrides, BagType.LEGENDARY);
                    changed |= copyTable(tables, "summoning_lt_name", "mob_loot_bags:summoning_loot_table", overrides, BagType.SUMMONING);
                    changed |= copyTable(tables, "locked_lt_name", "mob_loot_bags:locked_loot_table", overrides, BagType.LOCKED);
                    changed |= copyTable(tables, "timed_lt_name", "mob_loot_bags:time_warped_loot_table", overrides, BagType.HOURGLASS);
                }

                JsonObject recycler = read(legacy.resolve("bag_recycler.json"));
                if (recycler != null) {
                    JsonObject xp = machinesJson.getAsJsonObject("recycler").getAsJsonObject("bag_xp");
                    String[][] keys = {{"common_lb", "common"}, {"uncommon_lb", "uncommon"}, {"rare_lb", "rare"}, {"epic_lb", "epic"},
                            {"legendary_lb", "legendary"}, {"cursed_lb", "cursed"}, {"timed_lb", "hourglass"},
                            {"enchanted_timed_lb", "ripe_hourglass"}, {"locked_lb", "locked"}, {"summoning_lb", "summoning"}};
                    for (String[] key : keys) {
                        JsonElement value = recycler.get(key[0]);
                        if (value != null && value.isJsonPrimitive() && value.getAsInt() != xp.get(key[1]).getAsInt()) {
                            xp.addProperty(key[1], value.getAsInt());
                            changed = true;
                        }
                    }
                }

                if (!changed) {
                    MobLootBagsCommon.LOG.info("Found 1.x config in {} with default values; starting from the 2.0 defaults", legacy);
                    return;
                }
                Files.createDirectories(target);
                Files.writeString(target.resolve("drops.json"), dropsSpec.write(drops), StandardCharsets.UTF_8);
                Files.writeString(target.resolve("bags.json"), bagsSpec.write(bagsJson), StandardCharsets.UTF_8);
                Files.writeString(target.resolve("machines.json"), machinesSpec.write(machinesJson), StandardCharsets.UTF_8);
                MobLootBagsCommon.LOG.info("Migrated customised 1.x settings from {} to {}", legacy, target);
            } catch (IOException | RuntimeException e) {
                MobLootBagsCommon.LOG.warn("Could not migrate 1.x config from {}: {}", legacy, e.toString());
            }
        }

        private static boolean copyTable(JsonObject legacy, String key, String legacyDefault, JsonObject overrides, BagType type) {
            List<String> values = Json.strings(legacy, key);
            if (values.isEmpty() || (values.size() == 1 && values.get(0).equals(legacyDefault))) {
                return false;
            }
            overrides.add(type.key(), legacy.get(key).isJsonArray() ? legacy.get(key).deepCopy() : Json.arr(values.get(0)));
            return true;
        }

        private static JsonObject legacyRateDefaults() {
            return Json.obj("drop_chance_overall", 80, "common_drop_rate", 60, "uncommon_drop_rate", 25, "rare_drop_rate", 10,
                    "epic_drop_rate", 4, "legendary_drop_rate", 1, "cursed_drop_rate", 1, "timed_drop_rate", 20);
        }

        private static boolean matches(JsonObject actual, JsonObject defaults) {
            for (String key : defaults.keySet()) {
                if (Math.abs(Json.num(actual, key, defaults.get(key).getAsDouble()) - defaults.get(key).getAsDouble()) > 1e-9) {
                    return false;
                }
            }
            return true;
        }

        private static JsonObject read(Path file) {
            try {
                if (!Files.exists(file)) {
                    return null;
                }
                JsonElement parsed = CommentedJson.parse(Files.readString(file, StandardCharsets.UTF_8));
                return parsed != null && parsed.isJsonObject() ? parsed.getAsJsonObject() : null;
            } catch (IOException | RuntimeException e) {
                return null;
            }
        }

        private static double clamp01(double value) {
            return Math.max(0.0, Math.min(1.0, value));
        }

        private static final List<String> LEGACY_EVENTS = List.of(
                "summon minecraft:warden ~ ~ ~",
                "place structure minecraft:ancient_city",
                "weather thunder",
                "tp @p ~ ~-40 ~",
                "effect give @p minecraft:blindness 10",
                "summon minecraft:tnt ~ ~ ~ {Fuse:40}",
                "summon minecraft:wither ~ ~ ~",
                "summon minecraft:zombie ~ ~ ~ {IsBaby:1}",
                "summon minecraft:firework_rocket ~ ~ ~ {Life:20,LifeTime:40}",
                "xp add @p 50",
                "xp add @p 100",
                "xp add @p 500",
                "place structure minecraft:village_plains",
                "place structure minecraft:village_desert",
                "place structure minecraft:ruined_portal",
                "place structure minecraft:pillager_outpost",
                "place structure minecraft:jungle_temple");
    }
}
