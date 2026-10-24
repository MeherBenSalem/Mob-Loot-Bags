package tn.naizo.moblootbags.common.content;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.item.LootBagItem;
import tn.naizo.moblootbags.common.item.TooltipItem;
import tn.naizo.moblootbags.common.machine.BagOpenerBlock;
import tn.naizo.moblootbags.common.machine.BagOpenerBlockEntity;
import tn.naizo.moblootbags.common.machine.BagOpenerMenu;
import tn.naizo.moblootbags.common.machine.LootRecyclerBlock;
import tn.naizo.moblootbags.common.machine.LootRecyclerBlockEntity;
import tn.naizo.moblootbags.common.machine.LootRecyclerMenu;
import tn.naizo.moblootbags.common.machine.MachineBlock;
import tn.naizo.moblootbags.common.platform.Registrar;

/** Everything the mod registers. Registry ids of 1.x content are unchanged. */
public final class ModContent {
    public static final Map<BagType, Supplier<LootBagItem>> BAGS = new EnumMap<>(BagType.class);
    public static Supplier<Item> DIAMOND_KEY;
    public static Supplier<Item> FORTUNE_TALISMAN;

    public static Supplier<Block> BAG_OPENER;
    public static Supplier<Block> LOOT_RECYCLER;
    public static Supplier<Item> BAG_OPENER_ITEM;
    public static Supplier<Item> LOOT_RECYCLER_ITEM;
    public static Supplier<BlockEntityType<BagOpenerBlockEntity>> BAG_OPENER_ENTITY;
    public static Supplier<BlockEntityType<LootRecyclerBlockEntity>> LOOT_RECYCLER_ENTITY;
    public static Supplier<MenuType<BagOpenerMenu>> BAG_OPENER_MENU;
    public static Supplier<MenuType<LootRecyclerMenu>> LOOT_RECYCLER_MENU;

    public static Supplier<SoundEvent> SOUND_BAG_OPEN;
    public static Supplier<SoundEvent> SOUND_BAG_RUSTLE;
    public static Supplier<SoundEvent> SOUND_BAG_DROP;
    public static final Map<BagType, Supplier<SoundEvent>> REVEAL_SOUNDS = new EnumMap<>(BagType.class);
    public static Supplier<SoundEvent> SOUND_CURSED;
    public static Supplier<SoundEvent> SOUND_UNLOCK;
    public static Supplier<SoundEvent> SOUND_HOURGLASS;
    public static Supplier<SoundEvent> SOUND_MACHINE;
    public static Supplier<SoundEvent> SOUND_GRIND;
    public static Supplier<SoundEvent> DRAMATIC;
    public static Supplier<SoundEvent> LOOTBAG_SFX_1;
    public static Supplier<SoundEvent> LOOTBAG_SFX_2;

    public static final ResourceLocation STAT_BAGS_OPENED = MobLootBagsCommon.id("bags_opened");
    public static final ResourceLocation STAT_BAGS_FOUND = MobLootBagsCommon.id("bags_found");

    /** Creates menu types; their constructor is not public in vanilla, so each loader supplies this. */
    public interface MenuTypeFactory {
        <T extends AbstractContainerMenu> MenuType<T> create(MenuConstructor<T> constructor);
    }

    /** Creates block entity types; in 1.20.1 their factory interface is not public in vanilla either. */
    public interface BlockEntityTypeFactory {
        <T extends BlockEntity> BlockEntityType<T> create(BiFunction<BlockPos, BlockState, T> constructor, Block block);
    }

    @FunctionalInterface
    public interface MenuConstructor<T extends AbstractContainerMenu> {
        T create(int containerId, Inventory inventory);
    }

    private ModContent() {
    }

