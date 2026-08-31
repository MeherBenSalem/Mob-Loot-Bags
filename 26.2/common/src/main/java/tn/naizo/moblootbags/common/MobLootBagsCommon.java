package tn.naizo.moblootbags.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MobLootBagsCommon {
    public static final String MOD_ID = "mob_loot_bags";
    public static final String MOD_NAME = "Mob Loot Bags";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private static boolean initialized = false;

    private MobLootBagsCommon() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        LOG.info("Initializing Mob Loot Bags");
        MobLootBagsConfig.init();
        MobLootBagsRegistry.registerAll();
    }
}
