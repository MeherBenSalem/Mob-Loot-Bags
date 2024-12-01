package net.mcreator.moblootbags.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.CommandSourceStack;

import net.mcreator.moblootbags.init.MobLootBagsModItems;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;

public class ChangeBagRewardCommandProcedure {
	public static void execute(CommandContext<CommandSourceStack> arguments, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == MobLootBagsModItems.CUSTOM_LOOT_BAG.get()) {
			(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().putString("mlb_table", (new Object() {
				public String getMessage() {
					try {
						return MessageArgument.getMessage(arguments, "loot_table_name").getString();
					} catch (CommandSyntaxException ignored) {
						return "";
					}
				}
			}).getMessage());
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("You can't change the rewards of this item"), false);
		}
	}
}
