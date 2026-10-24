package tn.naizo.moblootbags.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.client.BagOpenerScreen;
import tn.naizo.moblootbags.common.client.LootRecyclerScreen;
import tn.naizo.moblootbags.common.client.MobLootBagsClient;
import tn.naizo.moblootbags.common.content.ModContent;

/** Client setup. The payload handlers are registered with the payloads in {@link MobLootBagsNeoForge} (NeoForge 21.1). */
@Mod(value = MobLootBagsCommon.MOD_ID, dist = Dist.CLIENT)
public final class MobLootBagsNeoForgeClient {
    public MobLootBagsNeoForgeClient(IEventBus modBus) {
        MobLootBagsClient.init();
        modBus.addListener((RegisterMenuScreensEvent event) -> {
            event.register(ModContent.BAG_OPENER_MENU.get(), BagOpenerScreen::new);
            event.register(ModContent.LOOT_RECYCLER_MENU.get(), LootRecyclerScreen::new);
        });
        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> MobLootBagsClient.reset());
    }
}
