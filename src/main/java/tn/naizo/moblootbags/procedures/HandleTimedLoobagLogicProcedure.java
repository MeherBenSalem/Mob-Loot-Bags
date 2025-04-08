package tn.naizo.moblootbags.procedures;

import tn.naizo.moblootbags.configuration.MainConfigFileConfiguration;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

public class HandleTimedLoobagLogicProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		double lootTableChosen = 0;
		double counter = 0;
		if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, itemstack) != 0) {
			counter = 0;
			for (String stringiterator : MainConfigFileConfiguration.LUCKY_LIST.get()) {
				counter = counter + 1;
			}
			lootTableChosen = Mth.nextInt(RandomSource.create(), 0, (int) counter);
			counter = 0;
			for (String stringiterator : MainConfigFileConfiguration.LUCKY_LIST.get()) {
				if (counter == lootTableChosen) {
					if (world instanceof ServerLevel _level)
						_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
								stringiterator);
					break;
				} else {
					counter = counter + 1;
				}
			}
			if (entity instanceof Player _player) {
				ItemStack _stktoremove = itemstack;
				_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
			}
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal(("\u00A74This lootbag isn't ready to be opened yet" + " wait for " + Math.round(itemstack.getOrCreateTag().getDouble("lb_timer") / 20) + " sec")), false);
		}
	}
}
