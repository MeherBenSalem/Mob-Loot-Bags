package tn.naizo.moblootbags.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import tn.naizo.moblootbags.common.LootBagOpenerBlockEntity;
import tn.naizo.moblootbags.common.LootBagRecyclerBlockEntity;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.MobLootBagsLogic;
import tn.naizo.moblootbags.common.MobLootBagsRegistry;

public final class MobLootBagsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MobLootBagsCommon.init();

        MobLootBagsRegistry.LOOT_BAG_RECYCLE_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath("mob_loot_bags", "loot_bag_recycle_block"),
                FabricBlockEntityTypeBuilder.create(LootBagRecyclerBlockEntity::new, MobLootBagsRegistry.LOOT_BAG_RECYCLE_BLOCK).build()
        );
        MobLootBagsRegistry.LOOT_BAG_OPENER_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath("mob_loot_bags", "loot_bag_opener_block"),
                FabricBlockEntityTypeBuilder.create(LootBagOpenerBlockEntity::new, MobLootBagsRegistry.LOOT_BAG_OPENER_BLOCK).build()
        );

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> MobLootBagsLogic.onMobDeath(entity, source));

        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath("mob_loot_bags", "loot_bags_creative_tab"),
                FabricItemGroup.builder()
                        .title(Component.translatable("itemGroup.mob_loot_bags.loot_bags_creative_tab"))
                        .icon(() -> new ItemStack(MobLootBagsRegistry.COMMONLOOTBAG))
                        .displayItems((parameters, output) -> MobLootBagsRegistry.forEachRegisteredItem(output::accept))
                        .build()
        );
    }
}
