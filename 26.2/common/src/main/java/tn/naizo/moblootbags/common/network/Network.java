package tn.naizo.moblootbags.common.network;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.config.BagsConfig;
import tn.naizo.moblootbags.common.config.LootBagsConfig;
import tn.naizo.moblootbags.common.content.BagType;
import tn.naizo.moblootbags.common.loot.LootService;

public final class Network {
    private Network() {
    }

    public static SyncPayload syncPayload() {
        BagsConfig bags = LootBagsConfig.bags();
        return new SyncPayload(bags.holdTicks, bags.sneakOpensStack, bags.previewLines, LootService.previews());
    }

    public static void sync(ServerPlayer player) {
        MobLootBagsCommon.platform().sendToPlayer(player, syncPayload());
    }

    public static void syncAll(MinecraftServer server) {
        SyncPayload payload = syncPayload();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            MobLootBagsCommon.platform().sendToPlayer(player, payload);
        }
    }

    public static void reveal(ServerPlayer player, BagType bag, int opened, List<ItemStack> items) {
        MobLootBagsCommon.platform().sendToPlayer(player, new RevealPayload(bag, opened, items));
    }
}
