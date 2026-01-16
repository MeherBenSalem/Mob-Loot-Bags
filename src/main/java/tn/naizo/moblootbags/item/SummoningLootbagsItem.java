package tn.naizo.moblootbags.item;

import tn.naizo.moblootbags.procedures.HandleNormalBagsLogicProcedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;

public class SummoningLootbagsItem extends Item {
	public SummoningLootbagsItem(Item.Properties properties) {
		super(properties.rarity(Rarity.EPIC));
	}

	@Override
	public InteractionResult use(Level world, Player entity, InteractionHand hand) {
		InteractionResult ar = super.use(world, entity, hand);
		HandleNormalBagsLogicProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, entity.getItemInHand(hand));
		return ar;
	}
}