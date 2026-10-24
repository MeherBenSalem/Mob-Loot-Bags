package tn.naizo.moblootbags.common.network;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.loot.LootService.PreviewEntry;

/** Server to client: the opening settings the client must agree on, and the sampled loot previews. */
public record SyncPayload(int holdTicks, boolean sneakOpensStack, int previewLines, Map<String, List<PreviewEntry>> previews)
        implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncPayload> TYPE = new CustomPacketPayload.Type<>(MobLootBagsCommon.id("sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPayload> CODEC = CustomPacketPayload.codec(SyncPayload::write, SyncPayload::read);

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(holdTicks);
        buf.writeBoolean(sneakOpensStack);
        buf.writeVarInt(previewLines);
        buf.writeVarInt(previews.size());
        for (Map.Entry<String, List<PreviewEntry>> entry : previews.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeVarInt(entry.getValue().size());
            for (PreviewEntry preview : entry.getValue()) {
                ItemStack.STREAM_CODEC.encode(buf, preview.icon());
                buf.writeFloat(preview.chance());
                buf.writeVarInt(preview.min());
                buf.writeVarInt(preview.max());
            }
        }
    }

    private static SyncPayload read(RegistryFriendlyByteBuf buf) {
        int holdTicks = buf.readVarInt();
        boolean sneak = buf.readBoolean();
        int lines = buf.readVarInt();
        int tables = Math.min(buf.readVarInt(), 512);
        Map<String, List<PreviewEntry>> previews = new LinkedHashMap<>();
        for (int i = 0; i < tables; i++) {
            String key = buf.readUtf();
            int size = Math.min(buf.readVarInt(), 64);
            List<PreviewEntry> entries = new ArrayList<>(size);
            for (int j = 0; j < size; j++) {
                entries.add(new PreviewEntry(ItemStack.STREAM_CODEC.decode(buf), buf.readFloat(), buf.readVarInt(), buf.readVarInt()));
            }
            previews.put(key, entries);
        }
        return new SyncPayload(holdTicks, sneak, lines, previews);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
