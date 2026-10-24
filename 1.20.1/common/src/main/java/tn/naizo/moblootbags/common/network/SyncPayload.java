package tn.naizo.moblootbags.common.network;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.loot.LootService.PreviewEntry;

/** Server to client: the opening settings the client must agree on, and the sampled loot previews. */
public record SyncPayload(int holdTicks, boolean sneakOpensStack, int previewLines, Map<String, List<PreviewEntry>> previews)
        implements Payload {
    public static final ResourceLocation ID = MobLootBagsCommon.id("sync");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(holdTicks);
        buf.writeBoolean(sneakOpensStack);
        buf.writeVarInt(previewLines);
        buf.writeVarInt(previews.size());
        for (Map.Entry<String, List<PreviewEntry>> entry : previews.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeVarInt(entry.getValue().size());
            for (PreviewEntry preview : entry.getValue()) {
                buf.writeItem(preview.icon());
                buf.writeFloat(preview.chance());
                buf.writeVarInt(preview.min());
                buf.writeVarInt(preview.max());
            }
        }
    }

    public static SyncPayload read(FriendlyByteBuf buf) {
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
                entries.add(new PreviewEntry(buf.readItem(), buf.readFloat(), buf.readVarInt(), buf.readVarInt()));
            }
            previews.put(key, entries);
        }
        return new SyncPayload(holdTicks, sneak, lines, previews);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
