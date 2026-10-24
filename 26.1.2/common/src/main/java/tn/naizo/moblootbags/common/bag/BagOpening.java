package tn.naizo.moblootbags.common.bag;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import tn.naizo.moblootbags.common.client.ClientBridge;
import tn.naizo.moblootbags.common.config.BagsConfig;
import tn.naizo.moblootbags.common.config.LootBagsConfig;
import tn.naizo.moblootbags.common.content.BagType;
import tn.naizo.moblootbags.common.content.ModContent;
import tn.naizo.moblootbags.common.drop.PlayerLootData;
import tn.naizo.moblootbags.common.item.LootBagItem;
import tn.naizo.moblootbags.common.loot.LootService;
import tn.naizo.moblootbags.common.network.Network;

/** Opening bags held by players. */
public final class BagOpening {
    private BagOpening() {
    }

    /** Ticks to hold Use: the server's value once synced, else the local config. */
    public static int holdTicks() {
        Integer synced = ClientBridge.syncedHoldTicks();
        return synced != null ? synced : LootBagsConfig.bags().holdTicks;
    }

    /** Checks run on both sides before the use animation starts: Hourglass bags must be ripe, Locked bags need a key. */
    public static boolean canStartOpening(Level level, Player player, ItemStack stack, BagType type) {
        if (type == BagType.HOURGLASS && !BagData.isRipe(stack, level.getGameTime())) {
            if (player instanceof ServerPlayer serverPlayer) {
                long ripeAt = BagData.ripeAt(stack);
                serverPlayer.sendOverlayMessage(ripeAt < 0
                        ? Component.translatable("message.mob_loot_bags.hourglass_starting").withStyle(ChatFormatting.GRAY)
                        : Component.translatable("message.mob_loot_bags.hourglass_wait",
                        Hourglass.formatRemaining(ripeAt - level.getGameTime())).withColor(type.color()));
            }
            return false;
        }
        if (type == BagType.LOCKED && !player.getAbilities().instabuild && findKey(player) < 0) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendOverlayMessage(Component.translatable("message.mob_loot_bags.needs_key").withColor(type.color()));
            }
            return false;
        }
        return true;
    }

    public static void open(ServerPlayer player, ItemStack stack, InteractionHand hand) {
        BagType type = LootBagItem.typeOf(stack);
        if (type == null || stack.isEmpty()) {
            return;
        }
        ServerLevel level = player.level();
        if (!canStartOpening(level, player, stack, type)) {
            return;
        }
        ItemStack trigger = stack.copyWithCount(1);
        if (type == BagType.CURSED) {
            consume(player, stack);
            Cursed.open(player, level);
            record(player, type, 1, trigger);
            return;
        }

        BagsConfig config = LootBagsConfig.bags();
        int wanted = player.isShiftKeyDown() && config.sneakOpensStack ? Math.min(stack.getCount(), config.maxStackOpen) : 1;
        String theme = BagData.theme(stack);
        List<ItemStack> loot = new ArrayList<>();
        int opened = 0;
        for (int i = 0; i < wanted && !stack.isEmpty(); i++) {
            if (type == BagType.LOCKED && !player.getAbilities().instabuild && !consumeKey(player)) {
                break;
            }
            loot.addAll(LootService.roll(level, player, player.position(), type, theme));
            consume(player, stack);
            opened++;
        }
        if (opened == 0) {
            return;
        }
        if (type == BagType.LOCKED) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), ModContent.SOUND_UNLOCK.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        List<ItemStack> merged = merge(loot);
        deliver(player, merged);
        record(player, type, opened, trigger);
        present(player, level, type, opened, merged);
    }

    /**
     * Puts loot into the inventory; whatever does not fit drops at the player's feet. An empty selected slot is filled
     * last: the player is often still holding Use, which would otherwise use the loot at once (equip armour, throw a
     * pearl or a splash potion).
     */
    public static void deliver(ServerPlayer player, List<ItemStack> loot) {
        boolean toInventory = LootBagsConfig.bags().lootToInventory;
        Inventory inventory = player.getInventory();
        int hand = inventory.getSelectedSlot();
        boolean guardHand = toInventory && inventory.getItem(hand).isEmpty();
        if (guardHand) {
            inventory.setItem(hand, handGuard());
        }
        List<ItemStack> left = new ArrayList<>();
        for (ItemStack drop : loot) {
            ItemStack remaining = drop.copy();
            if (toInventory) {
                inventory.add(remaining);
            }
            if (!remaining.isEmpty()) {
                left.add(remaining);
            }
        }
        if (guardHand) {
            inventory.setItem(hand, ItemStack.EMPTY);
            left.forEach(inventory::add);
        }
        for (ItemStack remaining : left) {
            if (!remaining.isEmpty()) {
                ItemEntity entity = new ItemEntity(player.level(), player.getX(), player.getY() + 0.5, player.getZ(), remaining);
                entity.setNoPickUpDelay();
                entity.setTarget(player.getUUID());
                player.level().addFreshEntity(entity);
            }
        }
        player.inventoryMenu.broadcastChanges();
    }

    /** Holds the selected slot for the length of {@link #deliver}; tagged so no loot can stack onto it. */
    private static ItemStack handGuard() {
        ItemStack guard = new ItemStack(Items.BARRIER);
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("mob_loot_bags_hand_guard", true);
        guard.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return guard;
    }

    public static void record(ServerPlayer player, BagType type, int opened, ItemStack trigger) {
        PlayerLootData.get(player.getUUID()).opened[type.ordinal()] += opened;
        PlayerLootData.markDirty();
        player.awardStat(ModContent.STAT_BAGS_OPENED, opened);
        player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(trigger.getItem()), opened);
        CriteriaTriggers.CONSUME_ITEM.trigger(player, trigger);
    }

    /** Sounds, particles, the reveal toast, the optional chat summary and the title for top-tier bags. */
    public static void present(ServerPlayer player, ServerLevel level, BagType type, int opened, List<ItemStack> loot) {
        BagsConfig config = LootBagsConfig.bags();
        double x = player.getX();
        double y = player.getY() + 1.0;
        double z = player.getZ();
        level.playSound(null, x, y, z, ModContent.SOUND_BAG_OPEN.get(), SoundSource.PLAYERS, 1.0F, 0.9F + level.getRandom().nextFloat() * 0.2F);
        int rank = type.isTier() ? type.rank() : BagType.EPIC.rank();
        var reveal = ModContent.REVEAL_SOUNDS.get(type.isTier() ? type : BagType.EPIC);
        if (reveal != null && rank >= BagType.RARE.rank()) {
            SoundEvent sound = reveal.get();
            level.playSound(null, x, y, z, sound, SoundSource.PLAYERS, 0.9F, 1.0F);
        }
        level.sendParticles(new DustParticleOptions(type.color(), 1.0F), x, y, z, 14 + rank * 6, 0.35, 0.35, 0.35, 0.05);
        if (rank >= BagType.EPIC.rank()) {
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 6 + rank * 3, 0.3, 0.4, 0.3, 0.06);
        }
        if (config.revealToast) {
            Network.reveal(player, type, opened, loot);
        }
        if (config.chatSummary && !loot.isEmpty()) {
            MutableComponent line = Component.translatable("message.mob_loot_bags.summary", opened,
                    Component.translatable(type.translationKey()).withColor(type.color())).withStyle(ChatFormatting.GRAY);
            for (ItemStack stack : loot) {
                line.append(" ").append(stack.getDisplayName()).append(Component.literal("×" + stack.getCount()).withStyle(ChatFormatting.DARK_GRAY));
            }
            player.sendSystemMessage(line);
        }
        if (type.isTier() && type.rank() >= config.titleMinRank) {
            player.connection.send(new ClientboundSetTitlesAnimationPacket(5, 40, 15));
            player.connection.send(new ClientboundSetTitleTextPacket(Component.translatable("title.mob_loot_bags." + type.key()).withColor(type.color())));
            player.connection.send(new ClientboundSetSubtitleTextPacket(Component.translatable("title.mob_loot_bags.subtitle", loot.size())
                    .withStyle(ChatFormatting.GRAY)));
        }
    }

    /** Combines equal stacks so the reveal and summary stay short. */
    public static List<ItemStack> merge(List<ItemStack> loot) {
        List<ItemStack> merged = new ArrayList<>();
        outer:
        for (ItemStack stack : loot) {
            if (stack.isEmpty()) {
                continue;
            }
            for (ItemStack existing : merged) {
                if (ItemStack.isSameItemSameComponents(existing, stack) && existing.getCount() + stack.getCount() <= existing.getMaxStackSize()) {
                    existing.grow(stack.getCount());
                    continue outer;
                }
            }
            merged.add(stack.copy());
        }
        return merged;
    }

    private static void consume(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    /** Inventory slot of the key (off hand first), or -1. */
    private static int findKey(Player player) {
        Item key = keyItem();
        Inventory inventory = player.getInventory();
        if (player.getOffhandItem().is(key)) {
            return Inventory.SLOT_OFFHAND;
        }
        if (!LootBagsConfig.bags().keyAnywhere) {
            return -1;
        }
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).is(key)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean consumeKey(Player player) {
        int slot = findKey(player);
        if (slot < 0) {
            return false;
        }
        player.getInventory().getItem(slot).shrink(1);
        return true;
    }

    private static Item keyItem() {
        Identifier id = Identifier.tryParse(LootBagsConfig.bags().lockedKey);
        return id != null ? BuiltInRegistries.ITEM.getOptional(id).orElse(ModContent.DIAMOND_KEY.get()) : ModContent.DIAMOND_KEY.get();
    }
}
