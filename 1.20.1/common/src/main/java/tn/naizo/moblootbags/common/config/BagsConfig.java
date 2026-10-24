package tn.naizo.moblootbags.common.config;

import com.google.gson.JsonObject;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import tn.naizo.moblootbags.common.content.BagType;

import static tn.naizo.moblootbags.common.config.Json.arr;
import static tn.naizo.moblootbags.common.config.Json.obj;

/** {@code config/mob_loot_bags/bags.json}: opening bags, their loot tables and the special bags. */
public final class BagsConfig {
    public final int holdTicks;
    public final boolean sneakOpensStack;
    public final int maxStackOpen;
    public final boolean lootToInventory;
    public final boolean revealToast;
    public final boolean chatSummary;
    public final int titleMinRank;
    public final Map<BagType, List<String>> tableOverrides = new EnumMap<>(BagType.class);
    public final int cursedAmbush;
    public final int cursedJackpot;
    public final int cursedHex;
    public final int cursedChaos;
    public final List<String> ambushMobs;
    public final int ambushMin;
    public final int ambushMax;
    public final int ambushRewardMinRank;
    public final int jackpotRank;
    public final List<String> hexEffects;
    public final int hexSeconds;
    public final List<String> chaosCommands;
    public final boolean cursedSound;
    public final double hourglassMinMinutes;
    public final double hourglassMaxMinutes;
    public final String lockedKey;
    public final boolean keyAnywhere;
    public final boolean previewEnabled;
    public final int previewSamples;
    public final int previewLines;

    private BagsConfig(JsonObject o) {
        holdTicks = Math.max(0, Math.min(200, Json.integer(o, "opening.hold_ticks", 16)));
        sneakOpensStack = Json.bool(o, "opening.sneak_opens_stack", true);
        maxStackOpen = Math.max(1, Json.integer(o, "opening.max_stack_open", 64));
        lootToInventory = Json.bool(o, "opening.loot_to_inventory", true);
        revealToast = Json.bool(o, "opening.reveal_toast", true);
        chatSummary = Json.bool(o, "opening.chat_summary", false);
        titleMinRank = DropsConfig.rankOrNone(Json.str(o, "opening.title_min_tier", "legendary"));
        for (BagType type : BagType.values()) {
            tableOverrides.put(type, Json.strings(o, "loot_tables." + type.key()));
        }
        cursedAmbush = Math.max(0, Json.integer(o, "cursed.outcomes.ambush", 50));
        cursedJackpot = Math.max(0, Json.integer(o, "cursed.outcomes.jackpot", 15));
        cursedHex = Math.max(0, Json.integer(o, "cursed.outcomes.hex", 25));
        cursedChaos = Math.max(0, Json.integer(o, "cursed.outcomes.chaos", 10));
        ambushMobs = Json.strings(o, "cursed.ambush_mobs");
        ambushMin = Math.max(1, Json.integer(o, "cursed.ambush_min", 3));
        ambushMax = Math.max(ambushMin, Json.integer(o, "cursed.ambush_max", 5));
        ambushRewardMinRank = DropsConfig.rank(Json.str(o, "cursed.ambush_reward_min_tier", "uncommon"), 1);
        jackpotRank = DropsConfig.rank(Json.str(o, "cursed.jackpot_tier", "legendary"), BagType.LEGENDARY.rank());
        hexEffects = Json.strings(o, "cursed.hex_effects");
        hexSeconds = Math.max(1, Json.integer(o, "cursed.hex_seconds", 12));
        chaosCommands = Json.strings(o, "cursed.chaos_commands");
        cursedSound = Json.bool(o, "cursed.sound", true);
        hourglassMinMinutes = Math.max(0.0, Json.num(o, "hourglass.min_minutes", 5.0));
        hourglassMaxMinutes = Math.max(hourglassMinMinutes, Json.num(o, "hourglass.max_minutes", 10.0));
        lockedKey = Json.str(o, "locked.key", "mob_loot_bags:diamond_key");
        keyAnywhere = Json.bool(o, "locked.key_anywhere", true);
        previewEnabled = Json.bool(o, "preview.enabled", true);
        previewSamples = Math.max(20, Math.min(5000, Json.integer(o, "preview.samples", 400)));
        previewLines = Math.max(1, Math.min(20, Json.integer(o, "preview.lines", 8)));
    }

