package tn.naizo.moblootbags.common;

public final class MobLootBagsCommon {
    private static boolean initialized = false;

    private MobLootBagsCommon() {
    }

    public static void initForFabric() {
        if (initialized) {
            return;
        }
        initialized = true;
        MobLootBagsConfig.init();
        MobLootBagsRegistry.registerAll();
    }

    public static void initForNeoForge() {
        if (initialized) {
            return;
        }
        initialized = true;
        MobLootBagsConfig.init();
    }
}
