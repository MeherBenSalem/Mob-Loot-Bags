
package tn.naizo.moblootbags.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class DiamondKeyItem extends Item {
	public DiamondKeyItem() {
		super(new Item.Properties().stacksTo(8).rarity(Rarity.COMMON));
	}
}
