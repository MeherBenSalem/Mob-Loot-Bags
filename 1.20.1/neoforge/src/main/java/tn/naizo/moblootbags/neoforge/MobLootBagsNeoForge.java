package tn.naizo.moblootbags.neoforge;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.MobLootBagsEvents;
import tn.naizo.moblootbags.common.client.MobLootBagsClient;
import tn.naizo.moblootbags.common.content.ModContent;
import tn.naizo.moblootbags.common.machine.MachineBlockEntity;
import tn.naizo.moblootbags.common.network.Payload;
import tn.naizo.moblootbags.common.network.RevealPayload;
import tn.naizo.moblootbags.common.network.SyncPayload;
import tn.naizo.moblootbags.common.platform.Platform;
import tn.naizo.moblootbags.common.platform.Registrar;

/** The Forge 47 entry point (this workspace's {@code neoforge} module builds for Forge on 1.20.1). */
@Mod(MobLootBagsCommon.MOD_ID)
public final class MobLootBagsNeoForge {
    /** Both sides must speak the same protocol, as with the 26.x payload registrar. */
    private static final String PROTOCOL = "2";
    private static final Map<ResourceLocation, SimpleChannel> CHANNELS = new HashMap<>();

    private final Map<ResourceKey<?>, DeferredRegister<?>> registers = new HashMap<>();

    public MobLootBagsNeoForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        MobLootBagsCommon.init(new ForgePlatform(), new ForgeRegistrar(), new ModContent.MenuTypeFactory() {
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
        DeferredRegister<CreativeModeTab> tabs = register(Registries.CREATIVE_MODE_TAB);
        tabs.register("loot_bags_creative_tab", () -> CreativeModeTab.builder()
                .title(ModContent.tabTitle())
                .icon(ModContent::tabIcon)
                .displayItems((parameters, output) -> ModContent.forEachTabItem(output::accept))
                .build());
        registers.values().forEach(deferred -> deferred.register(modBus));

        registerPayloads();
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, this::attachCapabilities);
        MinecraftForge.EVENT_BUS.addListener((LivingDeathEvent event) -> MobLootBagsEvents.onLivingDeath(event.getEntity(), event.getSource()));
        MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) -> MobLootBagsEvents.onServerStarted(event.getServer()));
        MinecraftForge.EVENT_BUS.addListener((ServerStoppingEvent event) -> MobLootBagsEvents.onServerStopping(event.getServer()));
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                MobLootBagsEvents.onServerTick(event.getServer());
            }
        });
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
                MobLootBagsEvents.registerCommands(event.getDispatcher(), event.getBuildContext()));
        MinecraftForge.EVENT_BUS.addListener((OnDatapackSyncEvent event) -> {
            if (event.getPlayer() == null) {
                MobLootBagsEvents.onDataReloaded(event.getPlayerList().getServer());
            } else {
                MobLootBagsEvents.onPlayerSync(event.getPlayer());
            }
        });
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MobLootBagsNeoForgeClient.init(modBus);
        }
    }

    @SuppressWarnings("unchecked")
    private <R> DeferredRegister<R> register(ResourceKey<? extends Registry<R>> registry) {
        return (DeferredRegister<R>) registers.computeIfAbsent(registry, key -> DeferredRegister.create(registry, MobLootBagsCommon.MOD_ID));
    }

    /** One channel per payload, named like the 26.x payload types ({@code mob_loot_bags:sync}, {@code mob_loot_bags:reveal}). */
    private static void registerPayloads() {
        channel(SyncPayload.ID).messageBuilder(SyncPayload.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncPayload::write)
                .decoder(SyncPayload::read)
                .consumerMainThread((payload, context) ->
                        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MobLootBagsClient.handleSync(payload)))
                .add();
        channel(RevealPayload.ID).messageBuilder(RevealPayload.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(RevealPayload::write)
                .decoder(RevealPayload::read)
                .consumerMainThread((payload, context) ->
                        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MobLootBagsClient.handleReveal(payload)))
                .add();
    }

    private static SimpleChannel channel(ResourceLocation id) {
        SimpleChannel channel = NetworkRegistry.newSimpleChannel(id, () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
        CHANNELS.put(id, channel);
        return channel;
    }

    private void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof MachineBlockEntity machine) {
            MachineItemHandlers handlers = new MachineItemHandlers(machine);
            event.addCapability(MobLootBagsCommon.id("items"), handlers);
            event.addListener(handlers::invalidate);
        }
    }

    /** Hoppers and pipes see a machine through sided wrappers that keep its input and output faces; side-less queries see every slot. */
    private static final class MachineItemHandlers implements ICapabilityProvider {
        private final LazyOptional<IItemHandler> all;
        private final Map<Direction, LazyOptional<IItemHandler>> sides = new EnumMap<>(Direction.class);

        MachineItemHandlers(MachineBlockEntity machine) {
            all = LazyOptional.of(() -> new InvWrapper(machine));
            for (Direction side : Direction.values()) {
                sides.put(side, LazyOptional.of(() -> new SidedInvWrapper(machine, side)));
            }
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
            if (capability != ForgeCapabilities.ITEM_HANDLER) {
                return LazyOptional.empty();
            }
            return (side == null ? all : sides.get(side)).cast();
        }

        void invalidate() {
            all.invalidate();
            sides.values().forEach(LazyOptional::invalidate);
        }
    }

    private final class ForgeRegistrar implements Registrar {
        @Override
        public <R, T extends R> Supplier<T> register(ResourceKey<? extends Registry<R>> registry, String path, Function<ResourceLocation, T> factory) {
            ResourceLocation id = MobLootBagsCommon.id(path);
            return MobLootBagsNeoForge.this.register(registry).register(path, () -> factory.apply(id));
        }
    }

    private static final class ForgePlatform implements Platform {
        @Override
        public Path configDir() {
            return FMLPaths.CONFIGDIR.get();
        }

        @Override
        public void sendToPlayer(ServerPlayer player, Payload payload) {
            CHANNELS.get(payload.id()).send(PacketDistributor.PLAYER.with(() -> player), payload);
        }

        @Override
        public boolean isFakePlayer(ServerPlayer player) {
            return player instanceof FakePlayer;
        }

        @Override
        public boolean isClient() {
            return FMLEnvironment.dist == Dist.CLIENT;
        }
    }
}
