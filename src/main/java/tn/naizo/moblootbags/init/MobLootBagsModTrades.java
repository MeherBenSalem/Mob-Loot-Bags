/*
*	MCreator note: This file will be REGENERATED on each build.
*/
package tn.naizo.moblootbags.init;

import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

@EventBusSubscriber
public class MobLootBagsModTrades {
	@SubscribeEvent
	public static void registerWanderingTrades(WandererTradesEvent event) {
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD), new ItemStack(MobLootBagsModItems.COMMONLOOTBAG.get()), 10, 5, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 4), new ItemStack(MobLootBagsModItems.UNCOMMONLOOTBAG.get()), 10, 5, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 8), new ItemStack(MobLootBagsModItems.RARELOOTBAG.get()), 10, 10, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 12), new ItemStack(MobLootBagsModItems.EPICLOOTBAG.get()), 10, 15, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 20), new ItemStack(MobLootBagsModItems.LEGENDARYLOOTBAG.get()), 10, 20, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD), new ItemStack(MobLootBagsModItems.DIAMOND_KEY.get()), 10, 15, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 16), new ItemStack(MobLootBagsModItems.LOCKED_LOOTBAGS.get()), 10, 20, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 6), new ItemStack(MobLootBagsModItems.SUMMONING_LOOTBAGS.get()), 10, 8, 0.05f));
	}
}