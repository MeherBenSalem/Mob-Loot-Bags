
package net.mcreator.moblootbags.network;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.mcreator.moblootbags.world.inventory.LootbagRecyleBlockGUIMenu;
import net.mcreator.moblootbags.procedures.LootBagRecycleProcedureProcedure;
import net.mcreator.moblootbags.MobLootBagsMod;

import java.util.function.Supplier;
import java.util.HashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LootbagRecyleBlockGUISlotMessage {
	private final int slotID, x, y, z, changeType, meta;

	public LootbagRecyleBlockGUISlotMessage(int slotID, int x, int y, int z, int changeType, int meta) {
		this.slotID = slotID;
		this.x = x;
		this.y = y;
		this.z = z;
		this.changeType = changeType;
		this.meta = meta;
	}

	public LootbagRecyleBlockGUISlotMessage(FriendlyByteBuf buffer) {
		this.slotID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.changeType = buffer.readInt();
		this.meta = buffer.readInt();
	}

	public static void buffer(LootbagRecyleBlockGUISlotMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.slotID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
		buffer.writeInt(message.changeType);
		buffer.writeInt(message.meta);
	}

	public static void handler(LootbagRecyleBlockGUISlotMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			int slotID = message.slotID;
			int changeType = message.changeType;
			int meta = message.meta;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			handleSlotAction(entity, slotID, changeType, meta, x, y, z);
		});
		context.setPacketHandled(true);
	}

	public static void handleSlotAction(Player entity, int slot, int changeType, int meta, int x, int y, int z) {
		Level world = entity.level();
		HashMap guistate = LootbagRecyleBlockGUIMenu.guistate;
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (slot == 0 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 1 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 2 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 3 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 4 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 5 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 6 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 7 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 8 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 9 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 10 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 11 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 12 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 13 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 14 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 15 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 16 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 17 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 18 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 19 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 20 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 21 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 22 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 23 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 24 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 25 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
		if (slot == 26 && changeType == 0) {

			LootBagRecycleProcedureProcedure.execute(world, x, y, z, slot);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		MobLootBagsMod.addNetworkMessage(LootbagRecyleBlockGUISlotMessage.class, LootbagRecyleBlockGUISlotMessage::buffer, LootbagRecyleBlockGUISlotMessage::new, LootbagRecyleBlockGUISlotMessage::handler);
	}
}
