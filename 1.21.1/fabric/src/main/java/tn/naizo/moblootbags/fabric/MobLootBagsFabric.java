package tn.naizo.moblootbags.fabric;

import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.MobLootBagsEvents;
import tn.naizo.moblootbags.common.content.ModContent;
import tn.naizo.moblootbags.common.network.RevealPayload;
import tn.naizo.moblootbags.common.network.SyncPayload;
import tn.naizo.moblootbags.common.platform.Platform;
import tn.naizo.moblootbags.common.platform.Registrar;

public final class MobLootBagsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MobLootBagsCommon.init(new FabricPlatform(), new FabricRegistrar(), new ModContent.MenuTypeFactory() {
            @Override
            public <T extends AbstractContainerMenu> MenuType<T> create(ModContent.MenuConstructor<T> constructor) {
                return new MenuType<>(constructor::create, FeatureFlags.VANILLA_SET);
            }
        });
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MobLootBagsCommon.id("loot_bags_creative_tab"),
                FabricItemGroup.builder()
                        .title(ModContent.tabTitle())
                        .icon(ModContent::tabIcon)
                        .displayItems((parameters, output) -> ModContent.forEachTabItem(output::accept))
                        .build());

        PayloadTypeRegistry.playS2C().register(SyncPayload.TYPE, SyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RevealPayload.TYPE, RevealPayload.CODEC);

        ServerLivingEntityEvents.AFTER_DEATH.register(MobLootBagsEvents::onLivingDeath);
        ServerLifecycleEvents.SERVER_STARTED.register(MobLootBagsEvents::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(MobLootBagsEvents::onServerStopping);
        ServerTickEvents.END_SERVER_TICK.register(MobLootBagsEvents::onServerTick);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resources, success) -> {
            if (success) {
                MobLootBagsEvents.onDataReloaded(server);
            }
        });
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            if (joined) {
                MobLootBagsEvents.onPlayerSync(player);
            }
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) ->
                MobLootBagsEvents.registerCommands(dispatcher, context));
    }

    private static final class FabricRegistrar implements Registrar {
        @Override
        @SuppressWarnings("unchecked")
        public <R, T extends R> Supplier<T> register(ResourceKey<? extends Registry<R>> registry, String path, Function<ResourceLocation, T> factory) {
            ResourceLocation id = MobLootBagsCommon.id(path);
            Registry<R> target = (Registry<R>) BuiltInRegistries.REGISTRY.getOrThrow((ResourceKey) registry);
            T value = factory.apply(id);
            Registry.register(target, id, value);
            return () -> value;
        }
    }

    private static final class FabricPlatform implements Platform {
        @Override
        public Path configDir() {
            return FabricLoader.getInstance().getConfigDir();
        }

        @Override
        public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
            if (ServerPlayNetworking.canSend(player, payload.type())) {
                ServerPlayNetworking.send(player, payload);
            }
        }

        @Override
        public boolean isFakePlayer(ServerPlayer player) {
            return player instanceof FakePlayer;
        }

        @Override
        public boolean isClient() {
            return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
        }
    }
}
