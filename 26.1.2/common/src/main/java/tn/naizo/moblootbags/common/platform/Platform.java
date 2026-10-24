package tn.naizo.moblootbags.common.platform;

import java.nio.file.Path;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/** The few things common code needs from the loader. Each loader sets an implementation in {@code MobLootBagsCommon.init}. */
public interface Platform {
    Path configDir();

    void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

    /** True for automation players (deployers, turtles, fake players of other mods). */
    boolean isFakePlayer(ServerPlayer player);

    boolean isClient();
}
