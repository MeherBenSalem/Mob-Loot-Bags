package tn.naizo.moblootbags.common.config;

import com.google.gson.JsonObject;
import java.util.EnumMap;
import java.util.Map;
import tn.naizo.moblootbags.common.content.BagType;

import static tn.naizo.moblootbags.common.config.Json.obj;

/** {@code config/mob_loot_bags/machines.json}: the Bag Opener and the Loot Recycler. */
public final class MachinesConfig {
    public final int openerTicksPerBag;
    public final int recyclerTicksPerItem;
    public final int pointsPerBag;
    public final BagType recyclerOutput;
    public final Map<BagType, Integer> bagXp = new EnumMap<>(BagType.class);
    public final int ripeHourglassXp;
    public final Map<String, Double> junk;

    private MachinesConfig(JsonObject o) {
        openerTicksPerBag = Math.max(1, Json.integer(o, "opener.ticks_per_bag", 20));
        recyclerTicksPerItem = Math.max(1, Json.integer(o, "recycler.ticks_per_item", 5));
        pointsPerBag = Math.max(1, Json.integer(o, "recycler.points_per_bag", 64));
        BagType output = BagType.byKey(Json.str(o, "recycler.output_bag", "common"));
        recyclerOutput = output != null ? output : BagType.COMMON;
        for (BagType type : BagType.values()) {
            bagXp.put(type, Math.max(0, Json.integer(o, "recycler.bag_xp." + type.key(), type.defaultXp())));
        }
        ripeHourglassXp = Math.max(0, Json.integer(o, "recycler.bag_xp.ripe_hourglass", 200));
        junk = Json.numbers(o, "recycler.junk");
    }

    public static MachinesConfig from(JsonObject json) {
        return new MachinesConfig(json);
    }

    public static CommentedJson spec() {
        JsonObject xp = new JsonObject();
        for (BagType type : BagType.values()) {
            xp.addProperty(type.key(), type.defaultXp());
        }
        xp.addProperty("ripe_hourglass", 200);
        JsonObject d = obj(
                "opener", obj("ticks_per_bag", 20),
                "recycler", obj(
                        "ticks_per_item", 5,
                        "points_per_bag", 64,
                        "output_bag", "common",
                        "bag_xp", xp,
                        "junk", obj(
                                "#mob_loot_bags:recycler_junk", 1,
                                "minecraft:spider_eye", 2,
                                "minecraft:gunpowder", 2,
                                "minecraft:slime_ball", 2,
                                "minecraft:phantom_membrane", 3,
                                "minecraft:ender_pearl", 4,
                                "minecraft:blaze_rod", 4,
                                "minecraft:ghast_tear", 6,
                                "minecraft:bow", 3,
                                "minecraft:crossbow", 3,
                                "minecraft:golden_sword", 3,
                                "minecraft:iron_sword", 4,
                                "minecraft:chainmail_helmet", 4,
                                "minecraft:chainmail_chestplate", 6,
                                "minecraft:chainmail_leggings", 5,
                                "minecraft:chainmail_boots", 3,
                                "minecraft:golden_helmet", 4,
                                "minecraft:golden_chestplate", 6,
                                "minecraft:golden_leggings", 5,
                                "minecraft:golden_boots", 3))
        );
        return new CommentedJson(d)
                .header("Mob Loot Bags 2.0: the Bag Opener and the Loot Recycler.\n"
                        + "Edit and run /lootbags reload, or restart. Missing keys are restored with their defaults.")
                .comment("opener", "ticks_per_bag: how long the Bag Opener takes per bag (20 ticks = 1 second).")
                .comment("recycler", "The Loot Recycler grinds mob-farm junk into salvage points and turns points_per_bag points into\n"
                        + "one output_bag. Loot bags fed to it become stored experience instead (bag_xp).\n"
                        + "junk: salvage points per item. Keys are item ids or #item tags. Anything not listed is refused.");
    }
}
