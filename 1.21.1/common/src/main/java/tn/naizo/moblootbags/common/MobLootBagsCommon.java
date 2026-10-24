package tn.naizo.moblootbags.common;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tn.naizo.moblootbags.common.config.LootBagsConfig;
import tn.naizo.moblootbags.common.content.ModContent;
import tn.naizo.moblootbags.common.platform.Platform;
import tn.naizo.moblootbags.common.platform.Registrar;

public final class MobLootBagsCommon {
    public static final String MOD_ID = "mob_loot_bags";
    public static final String MOD_NAME = "Mob Loot Bags";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private static Platform platform;
    private static boolean initialized = false;

    private MobLootBagsCommon() {
    }

    public static void init(Platform loaderPlatform, Registrar registrar, ModContent.MenuTypeFactory menus) {
        if (initialized) {
            return;
        }
        initialized = true;
        platform = loaderPlatform;
        LOG.info("Initializing Mob Loot Bags");
        LootBagsConfig.load(platform.configDir());
        ModContent.register(registrar, menus);
    }

    public static Platform platform() {
        return platform;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
