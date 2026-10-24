package tn.naizo.moblootbags.common.drop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.config.CommentedJson;
import tn.naizo.moblootbags.common.content.BagType;

/**
 * Per-player bag statistics and pity counters, saved to {@code <world>/data/mob_loot_bags/players.json}.
 */
public final class PlayerLootData {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<UUID, Entry> ENTRIES = new HashMap<>();
    private static Path file;
    private static boolean dirty;

    private PlayerLootData() {
    }

    public static final class Entry {
        public int killsSinceBag;
        public int bagsSinceRare;
        public int bagsSinceLegendary;
        public final int[] found = new int[BagType.values().length];
        public final int[] opened = new int[BagType.values().length];

        public int totalFound() {
            int total = 0;
            for (int value : found) {
                total += value;
            }
            return total;
        }

        public int totalOpened() {
            int total = 0;
            for (int value : opened) {
                total += value;
            }
            return total;
        }
    }

    public static Entry get(UUID player) {
        return ENTRIES.computeIfAbsent(player, id -> new Entry());
    }

    public static void markDirty() {
        dirty = true;
    }

    public static void load(MinecraftServer server) {
        ENTRIES.clear();
        dirty = false;
        file = server.getWorldPath(LevelResource.ROOT).resolve("data").resolve(MobLootBagsCommon.MOD_ID).resolve("players.json");
        if (!Files.exists(file)) {
            return;
        }
        try {
            JsonElement parsed = CommentedJson.parse(Files.readString(file, StandardCharsets.UTF_8));
            JsonObject root = parsed != null && parsed.isJsonObject() ? parsed.getAsJsonObject() : new JsonObject();
            for (Map.Entry<String, JsonElement> e : root.entrySet()) {
                if (!e.getValue().isJsonObject()) {
                    continue;
                }
                JsonObject o = e.getValue().getAsJsonObject();
                Entry entry = new Entry();
                entry.killsSinceBag = intOr(o, "kills_since_bag");
                entry.bagsSinceRare = intOr(o, "bags_since_rare");
                entry.bagsSinceLegendary = intOr(o, "bags_since_legendary");
                JsonObject found = o.has("found") ? o.getAsJsonObject("found") : new JsonObject();
                JsonObject opened = o.has("opened") ? o.getAsJsonObject("opened") : new JsonObject();
                for (BagType type : BagType.values()) {
                    entry.found[type.ordinal()] = intOr(found, type.key());
                    entry.opened[type.ordinal()] = intOr(opened, type.key());
                }
                ENTRIES.put(UUID.fromString(e.getKey()), entry);
            }
        } catch (IOException | RuntimeException ex) {
            MobLootBagsCommon.LOG.error("Could not read {}: {}", file, ex.toString());
        }
    }

    public static void save(boolean force) {
        if (file == null || (!dirty && !force)) {
            return;
        }
        JsonObject root = new JsonObject();
        ENTRIES.forEach((uuid, entry) -> {
            JsonObject o = new JsonObject();
            o.addProperty("kills_since_bag", entry.killsSinceBag);
            o.addProperty("bags_since_rare", entry.bagsSinceRare);
            o.addProperty("bags_since_legendary", entry.bagsSinceLegendary);
            JsonObject found = new JsonObject();
            JsonObject opened = new JsonObject();
            for (BagType type : BagType.values()) {
                found.addProperty(type.key(), entry.found[type.ordinal()]);
                opened.addProperty(type.key(), entry.opened[type.ordinal()]);
            }
            o.add("found", found);
            o.add("opened", opened);
            root.add(uuid.toString(), o);
        });
        try {
            Files.createDirectories(file.getParent());
            Path tmp = file.resolveSibling("players.json.tmp");
            Files.writeString(tmp, GSON.toJson(root), StandardCharsets.UTF_8);
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            dirty = false;
        } catch (IOException e) {
            MobLootBagsCommon.LOG.error("Could not save {}: {}", file, e.toString());
        }
    }

    public static void unload() {
        save(false);
        ENTRIES.clear();
        file = null;
    }

    private static int intOr(JsonObject o, String key) {
        try {
            return o.has(key) ? o.get(key).getAsInt() : 0;
        } catch (RuntimeException e) {
            return 0;
        }
    }
}
