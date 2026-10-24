package tn.naizo.moblootbags.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import tn.naizo.moblootbags.common.client.BagOpenerScreen;
import tn.naizo.moblootbags.common.client.LootRecyclerScreen;
import tn.naizo.moblootbags.common.client.MobLootBagsClient;
import tn.naizo.moblootbags.common.content.ModContent;
import tn.naizo.moblootbags.common.network.RevealPayload;
import tn.naizo.moblootbags.common.network.SyncPayload;

public final class MobLootBagsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MobLootBagsClient.init();
        MenuScreens.register(ModContent.BAG_OPENER_MENU.get(), BagOpenerScreen::new);
        MenuScreens.register(ModContent.LOOT_RECYCLER_MENU.get(), LootRecyclerScreen::new);
        ClientPlayNetworking.registerGlobalReceiver(SyncPayload.TYPE, (payload, context) -> MobLootBagsClient.handleSync(payload));
        ClientPlayNetworking.registerGlobalReceiver(RevealPayload.TYPE, (payload, context) -> MobLootBagsClient.handleReveal(payload));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> MobLootBagsClient.reset());
    }
}
