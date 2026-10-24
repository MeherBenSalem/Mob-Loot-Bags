package tn.naizo.moblootbags.fabric;

import java.nio.file.Path;
import java.util.function.BiFunction;
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
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.MobLootBagsEvents;
import tn.naizo.moblootbags.common.content.ModContent;
import tn.naizo.moblootbags.common.network.Payload;
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
        }, new ModContent.BlockEntityTypeFactory() {
            @Override
            public <T extends BlockEntity> BlockEntityType<T> create(BiFunction<BlockPos, BlockState, T> constructor, Block block) {
                return BlockEntityType.Builder.of(constructor::apply, block).build(null);
            }
        });
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MobLootBagsCommon.id("loot_bags_creative_tab"),
                FabricItemGroup.builder()
                        .title(ModContent.tabTitle())
                        .icon(ModContent::tabIcon)
                        .displayItems((parameters, output) -> ModContent.forEachTabItem(output::accept))
                        .build());

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
        public void sendToPlayer(ServerPlayer player, Payload payload) {
            if (ServerPlayNetworking.canSend(player, payload.id())) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                payload.write(buf);
                ServerPlayNetworking.send(player, payload.id(), buf);
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
