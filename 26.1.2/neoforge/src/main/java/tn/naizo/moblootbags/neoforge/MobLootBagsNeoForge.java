package tn.naizo.moblootbags.neoforge;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.MobLootBagsEvents;
import tn.naizo.moblootbags.common.content.ModContent;
import tn.naizo.moblootbags.common.network.RevealPayload;
import tn.naizo.moblootbags.common.network.SyncPayload;
import tn.naizo.moblootbags.common.platform.Platform;
import tn.naizo.moblootbags.common.platform.Registrar;

@Mod(MobLootBagsCommon.MOD_ID)
public final class MobLootBagsNeoForge {
    private final Map<ResourceKey<?>, DeferredRegister<?>> registers = new HashMap<>();

    public MobLootBagsNeoForge(IEventBus modBus) {
        MobLootBagsCommon.init(new NeoForgePlatform(), new NeoForgeRegistrar(), new ModContent.MenuTypeFactory() {
            @Override
            public <T extends AbstractContainerMenu> MenuType<T> create(ModContent.MenuConstructor<T> constructor) {
                return new MenuType<>(constructor::create, FeatureFlags.VANILLA_SET);
            }
        });
        DeferredRegister<CreativeModeTab> tabs = register(Registries.CREATIVE_MODE_TAB);
        tabs.register("loot_bags_creative_tab", () -> CreativeModeTab.builder()
                .title(ModContent.tabTitle())
                .icon(ModContent::tabIcon)
                .displayItems((parameters, output) -> ModContent.forEachTabItem(output::accept))
                .build());
        registers.values().forEach(deferred -> deferred.register(modBus));

        modBus.addListener(this::registerPayloads);
        modBus.addListener(this::registerCapabilities);
        NeoForge.EVENT_BUS.addListener((LivingDeathEvent event) -> MobLootBagsEvents.onLivingDeath(event.getEntity(), event.getSource()));
        NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) -> MobLootBagsEvents.onServerStarted(event.getServer()));
        NeoForge.EVENT_BUS.addListener((ServerStoppingEvent event) -> MobLootBagsEvents.onServerStopping(event.getServer()));
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> MobLootBagsEvents.onServerTick(event.getServer()));
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
                MobLootBagsEvents.registerCommands(event.getDispatcher(), event.getBuildContext()));
        NeoForge.EVENT_BUS.addListener((OnDatapackSyncEvent event) -> {
            if (event.getPlayer() == null) {
                MobLootBagsEvents.onDataReloaded(event.getPlayerList().getServer());
            } else {
                MobLootBagsEvents.onPlayerSync(event.getPlayer());
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <R> DeferredRegister<R> register(ResourceKey<? extends Registry<R>> registry) {
        return (DeferredRegister<R>) registers.computeIfAbsent(registry, key -> DeferredRegister.create(registry, MobLootBagsCommon.MOD_ID));
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("2")
                .playToClient(SyncPayload.TYPE, SyncPayload.CODEC)
                .playToClient(RevealPayload.TYPE, RevealPayload.CODEC);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModContent.BAG_OPENER_ENTITY.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModContent.LOOT_RECYCLER_ENTITY.get(), WorldlyContainerWrapper::new);
    }

    private final class NeoForgeRegistrar implements Registrar {
        @Override
        public <R, T extends R> Supplier<T> register(ResourceKey<? extends Registry<R>> registry, String path, Function<Identifier, T> factory) {
            return MobLootBagsNeoForge.this.register(registry).register(path, factory);
        }
    }

    private static final class NeoForgePlatform implements Platform {
        @Override
        public Path configDir() {
            return FMLPaths.CONFIGDIR.get();
        }

        @Override
        public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
            PacketDistributor.sendToPlayer(player, payload);
        }

        @Override
        public boolean isFakePlayer(ServerPlayer player) {
            return player instanceof FakePlayer;
        }

        @Override
        public boolean isClient() {
            return FMLEnvironment.getDist() == Dist.CLIENT;
        }
    }
}
