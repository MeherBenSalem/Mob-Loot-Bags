package tn.naizo.moblootbags.common;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class MobLootBagsRegistry {
    private static boolean registered;

    public static Block LOOT_BAG_RECYCLE_BLOCK;
    public static Block LOOT_BAG_OPENER_BLOCK;
    public static BlockEntityType<LootBagRecyclerBlockEntity> LOOT_BAG_RECYCLE_BLOCK_ENTITY;
    public static BlockEntityType<LootBagOpenerBlockEntity> LOOT_BAG_OPENER_BLOCK_ENTITY;
    public static Item LOOT_BAG_RECYCLE_BLOCK_ITEM;
    public static Item LOOT_BAG_OPENER_BLOCK_ITEM;

    public static Item COMMONLOOTBAG;
    public static Item UNCOMMONLOOTBAG;
    public static Item RARELOOTBAG;
    public static Item EPICLOOTBAG;
    public static Item LEGENDARYLOOTBAG;
    public static Item CURSED_LOOTBAG;
    public static Item TIMED_LOOT_BAG;
    public static Item DIAMOND_KEY;
    public static Item LOCKED_LOOTBAGS;
    public static Item SUMMONING_LOOTBAGS;

    public static SoundEvent DRAMATIC;
    public static SoundEvent LOOTBAG_SFX_1;
    public static SoundEvent LOOTBAG_SFX_2;

    private MobLootBagsRegistry() {
    }

    public static void registerAll() {
        if (registered) {
            return;
        }
        registered = true;

        LOOT_BAG_RECYCLE_BLOCK = registerBlock("loot_bag_recycle_block", createLootBagRecyclerBlock());
        LOOT_BAG_OPENER_BLOCK = registerBlock("loot_bag_opener_block", createLootBagOpenerBlock());
        LOOT_BAG_RECYCLE_BLOCK_ITEM = registerBlockItem("loot_bag_recycle_block", LOOT_BAG_RECYCLE_BLOCK, "block.mob_loot_bags.loot_bag_recycle_block.description_0");
        LOOT_BAG_OPENER_BLOCK_ITEM = registerBlockItem("loot_bag_opener_block", LOOT_BAG_OPENER_BLOCK, "block.mob_loot_bags.loot_bag_opener_block.description_0");

        COMMONLOOTBAG = registerItem("commonlootbag", () -> createTierLootBagItem("common", new Item.Properties()));
        UNCOMMONLOOTBAG = registerItem("uncommonlootbag", () -> createTierLootBagItem("uncommon", new Item.Properties()));
        RARELOOTBAG = registerItem("rarelootbag", () -> createTierLootBagItem("rare", new Item.Properties()));
        EPICLOOTBAG = registerItem("epiclootbag", () -> createTierLootBagItem("epic", new Item.Properties().rarity(Rarity.RARE)));
        LEGENDARYLOOTBAG = registerItem("legendarylootbag", () -> createTierLootBagItem("legendary", new Item.Properties().rarity(Rarity.EPIC)));
        CURSED_LOOTBAG = registerItem("cursed_lootbag", () -> createCursedLootBagItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
        TIMED_LOOT_BAG = registerItem("timed_loot_bag", () -> createTimedLootBagItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
        DIAMOND_KEY = registerItem("diamond_key", () -> createDiamondKeyItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));
        LOCKED_LOOTBAGS = registerItem("locked_lootbags", () -> createLockedLootBagItem(new Item.Properties().rarity(Rarity.EPIC)));
        SUMMONING_LOOTBAGS = registerItem("summoning_lootbags", () -> createSummoningLootBagItem(new Item.Properties().rarity(Rarity.RARE)));

        DRAMATIC = registerSound("dramatic");
        LOOTBAG_SFX_1 = registerSound("lootbag_sfx_1");
        LOOTBAG_SFX_2 = registerSound("lootbag_sfx_2");
    }

    public static void forEachRegisteredItem(Consumer<Item> consumer) {
        Item[] orderedItems = new Item[] {
                LOOT_BAG_RECYCLE_BLOCK_ITEM,
                LOOT_BAG_OPENER_BLOCK_ITEM,
                COMMONLOOTBAG,
                UNCOMMONLOOTBAG,
                RARELOOTBAG,
                EPICLOOTBAG,
                LEGENDARYLOOTBAG,
                CURSED_LOOTBAG,
                TIMED_LOOT_BAG,
                DIAMOND_KEY,
                LOCKED_LOOTBAGS,
                SUMMONING_LOOTBAGS
        };
        for (Item item : orderedItems) {
            if (item != null) {
                consumer.accept(item);
            }
        }
    }

    public static Block createLootBagRecyclerBlock() {
        return new LootBagRecyclerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0F, 10.0F).sound(SoundType.METAL));
    }

    public static Block createLootBagOpenerBlock() {
        return new LootBagOpenerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0F, 10.0F).sound(SoundType.METAL));
    }

    public static Item createTierLootBagItem(String tier, Item.Properties properties) {
        return new TierLootBagItem(tier, properties);
    }

    public static Item createSummoningLootBagItem(Item.Properties properties) {
        return new SummoningLootBagItem(properties);
    }

    public static Item createLockedLootBagItem(Item.Properties properties) {
        return new LockedLootBagItem(properties);
    }

    public static Item createTimedLootBagItem(Item.Properties properties) {
        return new TimedLootBagItem(properties);
    }

    public static Item createCursedLootBagItem(Item.Properties properties) {
        return new CursedLootBagItem(properties);
    }

    public static Item createDiamondKeyItem(Item.Properties properties) {
        return new Item(properties);
    }

    public static Item createTooltipBlockItem(Block block, Item.Properties properties, String tooltipKey) {
        return new TooltipBlockItem(block, properties, tooltipKey);
    }

    private static Block registerBlock(String id, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath("mob_loot_bags", id), block);
        return block;
    }

    private static Item registerBlockItem(String id, Block block, String tooltipKey) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("mob_loot_bags", id), new TooltipBlockItem(block, new Item.Properties(), tooltipKey));
    }

    private static Item registerItem(String id, Supplier<Item> itemFactory) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("mob_loot_bags", id), itemFactory.get());
    }

    private static SoundEvent registerSound(String id) {
        Identifier rl = Identifier.fromNamespaceAndPath("mob_loot_bags", id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, rl, SoundEvent.createVariableRangeEvent(rl));
    }

    public static final class TooltipBlockItem extends BlockItem {
        private final String tooltipKey;

        private TooltipBlockItem(Block block, Properties properties, String tooltipKey) {
            super(block, properties);
            this.tooltipKey = tooltipKey;
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, context, tooltipDisplay, tooltip, flag);
            tooltip.accept(Component.translatable(tooltipKey));
        }
    }

    public static class TierLootBagItem extends Item {
        private final String tier;

        private TierLootBagItem(String tier, Properties properties) {
            super(properties);
            this.tier = tier;
        }

        @Override
        public InteractionResult use(Level level, Player player, InteractionHand hand) {
            return MobLootBagsLogic.openTierBag(level, player, player.getItemInHand(hand), tier);
        }
    }

    public static final class SummoningLootBagItem extends Item {
        private SummoningLootBagItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult use(Level level, Player player, InteractionHand hand) {
            return MobLootBagsLogic.openSummoningBag(level, player, player.getItemInHand(hand));
        }
    }

    public static final class LockedLootBagItem extends Item {
        private LockedLootBagItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult use(Level level, Player player, InteractionHand hand) {
            return MobLootBagsLogic.openLockedBag(level, player, player.getItemInHand(hand));
        }
    }

    public static final class TimedLootBagItem extends Item {
        private TimedLootBagItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult use(Level level, Player player, InteractionHand hand) {
            return MobLootBagsLogic.openTimedBag(level, player, player.getItemInHand(hand));
        }

        @Override
        public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
            super.inventoryTick(stack, level, entity, slot);
            if (stack.isEnchanted()) {
                return;
            }

            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            var tag = customData.copyTag();

            if (!tag.getBooleanOr("is_active", false)) {
                tag.putBoolean("is_active", true);
                tag.putInt("lb_timer", Mth.nextInt(level.getRandom(), 6000, 12000));
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                return;
            }
            int timer = tag.getIntOr("lb_timer", 0);
            if (timer > 0) {
                tag.putInt("lb_timer", timer - 1);
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            } else {
                level.registryAccess().lookup(Registries.ENCHANTMENT)
                        .flatMap(lookup -> lookup.get(Enchantments.UNBREAKING))
                        .ifPresent(holder -> stack.enchant(holder, 1));
            }
        }
    }

    public static final class CursedLootBagItem extends Item {
        private CursedLootBagItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult use(Level level, Player player, InteractionHand hand) {
            return MobLootBagsLogic.openCursedBag(level, player, player.getItemInHand(hand));
        }
    }
}
