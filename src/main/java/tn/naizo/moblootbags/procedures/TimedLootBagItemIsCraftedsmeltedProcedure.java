package tn.naizo.moblootbags.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

public class TimedLootBagItemIsCraftedsmeltedProcedure {
	public static void execute(LevelAccessor world, ItemStack itemstack) {
		if (!(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, itemstack) != 0)) {
			if (!itemstack.getOrCreateTag().getBoolean("is_active")) {
				itemstack.getOrCreateTag().putBoolean("is_active", true);
				itemstack.getOrCreateTag().putDouble("lb_timer", (Mth.nextInt(RandomSource.create(), 6000, 12000)));
			} else if (itemstack.getOrCreateTag().getBoolean("is_active") && itemstack.getOrCreateTag().getDouble("lb_timer") > 0) {
				itemstack.getOrCreateTag().putDouble("lb_timer", (itemstack.getOrCreateTag().getDouble("lb_timer") - 1));
			} else {
				itemstack.enchant(Enchantments.UNBREAKING, 10);
			}
		}
	}
}