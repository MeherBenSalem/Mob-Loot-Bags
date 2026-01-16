package tn.naizo.moblootbags.network;

import tn.naizo.moblootbags.procedures.TakeExperienceProcedureProcedure;
import tn.naizo.moblootbags.MobLootBagsMod;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.BlockPos;

@EventBusSubscriber
public record LootbagRecyleBlockGUIButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {

	public static final Type<LootbagRecyleBlockGUIButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MobLootBagsMod.MODID, "lootbag_recyle_block_gui_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, LootbagRecyleBlockGUIButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, LootbagRecyleBlockGUIButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new LootbagRecyleBlockGUIButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));
	@Override
	public Type<LootbagRecyleBlockGUIButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final LootbagRecyleBlockGUIButtonMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> handleButtonAction(context.player(), message.buttonID, message.x, message.y, message.z)).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {

			TakeExperienceProcedureProcedure.execute(world, x, y, z, entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		MobLootBagsMod.addNetworkMessage(LootbagRecyleBlockGUIButtonMessage.TYPE, LootbagRecyleBlockGUIButtonMessage.STREAM_CODEC, LootbagRecyleBlockGUIButtonMessage::handleData);
	}
}