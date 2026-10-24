package tn.naizo.moblootbags.common.content;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * Every loot bag in the mod. The six tiers roll from mob kills; the four special bags replace a dropped bag by chance.
 * Registry ids are the 1.x ids so existing worlds keep their bags.
 */
public enum BagType {
    COMMON("commonlootbag", "common", 0, 0xD9D4C7, 10),
    UNCOMMON("uncommonlootbag", "uncommon", 1, 0x5BD45B, 20),
    RARE("rarelootbag", "rare", 2, 0x4B9CFF, 40),
    EPIC("epiclootbag", "epic", 3, 0xB15CFF, 80),
    LEGENDARY("legendarylootbag", "legendary", 4, 0xFFB01F, 200),
    MYTHIC("mythiclootbag", "mythic", 5, 0xFF4F7B, 500),
    CURSED("cursed_lootbag", "cursed", -1, 0x9BE05A, 60),
    HOURGLASS("timed_loot_bag", "hourglass", -1, 0x4FD8C8, 40),
    LOCKED("locked_lootbags", "locked", -1, 0xB8C4D6, 40),
    SUMMONING("summoning_lootbags", "summoning", -1, 0xF0864A, 40);

    public static final BagType[] TIERS = {COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC};
    public static final BagType[] SPECIALS = {CURSED, HOURGLASS, LOCKED, SUMMONING};

    private final String registryId;
    private final String key;
    private final int rank;
    private final int color;
    private final int defaultXp;

    BagType(String registryId, String key, int rank, int color, int defaultXp) {
        this.registryId = registryId;
        this.key = key;
        this.rank = rank;
        this.color = color;
        this.defaultXp = defaultXp;
    }

    /** The item id path, e.g. {@code rarelootbag}. */
    public String registryId() {
        return registryId;
    }

    /** The short name used in configs, commands, loot table paths and translations, e.g. {@code rare}. */
    public String key() {
        return key;
    }

    /** 0 (Common) to 5 (Mythic) for tier bags, -1 for special bags. */
    public int rank() {
        return rank;
    }

    public boolean isTier() {
        return rank >= 0;
    }

    public int color() {
        return color;
    }

    public int defaultXp() {
        return defaultXp;
    }

    /** The default loot table, e.g. {@code mob_loot_bags:bags/rare}. */
    public String defaultTable() {
        return "mob_loot_bags:bags/" + key;
    }

    public String translationKey() {
        return "bag_type.mob_loot_bags." + key;
    }

    public static BagType tier(int rank) {
        return TIERS[Math.max(0, Math.min(TIERS.length - 1, rank))];
    }

    public static @Nullable BagType byKey(String key) {
        String wanted = key.toLowerCase(Locale.ROOT).trim();
        for (BagType type : values()) {
            if (type.key.equals(wanted) || type.registryId.equals(wanted)) {
                return type;
            }
        }
        return null;
    }
}