    public static void register(Registrar r, MenuTypeFactory menus, BlockEntityTypeFactory blockEntities) {
        for (BagType type : BagType.values()) {
            BAGS.put(type, r.register(Registries.ITEM, type.registryId(), id -> new LootBagItem(type, bagProperties(type))));
        }
        DIAMOND_KEY = r.register(Registries.ITEM, "diamond_key",
                id -> new TooltipItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON), "item.mob_loot_bags.diamond_key.tooltip", false));
        FORTUNE_TALISMAN = r.register(Registries.ITEM, "fortune_talisman",
                id -> new TooltipItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).fireResistant(), "item.mob_loot_bags.fortune_talisman.tooltip", true));

        BAG_OPENER = r.register(Registries.BLOCK, "loot_bag_opener_block", id -> new BagOpenerBlock(machine(SoundType.WOOD, 0)));
        LOOT_RECYCLER = r.register(Registries.BLOCK, "loot_bag_recycle_block", id -> new LootRecyclerBlock(machine(SoundType.METAL, 9)));
        BAG_OPENER_ITEM = r.register(Registries.ITEM, "loot_bag_opener_block",
                id -> new BlockItem(BAG_OPENER.get(), new Item.Properties()));
        LOOT_RECYCLER_ITEM = r.register(Registries.ITEM, "loot_bag_recycle_block",
                id -> new BlockItem(LOOT_RECYCLER.get(), new Item.Properties()));
        BAG_OPENER_ENTITY = r.register(Registries.BLOCK_ENTITY_TYPE, "loot_bag_opener_block",
                id -> blockEntities.create(BagOpenerBlockEntity::new, BAG_OPENER.get()));
        LOOT_RECYCLER_ENTITY = r.register(Registries.BLOCK_ENTITY_TYPE, "loot_bag_recycle_block",
                id -> blockEntities.create(LootRecyclerBlockEntity::new, LOOT_RECYCLER.get()));
        BAG_OPENER_MENU = r.register(Registries.MENU, "bag_opener", id -> menus.create(BagOpenerMenu::new));
        LOOT_RECYCLER_MENU = r.register(Registries.MENU, "loot_recycler", id -> menus.create(LootRecyclerMenu::new));

        SOUND_BAG_OPEN = sound(r, "bag.open");
        SOUND_BAG_RUSTLE = sound(r, "bag.rustle");
        SOUND_BAG_DROP = sound(r, "bag.drop");
        for (BagType tier : new BagType[] {BagType.RARE, BagType.EPIC, BagType.LEGENDARY, BagType.MYTHIC}) {
            REVEAL_SOUNDS.put(tier, sound(r, "bag.reveal." + tier.key()));
        }
        SOUND_CURSED = sound(r, "bag.cursed");
        SOUND_UNLOCK = sound(r, "bag.unlock");
        SOUND_HOURGLASS = sound(r, "bag.hourglass_ripe");
        SOUND_MACHINE = sound(r, "machine.opener");
        SOUND_GRIND = sound(r, "machine.recycler");
        DRAMATIC = sound(r, "dramatic");
        LOOTBAG_SFX_1 = sound(r, "lootbag_sfx_1");
        LOOTBAG_SFX_2 = sound(r, "lootbag_sfx_2");

        // Custom stats are looked up by identity, so the registered value must be the constant itself.
        r.register(Registries.CUSTOM_STAT, STAT_BAGS_OPENED.getPath(), id -> STAT_BAGS_OPENED);
        r.register(Registries.CUSTOM_STAT, STAT_BAGS_FOUND.getPath(), id -> STAT_BAGS_FOUND);

    }

    /** The creative tab's items, in order. Each loader builds the tab itself (its builder API differs). */
    public static void forEachTabItem(java.util.function.Consumer<ItemStack> output) {
        for (BagType type : BagType.values()) {
            output.accept(new ItemStack(bag(type)));
        }
        output.accept(new ItemStack(DIAMOND_KEY.get()));
        output.accept(new ItemStack(FORTUNE_TALISMAN.get()));
        output.accept(new ItemStack(BAG_OPENER_ITEM.get()));
        output.accept(new ItemStack(LOOT_RECYCLER_ITEM.get()));
    }

    public static ItemStack tabIcon() {
        return new ItemStack(bag(BagType.LEGENDARY));
    }

    public static Component tabTitle() {
        return Component.translatable("itemGroup.mob_loot_bags.loot_bags_creative_tab");
    }

    public static LootBagItem bag(BagType type) {
        return BAGS.get(type).get();
    }

    private static Item.Properties bagProperties(BagType type) {
        Rarity rarity = switch (type) {
            case COMMON -> Rarity.COMMON;
            case UNCOMMON, CURSED -> Rarity.UNCOMMON;
            case RARE, HOURGLASS, SUMMONING -> Rarity.RARE;
            default -> Rarity.EPIC;
        };
        return new Item.Properties().rarity(rarity).fireResistant();
    }

    private static BlockBehaviour.Properties machine(SoundType sound, int activeLight) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.0F, 6.0F)
                .sound(sound)
                .lightLevel(state -> state.getValue(MachineBlock.ACTIVE) ? activeLight : 0);
    }

    private static Supplier<SoundEvent> sound(Registrar r, String path) {
        return r.register(Registries.SOUND_EVENT, path, SoundEvent::createVariableRangeEvent);
    }
}
