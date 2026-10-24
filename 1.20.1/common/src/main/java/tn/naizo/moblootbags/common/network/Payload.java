package tn.naizo.moblootbags.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * A server-to-client message. 1.20.1 has no {@code CustomPacketPayload}, so each loader sends these on a channel named
 * by {@link #id()} (the same ids as the 26.x payload types).
 */
public interface Payload {
    ResourceLocation id();

    void write(FriendlyByteBuf buf);
}
