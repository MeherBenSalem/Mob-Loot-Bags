package tn.naizo.moblootbags.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class MobLootBagsLogic {
    private MobLootBagsLogic() {
    }

    public static InteractionResultHolder<ItemStack> openTierBag(Level level, Player player, ItemStack stack, String tier) {
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        String table = MobLootBagsConfig.pickTierLootTable(tier, level.getRandom());
        openLootTableAtPlayer((ServerLevel) level, player, stack, table);
        return InteractionResultHolder.consume(stack);
    }

    public static InteractionResultHolder<ItemStack> openLockedBag(Level level, Player player, ItemStack stack) {
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (!consumeKey(player)) {
            player.displayClientMessage(Component.literal("You need a key in your offhand to open this bag"), true);
            return InteractionResultHolder.fail(stack);
        }
        openLootTableAtPlayer((ServerLevel) level, player, stack, MobLootBagsConfig.lockedLootTable());
        return InteractionResultHolder.consume(stack);
    }

    public static InteractionResultHolder<ItemStack> openSummoningBag(Level level, Player player, ItemStack stack) {
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        openLootTableAtPlayer((ServerLevel) level, player, stack, MobLootBagsConfig.summoningLootTable());
        return InteractionResultHolder.consume(stack);
    }

    public static InteractionResultHolder<ItemStack> openTimedBag(Level level, Player player, ItemStack stack) {
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (!stack.isEnchanted()) {
            CompoundTag tag = stack.getOrCreateTag();
            int ticks = Math.max(0, tag.getInt("lb_timer"));
            int seconds = Math.max(1, ticks / 20);
            player.displayClientMessage(Component.literal("This lootbag is not ready yet. Wait " + seconds + "s."), true);
            return InteractionResultHolder.fail(stack);
        }
        openLootTableAtPlayer((ServerLevel) level, player, stack, MobLootBagsConfig.timedLootTable());
        return InteractionResultHolder.consume(stack);
    }

    public static InteractionResultHolder<ItemStack> openCursedBag(Level level, Player player, ItemStack stack) {
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        executeRandomEvent((ServerLevel) level, player, true);
        consumeBag(player, stack);
        return InteractionResultHolder.consume(stack);
    }

    public static void onMobDeath(LivingEntity victim, DamageSource source) {
        if (!(victim.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ServerPlayer player = getAttackingPlayer(source);
        if (player == null) {
            return;
        }

        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType()).toString();
        if (MobLootBagsConfig.whitelistEnabled() && !MobLootBagsConfig.whitelistContains(mobId)) {
            return;
        }
        if (MobLootBagsConfig.blacklistEnabled() && MobLootBagsConfig.blacklistContains(mobId)) {
            return;
        }

        RandomSource random = serverLevel.getRandom();
        if (random.nextInt(100) + 1 > MobLootBagsConfig.dropChanceOverall()) {
            return;
        }

        Item item = rollDrop(random);
        if (item != null) {
            serverLevel.addFreshEntity(new ItemEntity(serverLevel, victim.getX(), victim.getY(), victim.getZ(), new ItemStack(item)));
        }

        if (MobLootBagsConfig.cursedEnabled()
                && MobLootBagsRegistry.CURSED_LOOTBAG != null
                && random.nextInt(100) + 1 <= MobLootBagsConfig.cursedDropRate()) {
            serverLevel.addFreshEntity(new ItemEntity(serverLevel, victim.getX(), victim.getY(), victim.getZ(), new ItemStack(MobLootBagsRegistry.CURSED_LOOTBAG)));
        }
    }

    private static ServerPlayer getAttackingPlayer(DamageSource source) {
        Entity entity = source.getEntity();
        if (entity instanceof ServerPlayer player) {
            return player;
        }
        if (entity instanceof Projectile projectile) {
            Entity owner = projectile.getOwner();
            if (owner instanceof ServerPlayer player) {
                return player;
            }
        }
        Entity direct = source.getDirectEntity();
        if (direct instanceof ServerPlayer player) {
            return player;
        }
        if (direct instanceof Projectile projectile) {
            Entity owner = projectile.getOwner();
            if (owner instanceof ServerPlayer player) {
                return player;
            }
        }
        return null;
    }

    public static boolean isOpenerSupportedBag(ItemStack stack) {
        Item item = stack.getItem();
        return item == MobLootBagsRegistry.COMMONLOOTBAG
                || item == MobLootBagsRegistry.UNCOMMONLOOTBAG
                || item == MobLootBagsRegistry.RARELOOTBAG
                || item == MobLootBagsRegistry.EPICLOOTBAG
                || item == MobLootBagsRegistry.LEGENDARYLOOTBAG
                || item == MobLootBagsRegistry.SUMMONING_LOOTBAGS;
    }

    public static int getRecycleXpValue(ItemStack stack) {
        Item item = stack.getItem();
        if (item == MobLootBagsRegistry.COMMONLOOTBAG) {
            return recyclerValue("common_lb", 10);
        }
        if (item == MobLootBagsRegistry.UNCOMMONLOOTBAG) {
            return recyclerValue("uncommon_lb", 20);
        }
        if (item == MobLootBagsRegistry.RARELOOTBAG) {
            return recyclerValue("rare_lb", 40);
        }
        if (item == MobLootBagsRegistry.EPICLOOTBAG) {
            return recyclerValue("epic_lb", 80);
        }
        if (item == MobLootBagsRegistry.LEGENDARYLOOTBAG) {
            return recyclerValue("legendary_lb", 200);
        }
        if (item == MobLootBagsRegistry.CURSED_LOOTBAG) {
            return recyclerValue("cursed_lb", 60);
        }
        if (item == MobLootBagsRegistry.TIMED_LOOT_BAG) {
            return stack.isEnchanted() ? recyclerValue("enchanted_timed_lb", 200) : recyclerValue("timed_lb", 40);
        }
        if (item == MobLootBagsRegistry.LOCKED_LOOTBAGS) {
            return recyclerValue("locked_lb", 40);
        }
        if (item == MobLootBagsRegistry.SUMMONING_LOOTBAGS) {
            return recyclerValue("summoning_lb", 40);
        }
        return 0;
    }

    public static boolean processNextBag(ServerLevel level, BlockPos openerPos, Container openerContainer) {
        for (int slot = 0; slot < openerContainer.getContainerSize(); slot++) {
            ItemStack stack = openerContainer.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }

            String table = tableForOpenerBag(stack.getItem(), level.getRandom());
            if (table == null) {
                continue;
            }

            List<ItemStack> drops = lootDrops(level, Vec3.atCenterOf(openerPos), table);
            Container adjacent = adjacentLootContainer(level, openerPos);
            for (ItemStack drop : drops) {
                ItemStack remaining = drop.copy();
                if (adjacent != null) {
                    remaining = insertIntoContainer(adjacent, remaining);
                }
                if (!remaining.isEmpty()) {
                    level.addFreshEntity(new ItemEntity(level, openerPos.getX() + 0.5D, openerPos.getY() + 1.0D, openerPos.getZ() + 0.5D, remaining));
                }
            }

            openerContainer.removeItem(slot, 1);
            openerContainer.setChanged();
            return true;
        }
        return false;
    }

    private static Item rollDrop(RandomSource random) {
        int roll = random.nextInt(100) + 1;
        if (roll <= MobLootBagsConfig.legendaryDropRate()) {
            return MobLootBagsRegistry.LEGENDARYLOOTBAG;
        }
        if (roll <= MobLootBagsConfig.epicDropRate()) {
            return MobLootBagsRegistry.EPICLOOTBAG;
        }
        if (roll <= MobLootBagsConfig.rareDropRate()) {
            if (random.nextInt(100) + 1 <= MobLootBagsConfig.timedDropRate()) {
                return MobLootBagsRegistry.TIMED_LOOT_BAG;
            }
            return MobLootBagsRegistry.RARELOOTBAG;
        }
        if (roll <= MobLootBagsConfig.uncommonDropRate()) {
            return MobLootBagsRegistry.UNCOMMONLOOTBAG;
        }
        return MobLootBagsRegistry.COMMONLOOTBAG;
    }

    private static void openLootTableAtPlayer(ServerLevel level, Player player, ItemStack stack, String tableName) {
        for (ItemStack drop : lootDrops(level, player.position(), tableName)) {
            level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), drop.copy()));
        }
        consumeBag(player, stack);
    }

    private static String tableForOpenerBag(Item item, RandomSource random) {
        if (item == MobLootBagsRegistry.COMMONLOOTBAG) {
            return MobLootBagsConfig.pickTierLootTable("common", random);
        }
        if (item == MobLootBagsRegistry.UNCOMMONLOOTBAG) {
            return MobLootBagsConfig.pickTierLootTable("uncommon", random);
        }
        if (item == MobLootBagsRegistry.RARELOOTBAG) {
            return MobLootBagsConfig.pickTierLootTable("rare", random);
        }
        if (item == MobLootBagsRegistry.EPICLOOTBAG) {
            return MobLootBagsConfig.pickTierLootTable("epic", random);
        }
        if (item == MobLootBagsRegistry.LEGENDARYLOOTBAG) {
            return MobLootBagsConfig.pickTierLootTable("legendary", random);
        }
        if (item == MobLootBagsRegistry.SUMMONING_LOOTBAGS) {
            return MobLootBagsConfig.summoningLootTable();
        }
        return null;
    }

    private static List<ItemStack> lootDrops(ServerLevel level, Vec3 origin, String tableName) {
        ResourceLocation tableId = ResourceLocation.tryParse(tableName);
        if (tableId == null) {
            tableId = new ResourceLocation("minecraft", "chests/end_city_treasure");
        }

        LootTable table = level.getServer().getLootData().getLootTable(tableId);
        LootParams lootParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, origin)
                .create(LootContextParamSets.CHEST);
        return table.getRandomItems(lootParams);
    }

    private static Container adjacentLootContainer(Level level, BlockPos pos) {
        Direction[] order = new Direction[] {
                Direction.UP,
                Direction.DOWN,
                Direction.EAST,
                Direction.WEST,
                Direction.SOUTH,
                Direction.NORTH
        };

        for (Direction direction : order) {
            BlockPos targetPos = pos.relative(direction);
            if (!(level.getBlockState(targetPos).is(Blocks.CHEST)
                    || level.getBlockState(targetPos).is(Blocks.TRAPPED_CHEST)
                    || level.getBlockState(targetPos).is(Blocks.HOPPER))) {
                continue;
            }
            BlockEntity blockEntity = level.getBlockEntity(targetPos);
            if (blockEntity instanceof Container container) {
                return container;
            }
        }
        return null;
    }

    private static ItemStack insertIntoContainer(Container container, ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack existing = container.getItem(i);
            if (existing.isEmpty()) {
                if (container.canPlaceItem(i, stack)) {
                    container.setItem(i, stack.copy());
                    container.setChanged();
                    return ItemStack.EMPTY;
                }
                continue;
            }

            if (!ItemStack.isSameItemSameTags(existing, stack)) {
                continue;
            }

            int limit = Math.min(container.getMaxStackSize(), existing.getMaxStackSize());
            int room = limit - existing.getCount();
            if (room <= 0) {
                continue;
            }

            int move = Math.min(room, stack.getCount());
            existing.grow(move);
            stack.shrink(move);
            container.setItem(i, existing);
            container.setChanged();
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    private static int recyclerValue(String key, int fallback) {
        JsonObject recyclerValues = MobLootBagsConfig.recyclerValues();
        if (recyclerValues == null) {
            return fallback;
        }
        JsonElement value = recyclerValues.get(key);
        if (value != null && value.isJsonPrimitive()) {
            return value.getAsInt();
        }
        return fallback;
    }

    private static void executeRandomEvent(ServerLevel level, Player player, boolean playCustomSound) {
        List<String> events = MobLootBagsConfig.cursedEvents();
        if (events.isEmpty()) {
            return;
        }
        String command = events.get(level.getRandom().nextInt(events.size()));
        CommandSourceStack source = new CommandSourceStack(
                CommandSource.NULL,
                player.position(),
                Vec2.ZERO,
                level,
                4,
                player.getName().getString(),
                player.getDisplayName(),
                level.getServer(),
                player
        );
        level.getServer().getCommands().performPrefixedCommand(source, command);
        if (playCustomSound && MobLootBagsConfig.cursedSoundEnabled()) {
            SoundEvent sound = switch (level.getRandom().nextInt(3)) {
                case 1 -> MobLootBagsRegistry.LOOTBAG_SFX_1;
                case 2 -> MobLootBagsRegistry.LOOTBAG_SFX_2;
                default -> MobLootBagsRegistry.DRAMATIC;
            };
            if (sound != null) {
                level.playSound(null, BlockPos.containing(player.position()), sound, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    private static void consumeBag(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    private static boolean consumeKey(Player player) {
        if (player.getAbilities().instabuild) {
            return true;
        }
        ItemStack offhand = player.getOffhandItem();
        if (offhand.is(MobLootBagsRegistry.DIAMOND_KEY)) {
            offhand.shrink(1);
            return true;
        }
        return false;
    }
}
