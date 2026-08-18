package tn.naizo.moblootbags.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import tn.naizo.moblootbags.common.LootBagOpenerBlockEntity;
import tn.naizo.moblootbags.common.LootBagOpenerBlock;
import tn.naizo.moblootbags.common.LootBagRecyclerBlockEntity;
import tn.naizo.moblootbags.common.LootBagRecyclerBlock;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.MobLootBagsConfig;
import tn.naizo.moblootbags.common.MobLootBagsLogic;
import tn.naizo.moblootbags.common.MobLootBagsRegistry;

@Mod(MobLootBagsCommon.MOD_ID)
public final class MobLootBagsNeoForge {
    private static final String MODID = MobLootBagsCommon.MOD_ID;

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    private static final DeferredHolder<Block, Block> LOOT_BAG_RECYCLE_BLOCK = BLOCKS.register("loot_bag_recycle_block", name -> new LootBagRecyclerBlock(blockProperties(name)));
    private static final DeferredHolder<Block, Block> LOOT_BAG_OPENER_BLOCK = BLOCKS.register("loot_bag_opener_block", name -> new LootBagOpenerBlock(blockProperties(name)));
    private static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootBagRecyclerBlockEntity>> LOOT_BAG_RECYCLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "loot_bag_recycle_block",
            () -> new BlockEntityType<>(LootBagRecyclerBlockEntity::new, LOOT_BAG_RECYCLE_BLOCK.get())
    );
    private static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootBagOpenerBlockEntity>> LOOT_BAG_OPENER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "loot_bag_opener_block",
            () -> new BlockEntityType<>(LootBagOpenerBlockEntity::new, LOOT_BAG_OPENER_BLOCK.get())
    );

    private static final DeferredHolder<Item, Item> COMMONLOOTBAG = ITEMS.register("commonlootbag", name -> MobLootBagsRegistry.createTierLootBagItem("common", itemProperties(name)));
    private static final DeferredHolder<Item, Item> UNCOMMONLOOTBAG = ITEMS.register("uncommonlootbag", name -> MobLootBagsRegistry.createTierLootBagItem("uncommon", itemProperties(name)));
    private static final DeferredHolder<Item, Item> RARELOOTBAG = ITEMS.register("rarelootbag", name -> MobLootBagsRegistry.createTierLootBagItem("rare", itemProperties(name)));
    private static final DeferredHolder<Item, Item> EPICLOOTBAG = ITEMS.register("epiclootbag", name -> MobLootBagsRegistry.createTierLootBagItem("epic", itemProperties(name).rarity(net.minecraft.world.item.Rarity.RARE)));
    private static final DeferredHolder<Item, Item> LEGENDARYLOOTBAG = ITEMS.register("legendarylootbag", name -> MobLootBagsRegistry.createTierLootBagItem("legendary", itemProperties(name).rarity(net.minecraft.world.item.Rarity.EPIC)));
    private static final DeferredHolder<Item, Item> CURSED_LOOTBAG = ITEMS.register("cursed_lootbag", name -> MobLootBagsRegistry.createCursedLootBagItem(itemProperties(name).rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    private static final DeferredHolder<Item, Item> TIMED_LOOT_BAG = ITEMS.register("timed_loot_bag", name -> MobLootBagsRegistry.createTimedLootBagItem(itemProperties(name).rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    private static final DeferredHolder<Item, Item> DIAMOND_KEY = ITEMS.register("diamond_key", name -> MobLootBagsRegistry.createDiamondKeyItem(itemProperties(name).stacksTo(16).rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    private static final DeferredHolder<Item, Item> LOCKED_LOOTBAGS = ITEMS.register("locked_lootbags", name -> MobLootBagsRegistry.createLockedLootBagItem(itemProperties(name).rarity(net.minecraft.world.item.Rarity.EPIC)));
    private static final DeferredHolder<Item, Item> SUMMONING_LOOTBAGS = ITEMS.register("summoning_lootbags", name -> MobLootBagsRegistry.createSummoningLootBagItem(itemProperties(name).rarity(net.minecraft.world.item.Rarity.RARE)));

    private static final DeferredHolder<Item, Item> LOOT_BAG_RECYCLE_BLOCK_ITEM = ITEMS.register(
            "loot_bag_recycle_block",
            name -> MobLootBagsRegistry.createTooltipBlockItem(LOOT_BAG_RECYCLE_BLOCK.get(), itemProperties(name), "block.mob_loot_bags.loot_bag_recycle_block.description_0")
    );
    private static final DeferredHolder<Item, Item> LOOT_BAG_OPENER_BLOCK_ITEM = ITEMS.register(
            "loot_bag_opener_block",
            name -> MobLootBagsRegistry.createTooltipBlockItem(LOOT_BAG_OPENER_BLOCK.get(), itemProperties(name), "block.mob_loot_bags.loot_bag_opener_block.description_0")
    );

    private static final DeferredHolder<SoundEvent, SoundEvent> DRAMATIC = SOUNDS.register("dramatic", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(MODID, "dramatic")));
    private static final DeferredHolder<SoundEvent, SoundEvent> LOOTBAG_SFX_1 = SOUNDS.register("lootbag_sfx_1", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(MODID, "lootbag_sfx_1")));
    private static final DeferredHolder<SoundEvent, SoundEvent> LOOTBAG_SFX_2 = SOUNDS.register("lootbag_sfx_2", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(MODID, "lootbag_sfx_2")));

    private static final DeferredHolder<CreativeModeTab, CreativeModeTab> LOOT_BAGS_TAB = CREATIVE_TABS.register("loot_bags_creative_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.mob_loot_bags.loot_bags_creative_tab"))
            .icon(() -> new ItemStack(COMMONLOOTBAG.get()))
            .displayItems((parameters, output) -> {
                output.accept(LOOT_BAG_RECYCLE_BLOCK_ITEM.get());
                output.accept(LOOT_BAG_OPENER_BLOCK_ITEM.get());
                output.accept(COMMONLOOTBAG.get());
                output.accept(UNCOMMONLOOTBAG.get());
                output.accept(RARELOOTBAG.get());
                output.accept(EPICLOOTBAG.get());
                output.accept(LEGENDARYLOOTBAG.get());
                output.accept(CURSED_LOOTBAG.get());
                output.accept(TIMED_LOOT_BAG.get());
                output.accept(DIAMOND_KEY.get());
                output.accept(LOCKED_LOOTBAGS.get());
                output.accept(SUMMONING_LOOTBAGS.get());
            })
            .build());

    public MobLootBagsNeoForge(IEventBus modEventBus) {
        MobLootBagsConfig.init();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        SOUNDS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(this::onCommonSetup);
        NeoForge.EVENT_BUS.addListener(this::onLivingDeath);
    }

    private static BlockBehaviour.Properties blockProperties(Identifier name) {
        return BlockBehaviour.Properties.of()
                .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                .strength(1.0F, 10.0F)
                .sound(net.minecraft.world.level.block.SoundType.METAL)
                .setId(ResourceKey.create(Registries.BLOCK, name));
    }

    private static Item.Properties itemProperties(Identifier name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, name));
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MobLootBagsRegistry.LOOT_BAG_RECYCLE_BLOCK = LOOT_BAG_RECYCLE_BLOCK.get();
            MobLootBagsRegistry.LOOT_BAG_OPENER_BLOCK = LOOT_BAG_OPENER_BLOCK.get();
            MobLootBagsRegistry.LOOT_BAG_RECYCLE_BLOCK_ENTITY = LOOT_BAG_RECYCLE_BLOCK_ENTITY.get();
            MobLootBagsRegistry.LOOT_BAG_OPENER_BLOCK_ENTITY = LOOT_BAG_OPENER_BLOCK_ENTITY.get();
            MobLootBagsRegistry.COMMONLOOTBAG = COMMONLOOTBAG.get();
            MobLootBagsRegistry.UNCOMMONLOOTBAG = UNCOMMONLOOTBAG.get();
            MobLootBagsRegistry.RARELOOTBAG = RARELOOTBAG.get();
            MobLootBagsRegistry.EPICLOOTBAG = EPICLOOTBAG.get();
            MobLootBagsRegistry.LEGENDARYLOOTBAG = LEGENDARYLOOTBAG.get();
            MobLootBagsRegistry.CURSED_LOOTBAG = CURSED_LOOTBAG.get();
            MobLootBagsRegistry.TIMED_LOOT_BAG = TIMED_LOOT_BAG.get();
            MobLootBagsRegistry.DIAMOND_KEY = DIAMOND_KEY.get();
            MobLootBagsRegistry.LOCKED_LOOTBAGS = LOCKED_LOOTBAGS.get();
            MobLootBagsRegistry.SUMMONING_LOOTBAGS = SUMMONING_LOOTBAGS.get();
            MobLootBagsRegistry.DRAMATIC = DRAMATIC.get();
            MobLootBagsRegistry.LOOTBAG_SFX_1 = LOOTBAG_SFX_1.get();
            MobLootBagsRegistry.LOOTBAG_SFX_2 = LOOTBAG_SFX_2.get();
            MobLootBagsRegistry.LOOT_BAG_RECYCLE_BLOCK_ITEM = LOOT_BAG_RECYCLE_BLOCK_ITEM.get();
            MobLootBagsRegistry.LOOT_BAG_OPENER_BLOCK_ITEM = LOOT_BAG_OPENER_BLOCK_ITEM.get();
            LOOT_BAGS_TAB.get();
        });
    }

    private void onLivingDeath(LivingDeathEvent event) {
        MobLootBagsLogic.onMobDeath(event.getEntity(), event.getSource());
    }
}
