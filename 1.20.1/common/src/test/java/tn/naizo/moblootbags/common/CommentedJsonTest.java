package tn.naizo.moblootbags.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tn.naizo.moblootbags.common.config.BagsConfig;
import tn.naizo.moblootbags.common.config.CommentedJson;
import tn.naizo.moblootbags.common.config.DropsConfig;
import tn.naizo.moblootbags.common.config.Json;
import tn.naizo.moblootbags.common.config.MachinesConfig;

class CommentedJsonTest {
    @TempDir
    Path dir;

    @Test
    void writesDefaultsWithComments() throws Exception {
        Path file = dir.resolve("drops.json");
        JsonObject loaded = DropsConfig.spec().load(file);
        String text = Files.readString(file);
        assertTrue(text.startsWith("// Mob Loot Bags 2.0"));
        assertTrue(text.contains("// Master switch"));
        assertEquals(0.18, Json.num(loaded, "hostile_chance", -1), 1e-9);
        // The written file reads back to the same values.
        assertEquals(loaded, CommentedJson.parse(text).getAsJsonObject());
    }

    @Test
    void keepsUserValuesAndUnknownKeysAndRestoresMissingOnes() throws Exception {
        Path file = dir.resolve("drops.json");
        Files.writeString(file, """
                // my notes
                {
                  "hostile_chance": 0.5, // tweaked
                  "pity": { "kills_without_bag": 3 },
                  "my_extra_key": "kept"
                }
                """);
        JsonObject loaded = DropsConfig.spec().load(file);
        DropsConfig config = DropsConfig.from(loaded);
        assertEquals(0.5, config.hostileChance, 1e-9);
        assertEquals(3, config.pityKills);
        assertEquals(20, config.pityRare, "missing nested key restored");
        assertEquals("kept", Json.str(loaded, "my_extra_key", ""));
        assertTrue(Files.readString(file).contains("\"bags_until_legendary\": 200"));
    }

    @Test
    void badValuesFallBackToDefaults() {
        JsonObject json = Json.obj("hostile_chance", "lots", "tier_weights", Json.obj("rare", -5));
        DropsConfig config = DropsConfig.from(json);
        assertEquals(0.18, config.hostileChance, 1e-9);
        assertEquals(0.0, config.tierWeights[2], 1e-9);
    }

    @Test
    void brokenFileIsLeftUntouched() throws Exception {
        Path file = dir.resolve("bags.json");
        Files.writeString(file, "[1, 2, 3]");
        JsonObject loaded = BagsConfig.spec().load(file);
        assertEquals("[1, 2, 3]", Files.readString(file));
        assertEquals(16, BagsConfig.from(loaded).holdTicks);
    }

    @Test
    void unchangedFilesAreNotRewritten() throws Exception {
        Path file = dir.resolve("machines.json");
        MachinesConfig.spec().load(file);
        long modified = Files.getLastModifiedTime(file).toMillis();
        Thread.sleep(20);
        MachinesConfig.spec().load(file);
        assertEquals(modified, Files.getLastModifiedTime(file).toMillis());
        assertFalse(MachinesConfig.from(MachinesConfig.spec().load(file)).junk.isEmpty());
    }
}
