package tn.naizo.moblootbags.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.RandomSource;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class MobLootBagsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = Path.of("config", "mlb");

    private static JsonObject lootTables;
    private static JsonObject dropRates;
    private static JsonObject specialBags;
    private static JsonObject whitelist;
    private static JsonObject blacklist;
    private static JsonObject recycler;

    private MobLootBagsConfig() {
    }

    public static void init() {
        lootTables = loadSection("loot_tables.json", defaultLootTables());
        dropRates = loadSection("drop_rates.json", defaultDropRates());
        specialBags = loadSection("special_bags.json", defaultSpecialBags());
        whitelist = loadSection("whitelist.json", defaultMobList());
        blacklist = loadSection("blacklist.json", defaultMobList());
        recycler = loadSection("bag_recycler.json", defaultRecycler());
    }

    public static String pickTierLootTable(String tier, RandomSource random) {
        String key = switch (tier) {
            case "common" -> "common_lt_name";
            case "uncommon" -> "uncommon_lt_name";
            case "rare" -> "rare_lt_name";
            case "epic" -> "epic_lt_name";
            case "legendary" -> "legendary_lt_name";
            default -> "default_lt_name";
        };
        List<String> values = getStringList(lootTables, key);
        if (!values.isEmpty()) {
            return values.get(random.nextInt(values.size()));
        }
        return getString(lootTables, "default_lt_name", "minecraft:chests/end_city_treasure");
    }

    public static String lockedLootTable() {
        return getString(lootTables, "locked_lt_name", "mob_loot_bags:locked_loot_table");
    }

    public static String summoningLootTable() {
        return getString(lootTables, "summoning_lt_name", "mob_loot_bags:summoning_loot_table");
    }

    public static String timedLootTable() {
        return "mob_loot_bags:time_warped_loot_table";
    }

    public static boolean cursedEnabled() {
        return getBoolean(specialBags, "enable_cursed_bag", true);
    }

    public static boolean cursedSoundEnabled() {
        return getBoolean(specialBags, "enable_sound", true);
    }

    public static List<String> cursedEvents() {
        List<String> values = getStringList(specialBags, "events");
        if (!values.isEmpty()) {
            return values;
        }
        return getStringList(defaultSpecialBags(), "events");
    }

    public static int dropChanceOverall() {
        return getInt(dropRates, "drop_chance_overall", 80);
    }

    public static int commonDropRate() {
        return getInt(dropRates, "common_drop_rate", 60);
    }

    public static int uncommonDropRate() {
        return getInt(dropRates, "uncommon_drop_rate", 25);
    }

    public static int rareDropRate() {
        return getInt(dropRates, "rare_drop_rate", 10);
    }

    public static int epicDropRate() {
        return getInt(dropRates, "epic_drop_rate", 4);
    }

    public static int legendaryDropRate() {
        return getInt(dropRates, "legendary_drop_rate", 1);
    }

    public static int timedDropRate() {
        return getInt(dropRates, "timed_drop_rate", 20);
    }

    public static int cursedDropRate() {
        return getInt(dropRates, "cursed_drop_rate", 1);
    }

    public static boolean whitelistEnabled() {
        return getBoolean(whitelist, "enable", false);
    }

    public static boolean blacklistEnabled() {
        return getBoolean(blacklist, "enable", false);
    }

    public static boolean whitelistContains(String mobId) {
        return getStringList(whitelist, "mobs").contains(mobId);
    }

    public static boolean blacklistContains(String mobId) {
        return getStringList(blacklist, "mobs").contains(mobId);
    }

    public static JsonObject recyclerValues() {
        return recycler;
    }

    private static JsonObject loadSection(String fileName, JsonObject defaults) {
        try {
            Files.createDirectories(CONFIG_DIR);
            Path path = CONFIG_DIR.resolve(fileName);
            if (!Files.exists(path)) {
                write(path, defaults);
                return defaults.deepCopy();
            }
            try (Reader reader = Files.newBufferedReader(path)) {
                JsonElement parsed = JsonParser.parseReader(reader);
                if (!parsed.isJsonObject()) {
                    write(path, defaults);
                    return defaults.deepCopy();
                }
                JsonObject merged = defaults.deepCopy();
                merge(merged, parsed.getAsJsonObject());
                write(path, merged);
                return merged;
            }
        } catch (IOException ignored) {
            return defaults.deepCopy();
        }
    }

    private static void write(Path path, JsonObject object) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(object, writer);
        }
    }

    private static void merge(JsonObject target, JsonObject source) {
        for (String key : source.keySet()) {
            JsonElement sourceValue = source.get(key);
            JsonElement targetValue = target.get(key);
            if (sourceValue.isJsonObject() && targetValue != null && targetValue.isJsonObject()) {
                merge(targetValue.getAsJsonObject(), sourceValue.getAsJsonObject());
            } else {
                target.add(key, sourceValue);
            }
        }
    }

    private static String getString(JsonObject object, String key, String fallback) {
        JsonElement value = object.get(key);
        if (value != null && value.isJsonPrimitive()) {
            return value.getAsString();
        }
        List<String> list = getStringList(object, key);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return fallback;
    }

    private static boolean getBoolean(JsonObject object, String key, boolean fallback) {
        JsonElement value = object.get(key);
        if (value != null && value.isJsonPrimitive()) {
            return value.getAsBoolean();
        }
        return fallback;
    }

    private static int getInt(JsonObject object, String key, int fallback) {
        JsonElement value = object.get(key);
        if (value != null && value.isJsonPrimitive()) {
            return value.getAsInt();
        }
        return fallback;
    }

    private static List<String> getStringList(JsonObject object, String key) {
        List<String> values = new ArrayList<>();
        JsonElement value = object.get(key);
        if (value == null) {
            return values;
        }
        if (value.isJsonArray()) {
            JsonArray array = value.getAsJsonArray();
            for (JsonElement element : array) {
                values.add(element.getAsString());
            }
            return values;
        }
        if (value.isJsonPrimitive()) {
            values.add(value.getAsString());
        }
        return values;
    }

    private static JsonObject defaultLootTables() {
        JsonObject o = new JsonObject();
        o.add("common_lt_name", array("mob_loot_bags:lootbags/common"));
        o.add("uncommon_lt_name", array("mob_loot_bags:un_common"));
        o.add("rare_lt_name", array("mob_loot_bags:lootbags/rare"));
        o.add("epic_lt_name", array("mob_loot_bags:lootbags/epic"));
        o.add("legendary_lt_name", array("mob_loot_bags:legendary_loot_table"));
        o.addProperty("summoning_lt_name", "mob_loot_bags:summoning_loot_table");
        o.addProperty("locked_lt_name", "mob_loot_bags:locked_loot_table");
        o.addProperty("default_lt_name", "minecraft:chests/end_city_treasure");
        return o;
    }

    private static JsonObject defaultDropRates() {
        JsonObject o = new JsonObject();
        o.addProperty("drop_chance_overall", 80);
        o.addProperty("common_drop_rate", 60);
        o.addProperty("uncommon_drop_rate", 25);
        o.addProperty("rare_drop_rate", 10);
        o.addProperty("epic_drop_rate", 4);
        o.addProperty("legendary_drop_rate", 1);
        o.addProperty("cursed_drop_rate", 1);
        o.addProperty("timed_drop_rate", 20);
        return o;
    }

    private static JsonObject defaultSpecialBags() {
        JsonObject o = new JsonObject();
        o.addProperty("enable_cursed_bag", true);
        o.addProperty("enable_sound", true);
        o.add("events", array(
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
                "place structure minecraft:jungle_temple"
        ));
        return o;
    }

    private static JsonObject defaultMobList() {
        JsonObject o = new JsonObject();
        o.addProperty("enable", false);
        o.add("mobs", array("minecraft:cow"));
        return o;
    }

    private static JsonObject defaultRecycler() {
        JsonObject o = new JsonObject();
        o.addProperty("common_lb", 10);
        o.addProperty("uncommon_lb", 20);
        o.addProperty("rare_lb", 40);
        o.addProperty("epic_lb", 80);
        o.addProperty("legendary_lb", 200);
        o.addProperty("cursed_lb", 60);
        o.addProperty("timed_lb", 40);
        o.addProperty("enchanted_timed_lb", 200);
        o.addProperty("locked_lb", 40);
        o.addProperty("summoning_lb", 40);
        return o;
    }

    private static JsonArray array(String... values) {
        JsonArray array = new JsonArray();
        for (String value : values) {
            array.add(value);
        }
        return array;
    }
}
