package tn.naizo.moblootbags.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import tn.naizo.moblootbags.common.bag.BagData;
import tn.naizo.moblootbags.common.config.DropsConfig;
import tn.naizo.moblootbags.common.config.LootBagsConfig;
import tn.naizo.moblootbags.common.content.BagType;
import tn.naizo.moblootbags.common.content.ModContent;
import tn.naizo.moblootbags.common.content.ModTags;
import tn.naizo.moblootbags.common.drop.DropEngine;
import tn.naizo.moblootbags.common.drop.PlayerLootData;
import tn.naizo.moblootbags.common.drop.TierRoller;
import tn.naizo.moblootbags.common.loot.LootService;
import tn.naizo.moblootbags.common.network.Network;

/** {@code /lootbags} (alias {@code /mlb}): give, simulate, odds, stats and reload. */
public final class LootBagsCommand {
    private static final DynamicCommandExceptionType UNKNOWN_BAG =
            new DynamicCommandExceptionType(key -> Component.translatable("commands.mob_loot_bags.unknown_bag", key));
    private static final SuggestionProvider<CommandSourceStack> BAGS = (context, builder) -> SharedSuggestionProvider.suggest(
            java.util.Arrays.stream(BagType.values()).map(BagType::key), builder);

    private LootBagsCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("lootbags")
                .then(Commands.literal("give").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("bag", StringArgumentType.word()).suggests(BAGS)
                                        .executes(c -> give(c, 1, null))
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 6400))
                                                .executes(c -> give(c, IntegerArgumentType.getInteger(c, "count"), null))
                                                .then(Commands.argument("theme", StringArgumentType.word())
                                                        .suggests((c, b) -> SharedSuggestionProvider.suggest(List.of("the_nether", "the_end"), b))
                                                        .executes(c -> give(c, IntegerArgumentType.getInteger(c, "count"),
                                                                StringArgumentType.getString(c, "theme"))))))))
                .then(Commands.literal("simulate").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("bag", StringArgumentType.word()).suggests(BAGS)
                                .executes(c -> simulate(c, 100, null))
                                .then(Commands.argument("bags", IntegerArgumentType.integer(1, 10000))
                                        .executes(c -> simulate(c, IntegerArgumentType.getInteger(c, "bags"), null))
                                        .then(Commands.argument("theme", StringArgumentType.word())
                                                .executes(c -> simulate(c, IntegerArgumentType.getInteger(c, "bags"),
                                                        StringArgumentType.getString(c, "theme")))))))
                .then(Commands.literal("odds")
                        .executes(c -> odds(c, EntityTypes.ZOMBIE))
                        .then(Commands.argument("mob", ResourceArgument.resource(context, Registries.ENTITY_TYPE))
                                .executes(c -> odds(c, ResourceArgument.getSummonableEntityType(c, "mob").value()))))
                .then(Commands.literal("stats")
                        .executes(c -> stats(c, c.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", EntityArgument.player()).requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .executes(c -> stats(c, EntityArgument.getPlayer(c, "player")))))
                .then(Commands.literal("reload").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(LootBagsCommand::reload));
        dispatcher.register(root);
        dispatcher.register(Commands.literal("mlb").redirect(dispatcher.getRoot().getChild("lootbags")));
    }

    private static BagType bag(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        String key = StringArgumentType.getString(c, "bag");
        BagType type = BagType.byKey(key);
        if (type == null) {
            throw UNKNOWN_BAG.create(key);
        }
        return type;
    }

    private static int give(CommandContext<CommandSourceStack> c, int count, String theme) throws CommandSyntaxException {
        BagType type = bag(c);
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(c, "targets");
        for (ServerPlayer player : targets) {
            int left = count;
            while (left > 0) {
                ItemStack stack = new ItemStack(ModContent.bag(type), Math.min(left, 64));
                if (theme != null) {
                    BagData.setTheme(stack, theme.toLowerCase(Locale.ROOT));
                }
                left -= stack.getCount();
                if (!player.getInventory().add(stack) || !stack.isEmpty()) {
                    player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack));
                }
            }
        }
        c.getSource().sendSuccess(() -> Component.translatable("commands.mob_loot_bags.give", count,
                Component.translatable(type.translationKey()).withColor(type.color()), targets.size()), true);
        return targets.size();
    }

    private static int simulate(CommandContext<CommandSourceStack> c, int bags, String theme) throws CommandSyntaxException {
        BagType type = bag(c);
        if (type == BagType.CURSED) {
            c.getSource().sendFailure(Component.translatable("commands.mob_loot_bags.simulate.cursed"));
            return 0;
        }
        ServerLevel level = c.getSource().getLevel();
        Map<String, Long> totals = new HashMap<>();
        Map<String, Integer> bagsWith = new HashMap<>();
        Map<String, ItemStack> samples = new HashMap<>();
        long items = 0;
        for (int i = 0; i < bags; i++) {
            Map<String, Integer> seen = new HashMap<>();
            for (ItemStack stack : LootService.roll(level, null, c.getSource().getPosition(), type, theme)) {
                String key = LootService.statKey(stack);
                totals.merge(key, (long) stack.getCount(), Long::sum);
                seen.merge(key, 1, Integer::sum);
                samples.putIfAbsent(key, stack.copyWithCount(1));
                items += stack.getCount();
            }
            seen.keySet().forEach(key -> bagsWith.merge(key, 1, Integer::sum));
        }
        List<Map.Entry<String, Long>> sorted = new ArrayList<>(totals.entrySet());
        sorted.sort((a, b) -> Integer.compare(bagsWith.get(b.getKey()), bagsWith.get(a.getKey())));
        String table = LootService.resolve(level.getServer(), type, theme, level.getRandom()).toString();
        long totalItems = items;
        c.getSource().sendSuccess(() -> Component.translatable("commands.mob_loot_bags.simulate.header", bags,
                Component.translatable(type.translationKey()).withColor(type.color()), table,
                String.format(Locale.ROOT, "%.1f", totalItems / (double) bags)).withStyle(ChatFormatting.GOLD), false);
        for (int i = 0; i < Math.min(15, sorted.size()); i++) {
            Map.Entry<String, Long> entry = sorted.get(i);
            double percent = 100.0 * bagsWith.get(entry.getKey()) / bags;
            double average = entry.getValue() / (double) bagsWith.get(entry.getKey());
            MutableComponent line = Component.literal(String.format(Locale.ROOT, " %5.1f%%  ", percent)).withStyle(ChatFormatting.YELLOW)
                    .append(samples.get(entry.getKey()).getDisplayName())
                    .append(Component.literal(String.format(Locale.ROOT, "  avg ×%.1f", average)).withStyle(ChatFormatting.GRAY));
            c.getSource().sendSuccess(() -> line, false);
        }
        if (sorted.size() > 15) {
            c.getSource().sendSuccess(() -> Component.translatable("commands.mob_loot_bags.simulate.more", sorted.size() - 15)
                    .withStyle(ChatFormatting.DARK_GRAY), false);
        }
        return sorted.size();
    }

    private static int odds(CommandContext<CommandSourceStack> c, EntityType<?> type) {
        ServerLevel level = c.getSource().getLevel();
        ServerPlayer player = c.getSource().getPlayer();
        Entity entity = type.create(level, EntitySpawnReason.COMMAND);
        if (!(entity instanceof LivingEntity living)) {
            c.getSource().sendFailure(Component.translatable("commands.mob_loot_bags.odds.not_living"));
            return 0;
        }
        DropsConfig config = LootBagsConfig.drops();
        boolean boss = ModTags.is(type, ModTags.BOSSES);
        boolean blocked = ModTags.is(type, ModTags.NO_BAGS) || !config.enabled;
        double chance = blocked ? 0.0 : boss ? 1.0 : DropEngine.chance(living, player, config);
        double luck = DropEngine.luck(player, living, level, config);
        int minRank = boss ? config.bossMinRank : 0;
        double[] dist = TierRoller.distribution(config.tierWeights, luck, config.luckPerPoint, minRank);
        entity.discard();

        c.getSource().sendSuccess(() -> Component.translatable("commands.mob_loot_bags.odds.header", type.getDescription(),
                String.format(Locale.ROOT, "%.1f%%", chance * 100.0), String.format(Locale.ROOT, "%.2f", luck)).withStyle(ChatFormatting.GOLD), false);
        if (boss) {
            c.getSource().sendSuccess(() -> Component.translatable("commands.mob_loot_bags.odds.boss", config.bossBags,
                    String.format(Locale.ROOT, "%.0f%%", config.bossMythicChance * 100.0)).withStyle(ChatFormatting.LIGHT_PURPLE), false);
        }
        MutableComponent tiers = Component.empty();
        for (BagType tier : BagType.TIERS) {
            if (dist[tier.rank()] <= 0) {
                continue;
            }
            tiers.append(Component.translatable(tier.translationKey()).withColor(tier.color()))
                    .append(Component.literal(String.format(Locale.ROOT, " %.2f%%  ", dist[tier.rank()] * 100.0)).withStyle(ChatFormatting.GRAY));
        }
        c.getSource().sendSuccess(() -> tiers, false);
        return (int) Math.round(chance * 100.0);
    }

    private static int stats(CommandContext<CommandSourceStack> c, ServerPlayer player) {
        PlayerLootData.Entry entry = PlayerLootData.get(player.getUUID());
        DropsConfig config = LootBagsConfig.drops();
        c.getSource().sendSuccess(() -> Component.translatable("commands.mob_loot_bags.stats.header", player.getDisplayName(),
                entry.totalFound(), entry.totalOpened()).withStyle(ChatFormatting.GOLD), false);
        MutableComponent line = Component.empty();
        for (BagType type : BagType.values()) {
            int found = entry.found[type.ordinal()];
            int opened = entry.opened[type.ordinal()];
            if (found == 0 && opened == 0) {
                continue;
            }
            line.append(Component.translatable(type.translationKey()).withColor(type.color()))
                    .append(Component.literal(" " + found + "/" + opened + "  ").withStyle(ChatFormatting.GRAY));
        }
        c.getSource().sendSuccess(() -> line, false);
        if (config.pityEnabled) {
            c.getSource().sendSuccess(() -> Component.translatable("commands.mob_loot_bags.stats.pity",
                    entry.killsSinceBag, config.pityKills, entry.bagsSinceRare, config.pityRare,
                    entry.bagsSinceLegendary, config.pityLegendary).withStyle(ChatFormatting.DARK_AQUA), false);
        }
        return entry.totalFound();
    }

    private static int reload(CommandContext<CommandSourceStack> c) {
        LootBagsConfig.load(tn.naizo.moblootbags.common.MobLootBagsCommon.platform().configDir());
        LootService.rebuildPreviews(c.getSource().getServer());
        Network.syncAll(c.getSource().getServer());
        c.getSource().sendSuccess(() -> Component.translatable("commands.mob_loot_bags.reload"), true);
        return 1;
    }
}
