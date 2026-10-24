package tn.naizo.moblootbags.common.config;

import com.google.gson.JsonObject;

import static tn.naizo.moblootbags.common.config.Json.obj;

/** {@code config/mob_loot_bags/client.json}: visual options that only affect your own game. */
public final class ClientConfig {
    public final boolean lootBeams;
    public final int beamMinRank;
    public final double beamHeight;
    public final boolean revealToast;
    public final double toastSeconds;
    public final boolean previewTooltips;

    private ClientConfig(JsonObject o) {
        lootBeams = Json.bool(o, "loot_beams", true);
        beamMinRank = DropsConfig.rankOrNone(Json.str(o, "beam_min_tier", "rare"));
        beamHeight = Math.max(0.5, Math.min(8.0, Json.num(o, "beam_height", 2.5)));
        revealToast = Json.bool(o, "reveal_toast", true);
        toastSeconds = Math.max(1.0, Math.min(30.0, Json.num(o, "toast_seconds", 5.0)));
        previewTooltips = Json.bool(o, "preview_tooltips", true);
    }

    public static ClientConfig from(JsonObject json) {
        return new ClientConfig(json);
    }

    public static CommentedJson spec() {
        JsonObject d = obj("loot_beams", true, "beam_min_tier", "rare", "beam_height", 2.5,
                "reveal_toast", true, "toast_seconds", 5.0, "preview_tooltips", true);
        return new CommentedJson(d)
                .header("Mob Loot Bags 2.0: client-side visuals. These only change what you see.")
                .comment("loot_beams", "Dropped bags of beam_min_tier or better send up a beam of their tier's colour, beam_height blocks tall.")
                .comment("reveal_toast", "Show the loot you got from a bag in a toast for toast_seconds.")
                .comment("preview_tooltips", "Hold Shift over a bag to see its possible loot.");
    }
}
