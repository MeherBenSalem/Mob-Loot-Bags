package tn.naizo.moblootbags.common;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import tn.naizo.moblootbags.common.command.LootBagsCommand;
import tn.naizo.moblootbags.common.drop.DropEngine;
import tn.naizo.moblootbags.common.drop.PlayerLootData;
import tn.naizo.moblootbags.common.loot.LootService;
import tn.naizo.moblootbags.common.network.Network;

/** Loader-independent event handlers; each loader forwards its events here. */
public final class MobLootBagsEvents {
    private static final int AUTOSAVE_TICKS = 20 * 60 * 5;
    private static int ticks;

    private MobLootBagsEvents() {
    }

    public static void onServerStarted(MinecraftServer server) {
        PlayerLootData.load(server);
        LootService.rebuildPreviews(server);
    }

    public static void onServerStopping(MinecraftServer server) {
        PlayerLootData.unload();
    }

    public static void onServerTick(MinecraftServer server) {
        if (++ticks >= AUTOSAVE_TICKS) {
            ticks = 0;
            PlayerLootData.save(false);
        }
    }

    /** After {@code /reload}: loot tables may have changed. */
    public static void onDataReloaded(MinecraftServer server) {
        LootService.rebuildPreviews(server);
        Network.syncAll(server);
    }

    /** A player joined (or datapacks were re-sent to them). */
    public static void onPlayerSync(ServerPlayer player) {
        Network.sync(player);
    }

    public static void onLivingDeath(LivingEntity entity, DamageSource source) {
        DropEngine.onDeath(entity, source);
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        LootBagsCommand.register(dispatcher, context);
    }
}