    public static BagsConfig from(JsonObject json) {
        return new BagsConfig(json);
    }

    public static CommentedJson spec() {
        JsonObject tables = new JsonObject();
        for (BagType type : BagType.values()) {
            tables.add(type.key(), arr());
        }
        JsonObject d = obj(
                "opening", obj("hold_ticks", 16, "sneak_opens_stack", true, "max_stack_open", 64, "loot_to_inventory", true,
                        "reveal_toast", true, "chat_summary", false, "title_min_tier", "legendary"),
                "loot_tables", tables,
                "cursed", obj(
                        "outcomes", obj("ambush", 50, "jackpot", 15, "hex", 25, "chaos", 10),
                        "ambush_mobs", arr("minecraft:zombie", "minecraft:skeleton", "minecraft:spider", "minecraft:husk",
                                "minecraft:stray", "minecraft:witch"),
                        "ambush_min", 3,
                        "ambush_max", 5,
                        "ambush_reward_min_tier", "uncommon",
                        "jackpot_tier", "legendary",
                        "hex_effects", arr("minecraft:darkness", "minecraft:slowness", "minecraft:weakness", "minecraft:hunger",
                                "minecraft:nausea", "minecraft:mining_fatigue", "minecraft:levitation"),
                        "hex_seconds", 12,
                        // Command syntax is the running version's: 1.20.1 TNT reads "Fuse" (26.x reads "fuse").
                        "chaos_commands", arr(
                                "summon minecraft:tnt ~ ~1 ~ {Fuse:60}",
                                "summon minecraft:lightning_bolt ~2 ~ ~2",
                                "weather thunder 600",
                                "effect give @s minecraft:levitation 3 1",
                                "summon minecraft:zombie ~ ~ ~ {IsBaby:1b}",
                                "summon minecraft:vex ~ ~1 ~",
                                "xp add @s 100",
                                "give @s minecraft:poisonous_potato 16",
                                "summon minecraft:firework_rocket ~ ~1 ~ {LifeTime:30}"),
                        "sound", true),
                "hourglass", obj("min_minutes", 5.0, "max_minutes", 10.0),
                "locked", obj("key", "mob_loot_bags:diamond_key", "key_anywhere", true),
                "preview", obj("enabled", true, "samples", 400, "lines", 8)
        );
        return new CommentedJson(d)
                .header("Mob Loot Bags 2.0: opening bags, their loot tables and the special bags.\n"
                        + "Edit and run /lootbags reload, or restart. Missing keys are restored with their defaults.")
                .comment("opening", "hold_ticks: how long you hold Use to open a bag (20 ticks = 1 second, 0 = instant).\n"
                        + "sneak_opens_stack: sneak while opening to open the whole stack (up to max_stack_open) at once.\n"
                        + "loot_to_inventory: loot goes straight into your inventory; what does not fit drops at your feet.\n"
                        + "reveal_toast: show the loot you got in a toast. chat_summary: also list it in chat.\n"
                        + "title_min_tier: bags of this tier or better flash a title on screen. \"none\" to disable.")
                .comment("loot_tables", "Optional loot table overrides per bag. Empty = the mod's table (mob_loot_bags:bags/<bag>) with\n"
                        + "dimension themes. List several tables to pick one at random each time. Datapacks can also\n"
                        + "override mob_loot_bags:bags/<bag> directly, or add items to #mob_loot_bags:bonus/<tier>.")
                .comment("cursed", "Opening a Cursed Loot Bag rolls one outcome by weight:\n"
                        + "ambush: cursed mobs surround you; each one you kill drops a bag of at least ambush_reward_min_tier.\n"
                        + "jackpot: the bag opens as a jackpot_tier bag. hex: random harmful effects for hex_seconds.\n"
                        + "chaos: runs one random chaos_command as the player (leave the list empty or weight 0 to disable).")
                .comment("hourglass", "An Hourglass Loot Bag ripens after a random time in this range, then opens into better bags.")
                .comment("locked", "The item that unlocks a Locked Loot Bag (used up). key_anywhere: the key can be anywhere in your\n"
                        + "inventory, otherwise it must be in your off hand.")
                .comment("preview", "Hold Shift over a bag to see its possible loot. The server samples each loot table this many times\n"
                        + "and sends the results to players. lines: how many items the tooltip lists.");
    }
}
