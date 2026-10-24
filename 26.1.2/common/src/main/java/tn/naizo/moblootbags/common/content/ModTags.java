package tn.naizo.moblootbags.common.content;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import tn.naizo.moblootbags.common.MobLootBagsCommon;

public final class ModTags {
    /** Always drop several high-tier bags. */
    public static final TagKey<EntityType<?>> BOSSES = TagKey.create(Registries.ENTITY_TYPE, MobLootBagsCommon.id("bosses"));
    /** Can spawn holding a loot bag in the off hand. */
    public static final TagKey<EntityType<?>> BAG_CARRIERS = TagKey.create(Registries.ENTITY_TYPE, MobLootBagsCommon.id("bag_carriers"));
    /** Never drop bags, whatever the config says. */
    public static final TagKey<EntityType<?>> NO_BAGS = TagKey.create(Registries.ENTITY_TYPE, MobLootBagsCommon.id("no_bags"));

    public static final TagKey<Item> LOOT_BAGS = TagKey.create(Registries.ITEM, MobLootBagsCommon.id("loot_bags"));
    public static final TagKey<Item> RECYCLER_JUNK = TagKey.create(Registries.ITEM, MobLootBagsCommon.id("recycler_junk"));

    private ModTags() {
    }

    public static boolean is(EntityType<?> type, TagKey<EntityType<?>> tag) {
        return type.builtInRegistryHolder().is(tag);
    }
}
