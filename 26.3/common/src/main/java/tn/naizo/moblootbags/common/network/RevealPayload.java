package tn.naizo.moblootbags.common.network;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.content.BagType;

/** Server to client: what a player just got from opening {@code opened} bags of one type. Drives the reveal toast. */
public record RevealPayload(BagType bag, int opened, List<ItemStack> items) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RevealPayload> TYPE = new CustomPacketPayload.Type<>(MobLootBagsCommon.id("reveal"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RevealPayload> CODEC = CustomPacketPayload.codec(RevealPayload::write, RevealPayload::read);
    public static final int MAX_ITEMS = 32;

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(bag.ordinal());
        buf.writeVarInt(opened);
        int size = Math.min(items.size(), MAX_ITEMS);
        buf.writeVarInt(size);
        for (int i = 0; i < size; i++) {
            ItemStack.STREAM_CODEC.encode(buf, items.get(i));
        }
    }

    private static RevealPayload read(RegistryFriendlyByteBuf buf) {
        BagType[] types = BagType.values();
        BagType bag = types[Math.floorMod(buf.readVarInt(), types.length)];
        int opened = buf.readVarInt();
        int size = Math.min(buf.readVarInt(), MAX_ITEMS);
        List<ItemStack> items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            items.add(ItemStack.STREAM_CODEC.decode(buf));
        }
        return new RevealPayload(bag, opened, items);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
