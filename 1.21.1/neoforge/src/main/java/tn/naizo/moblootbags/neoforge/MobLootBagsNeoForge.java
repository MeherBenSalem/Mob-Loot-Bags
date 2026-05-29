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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;
import tn.naizo.moblootbags.common.LootBagOpenerBlockEntity;
import tn.naizo.moblootbags.common.LootBagRecyclerBlockEntity;
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

    private static final DeferredHolder<Block, Block> LOOT_BAG_RECYCLE_BLOCK = BLOCKS.register("loot_bag_recycle_block", MobLootBagsRegistry::createLootBagRecyclerBlock);
    private static final DeferredHolder<Block, Block> LOOT_BAG_OPENER_BLOCK = BLOCKS.register("loot_bag_opener_block", MobLootBagsRegistry::createLootBagOpenerBlock);
    private static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootBagRecyclerBlockEntity>> LOOT_BAG_RECYCLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "loot_bag_recycle_block",
            () -> BlockEntityType.Builder.of(LootBagRecyclerBlockEntity::new, LOOT_BAG_RECYCLE_BLOCK.get()).build(null)
    );
    private static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootBagOpenerBlockEntity>> LOOT_BAG_OPENER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "loot_bag_opener_block",
            () -> BlockEntityType.Builder.of(LootBagOpenerBlockEntity::new, LOOT_BAG_OPENER_BLOCK.get()).build(null)
    );

    private static final DeferredHolder<Item, Item> COMMONLOOTBAG = ITEMS.register("commonlootbag", () -> MobLootBagsRegistry.createTierLootBagItem("common", new Item.Properties()));
    private static final DeferredHolder<Item, Item> UNCOMMONLOOTBAG = ITEMS.register("uncommonlootbag", () -> MobLootBagsRegistry.createTierLootBagItem("uncommon", new Item.Properties()));
    private static final DeferredHolder<Item, Item> RARELOOTBAG = ITEMS.register("rarelootbag", () -> MobLootBagsRegistry.createTierLootBagItem("rare", new Item.Properties()));
    private static final DeferredHolder<Item, Item> EPICLOOTBAG = ITEMS.register("epiclootbag", () -> MobLootBagsRegistry.createTierLootBagItem("epic", new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    private static final DeferredHolder<Item, Item> LEGENDARYLOOTBAG = ITEMS.register("legendarylootbag", () -> MobLootBagsRegistry.createTierLootBagItem("legendary", new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));
    private static final DeferredHolder<Item, Item> CURSED_LOOTBAG = ITEMS.register("cursed_lootbag", () -> MobLootBagsRegistry.createCursedLootBagItem(new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    private static final DeferredHolder<Item, Item> TIMED_LOOT_BAG = ITEMS.register("timed_loot_bag", () -> MobLootBagsRegistry.createTimedLootBagItem(new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    private static final DeferredHolder<Item, Item> DIAMOND_KEY = ITEMS.register("diamond_key", () -> MobLootBagsRegistry.createDiamondKeyItem(new Item.Properties().stacksTo(16).rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    private static final DeferredHolder<Item, Item> LOCKED_LOOTBAGS = ITEMS.register("locked_lootbags", () -> MobLootBagsRegistry.createLockedLootBagItem(new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));
    private static final DeferredHolder<Item, Item> SUMMONING_LOOTBAGS = ITEMS.register("summoning_lootbags", () -> MobLootBagsRegistry.createSummoningLootBagItem(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));

    private static final DeferredHolder<Item, Item> LOOT_BAG_RECYCLE_BLOCK_ITEM = ITEMS.register(
            "loot_bag_recycle_block",
            () -> MobLootBagsRegistry.createTooltipBlockItem(LOOT_BAG_RECYCLE_BLOCK.get(), new Item.Properties(), "block.mob_loot_bags.loot_bag_recycle_block.description_0")
    );
    private static final DeferredHolder<Item, Item> LOOT_BAG_OPENER_BLOCK_ITEM = ITEMS.register(
            "loot_bag_opener_block",
            () -> MobLootBagsRegistry.createTooltipBlockItem(LOOT_BAG_OPENER_BLOCK.get(), new Item.Properties(), "block.mob_loot_bags.loot_bag_opener_block.description_0")
    );

    private static final DeferredHolder<SoundEvent, SoundEvent> DRAMATIC = SOUNDS.register("dramatic", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "dramatic")));
    private static final DeferredHolder<SoundEvent, SoundEvent> LOOTBAG_SFX_1 = SOUNDS.register("lootbag_sfx_1", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "lootbag_sfx_1")));
    private static final DeferredHolder<SoundEvent, SoundEvent> LOOTBAG_SFX_2 = SOUNDS.register("lootbag_sfx_2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "lootbag_sfx_2")));

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
        // NeoForge registers blocks/items via DeferredRegister — do NOT call MobLootBagsCommon.init()
        // which tries vanilla Registry.register() on frozen registries.
        // Instead, init config here and set static fields in onCommonSetup.
        MobLootBagsConfig.init();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        SOUNDS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(this::onCommonSetup);
        NeoForge.EVENT_BUS.addListener(this::onLivingDeath);
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
