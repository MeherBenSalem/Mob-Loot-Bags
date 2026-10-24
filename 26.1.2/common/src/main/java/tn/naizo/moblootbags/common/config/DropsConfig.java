package tn.naizo.moblootbags.common.config;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import tn.naizo.moblootbags.common.content.BagType;

import static tn.naizo.moblootbags.common.config.Json.arr;
import static tn.naizo.moblootbags.common.config.Json.obj;

/** {@code config/mob_loot_bags/drops.json}: who drops bags, how often and how good. */
public final class DropsConfig {
    public final boolean enabled;
    public final double hostileChance;
    public final double passiveChance;
    public final double globalMultiplier;
    public final boolean requirePlayer;
    public final boolean creditPetKills;
    public final boolean allowFakePlayers;
    public final boolean healthScaling;
    public final double referenceHealth;
    public final double minHealthMultiplier;
    public final double maxHealthMultiplier;
    public final double spawnerMultiplier;
    public final double trialSpawnerMultiplier;
    public final double[] tierWeights = new double[BagType.TIERS.length];
    public final double luckPerPoint;
    public final double luckPerLooting;
    public final double luckAttribute;
    public final double maxHealthLuck;
    public final double luckNether;
    public final double luckEnd;
    public final double luckHard;
    public final double luckTalisman;
    public final double talismanChanceMultiplier;
    public final double cursedChance;
    public final double hourglassChance;
    public final double lockedChance;
    public final double summoningChance;
    public final boolean themes;
    public final boolean pityEnabled;
    public final int pityKills;
    public final int pityRare;
    public final int pityLegendary;
    public final int bossBags;
    public final int bossMinRank;
    public final double bossMythicChance;
    public final double carrierChance;
    public final double carrierLuck;
    public final int announceMinRank;
    public final int dropSoundMinRank;
    public final Map<String, Double> mobMultipliers;
    public final String filterMode;
    public final List<String> filterMobs;

    private DropsConfig(JsonObject o) {
        enabled = Json.bool(o, "enabled", true);
        hostileChance = clamp01(Json.num(o, "hostile_chance", 0.18));
        passiveChance = clamp01(Json.num(o, "passive_chance", 0.0));
        globalMultiplier = Math.max(0.0, Json.num(o, "global_multiplier", 1.0));
        requirePlayer = Json.bool(o, "kills.require_player", true);
        creditPetKills = Json.bool(o, "kills.credit_pet_kills", true);
        allowFakePlayers = Json.bool(o, "kills.allow_fake_players", false);
        healthScaling = Json.bool(o, "health_scaling.enabled", true);
        referenceHealth = Math.max(1.0, Json.num(o, "health_scaling.reference_health", 20.0));
        minHealthMultiplier = Math.max(0.0, Json.num(o, "health_scaling.min_multiplier", 0.5));
        maxHealthMultiplier = Math.max(minHealthMultiplier, Json.num(o, "health_scaling.max_multiplier", 2.5));
        spawnerMultiplier = Math.max(0.0, Json.num(o, "spawner_multiplier", 0.25));
        trialSpawnerMultiplier = Math.max(0.0, Json.num(o, "trial_spawner_multiplier", 1.0));
        double[] defaults = {600, 260, 95, 35, 9, 1};
        for (BagType tier : BagType.TIERS) {
            tierWeights[tier.rank()] = Math.max(0.0, Json.num(o, "tier_weights." + tier.key(), defaults[tier.rank()]));
        }
        luckPerPoint = Math.max(0.0, Json.num(o, "luck.per_point", 0.35));
        luckPerLooting = Json.num(o, "luck.per_looting_level", 0.5);
        luckAttribute = Json.num(o, "luck.luck_attribute", 1.0);
        maxHealthLuck = Math.max(0.0, Json.num(o, "luck.max_health_luck", 2.0));
        luckNether = Json.num(o, "luck.nether", 0.5);
        luckEnd = Json.num(o, "luck.end", 1.0);
        luckHard = Json.num(o, "luck.hard_difficulty", 0.25);
        luckTalisman = Json.num(o, "luck.fortune_talisman", 1.0);
        talismanChanceMultiplier = Math.max(0.0, Json.num(o, "talisman_chance_multiplier", 1.25));
        cursedChance = clamp01(Json.num(o, "specials.cursed", 0.02));
        hourglassChance = clamp01(Json.num(o, "specials.hourglass", 0.03));
        lockedChance = clamp01(Json.num(o, "specials.locked", 0.01));
        summoningChance = clamp01(Json.num(o, "specials.summoning", 0.015));
        themes = Json.bool(o, "themes", true);
        pityEnabled = Json.bool(o, "pity.enabled", true);
        pityKills = Math.max(0, Json.integer(o, "pity.kills_without_bag", 12));
        pityRare = Math.max(0, Json.integer(o, "pity.bags_until_rare", 20));
        pityLegendary = Math.max(0, Json.integer(o, "pity.bags_until_legendary", 200));
        bossBags = Math.max(0, Json.integer(o, "bosses.bags", 3));
        bossMinRank = rank(Json.str(o, "bosses.min_tier", "epic"), BagType.EPIC.rank());
        bossMythicChance = clamp01(Json.num(o, "bosses.mythic_chance", 0.15));
        carrierChance = clamp01(Json.num(o, "carriers.chance", 0.02));
        carrierLuck = Json.num(o, "carriers.luck", 1.0);
        announceMinRank = rankOrNone(Json.str(o, "announce_min_tier", "legendary"));
        dropSoundMinRank = rankOrNone(Json.str(o, "drop_sound_min_tier", "rare"));
        mobMultipliers = Json.numbers(o, "mob_multipliers");
        filterMode = Json.str(o, "filter.mode", "off").toLowerCase(java.util.Locale.ROOT);
        filterMobs = Json.strings(o, "filter.mobs");
    }

