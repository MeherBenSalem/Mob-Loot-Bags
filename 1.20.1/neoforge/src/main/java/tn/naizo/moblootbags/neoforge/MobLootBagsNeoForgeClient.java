package tn.naizo.moblootbags.neoforge;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tn.naizo.moblootbags.common.client.BagOpenerScreen;
import tn.naizo.moblootbags.common.client.LootRecyclerScreen;
import tn.naizo.moblootbags.common.client.MobLootBagsClient;
import tn.naizo.moblootbags.common.content.ModContent;

/**
 * Client setup. Forge 47 has no client-only {@code @Mod} entry point, so {@link MobLootBagsNeoForge} calls {@link #init}
 * on the physical client; the payload handlers live with the channels there.
 */
public final class MobLootBagsNeoForgeClient {
    private MobLootBagsNeoForgeClient() {
    }

    public static void init(IEventBus modBus) {
        MobLootBagsClient.init();
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            MenuScreens.register(ModContent.BAG_OPENER_MENU.get(), BagOpenerScreen::new);
            MenuScreens.register(ModContent.LOOT_RECYCLER_MENU.get(), LootRecyclerScreen::new);
        }));
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> MobLootBagsClient.reset());
    }
}
