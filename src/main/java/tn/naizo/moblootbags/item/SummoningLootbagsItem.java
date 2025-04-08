
package tn.naizo.moblootbags.item;

import tn.naizo.moblootbags.procedures.HandleNormalBagsLogicProcedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;

public class SummoningLootbagsItem extends Item {
	public SummoningLootbagsItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.EPIC));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		HandleNormalBagsLogicProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}
}
