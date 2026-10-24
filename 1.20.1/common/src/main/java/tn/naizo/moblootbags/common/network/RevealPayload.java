package tn.naizo.moblootbags.common.network;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.content.BagType;

/** Server to client: what a player just got from opening {@code opened} bags of one type. Drives the reveal toast. */
public record RevealPayload(BagType bag, int opened, List<ItemStack> items) implements Payload {
    public static final ResourceLocation ID = MobLootBagsCommon.id("reveal");
    public static final int MAX_ITEMS = 32;

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(bag.ordinal());
        buf.writeVarInt(opened);
        int size = Math.min(items.size(), MAX_ITEMS);
        buf.writeVarInt(size);
        for (int i = 0; i < size; i++) {
            buf.writeItem(items.get(i));
        }
    }

    public static RevealPayload read(FriendlyByteBuf buf) {
        BagType[] types = BagType.values();
        BagType bag = types[Math.floorMod(buf.readVarInt(), types.length)];
        int opened = buf.readVarInt();
        int size = Math.min(buf.readVarInt(), MAX_ITEMS);
        List<ItemStack> items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            items.add(buf.readItem());
        }
        return new RevealPayload(bag, opened, items);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