    public static DropsConfig from(JsonObject json) {
        return new DropsConfig(json);
    }

    static int rank(String key, int fallback) {
        BagType type = BagType.byKey(key);
        return type != null && type.isTier() ? type.rank() : fallback;
    }

    /** A tier rank, or {@link Integer#MAX_VALUE} for {@code none}. */
    static int rankOrNone(String key) {
        BagType type = BagType.byKey(key);
        return type != null && type.isTier() ? type.rank() : Integer.MAX_VALUE;
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    public static CommentedJson spec() {
        JsonObject d = obj(
                "enabled", true,
                "hostile_chance", 0.18,
                "passive_chance", 0.0,
                "global_multiplier", 1.0,
                "kills", obj("require_player", true, "credit_pet_kills", true, "allow_fake_players", false),
                "health_scaling", obj("enabled", true, "reference_health", 20.0, "min_multiplier", 0.5, "max_multiplier", 2.5),
                "spawner_multiplier", 0.25,
                "trial_spawner_multiplier", 1.0,
                "tier_weights", obj("common", 600, "uncommon", 260, "rare", 95, "epic", 35, "legendary", 9, "mythic", 1),
                "luck", obj("per_point", 0.35, "per_looting_level", 0.5, "luck_attribute", 1.0, "max_health_luck", 2.0,
                        "nether", 0.5, "end", 1.0, "hard_difficulty", 0.25, "fortune_talisman", 1.0),
                "talisman_chance_multiplier", 1.25,
                "specials", obj("cursed", 0.02, "hourglass", 0.03, "locked", 0.01, "summoning", 0.015),
                "themes", true,
                "pity", obj("enabled", true, "kills_without_bag", 12, "bags_until_rare", 20, "bags_until_legendary", 200),
                "bosses", obj("bags", 3, "min_tier", "epic", "mythic_chance", 0.15),
                "carriers", obj("chance", 0.02, "luck", 1.0),
                "announce_min_tier", "legendary",
                "drop_sound_min_tier", "rare",
                "mob_multipliers", obj("minecraft:slime", 0.25, "minecraft:magma_cube", 0.25,
                        "minecraft:silverfish", 0.25, "minecraft:endermite", 0.25),
                "filter", obj("mode", "off", "mobs", arr())
        );
        return new CommentedJson(d)
                .header("Mob Loot Bags 2.0: who drops loot bags, how often and how good.\n"
                        + "Edit and run /lootbags reload, or restart. Missing keys are restored with their defaults.")
                .comment("enabled", "Master switch for loot bag drops from mob kills.")
                .comment("hostile_chance", "Chance (0-1) that a hostile mob drops a bag, before the multipliers below.")
                .comment("passive_chance", "Chance (0-1) for passive and neutral creatures (cows, villagers, golems...). 0 = never.")
                .comment("global_multiplier", "Multiplies every drop chance. 0.5 halves drops, 2.0 doubles them.")
                .comment("kills", "Who gets credit. Arrows and tridents count for their shooter.")
                .comment("health_scaling", "Tougher mobs drop bags more often: chance x (max health / reference_health), clamped.")
                .comment("spawner_multiplier", "Chance multiplier for mobs from monster spawners (keeps mob farms in check).")
                .comment("trial_spawner_multiplier", "Chance multiplier for mobs from trial spawners.")
                .comment("tier_weights", "Relative odds of each tier before Bag Luck. Defaults: about 60% / 26% / 9.5% / 3.5% / 0.9% / 0.1%.")
                .comment("luck", "Bag Luck shifts the tier odds upward. Each point makes every tier about 42% likelier\n"
                        + "relative to the tier below it (per_point 0.35). Sources add up:")
                .comment("luck.per_looting_level", "Per level of Looting on the killing weapon.")
                .comment("luck.luck_attribute", "Multiplier for the player's Luck attribute (Potion of Luck, modded gear).")
                .comment("luck.max_health_luck", "Mobs earn one point per doubling of max health over reference_health, up to this.")
                .comment("luck.nether", "Bonus for kills in the Nether, the End and on Hard difficulty.")
                .comment("luck.fortune_talisman", "Bonus while the killer carries a Fortune Talisman.")
                .comment("talisman_chance_multiplier", "A Fortune Talisman also multiplies the drop chance.")
                .comment("specials", "Chance that a dropped bag becomes a special bag instead (checked in this order).")
                .comment("themes", "Bags dropped in the Nether, the End or a modded dimension use themed loot when a table exists:\n"
                        + "mob_loot_bags:bags/<tier>/<dimension path>, e.g. bags/rare/the_nether.")
                .comment("pity", "Bad-luck protection, tracked per player.\n"
                        + "kills_without_bag: after this many eligible kills without a bag, the next one drops a bag.\n"
                        + "bags_until_rare / bags_until_legendary: after this many bags below that tier, the next is at least that tier.\n"
                        + "Set any value to 0 to turn that rule off.")
                .comment("bosses", "Mobs in #mob_loot_bags:bosses (Wither, Ender Dragon, Warden, Elder Guardian) always drop this many\n"
                        + "bags of at least min_tier, each with mythic_chance to be Mythic.")
                .comment("carriers", "Chance that a naturally spawned mob in #mob_loot_bags:bag_carriers spawns holding a loot bag\n"
                        + "in its off hand. It always drops it. luck is the Bag Luck used to roll the carried bag.")
                .comment("announce_min_tier", "Bags of this tier or better are announced in chat to everyone. \"none\" to disable.")
                .comment("drop_sound_min_tier", "Bags of this tier or better chime when they drop. \"none\" to disable.")
                .comment("mob_multipliers", "Per-mob chance multipliers. Keys are entity ids or entity tags (#namespace:path). 0 = never drops.")
                .comment("filter", "mode \"whitelist\": only the listed mobs drop bags. mode \"blacklist\": the listed mobs never do.\n"
                        + "mode \"off\": no filter. Entries are entity ids or #tags.");
    }
}
