package tn.naizo.moblootbags.common.loot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.config.LootBagsConfig;
import tn.naizo.moblootbags.common.content.BagType;

/** Picks and rolls bag loot tables, and samples them for the Shift-tooltip previews. */
public final class LootService {
    /** Preview key ("rare", "rare/the_nether") to sampled entries, rebuilt on server start and every reload. */
    private static Map<String, List<PreviewEntry>> previews = Map.of();

    private LootService() {
    }

    public static Identifier defaultTable(BagType type) {
        return MobLootBagsCommon.id("bags/" + type.key());
    }

    public static Identifier themedTable(BagType type, String theme) {
        return MobLootBagsCommon.id("bags/" + type.key() + "/" + theme);
    }

    public static boolean exists(MinecraftServer server, Identifier table) {
        return table(server, table) != LootTable.EMPTY;
    }

    private static LootTable table(MinecraftServer server, Identifier id) {
        return server.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
    }

    /** The theme for a bag dropped in {@code level}: the dimension's path, if a themed table exists for it. */
    public static @Nullable String themeFor(ServerLevel level, BagType type) {
        if (!LootBagsConfig.drops().themes) {
            return null;
        }
        String theme = level.dimension().identifier().getPath();
        return exists(level.getServer(), themedTable(type, theme)) ? theme : null;
    }

    /** Overrides from bags.json win, then the themed table, then the default table. */
    public static Identifier resolve(MinecraftServer server, BagType type, @Nullable String theme, RandomSource random) {
        List<String> overrides = LootBagsConfig.bags().tableOverrides.get(type);
        if (overrides != null && !overrides.isEmpty()) {
            Identifier picked = Identifier.tryParse(overrides.get(random.nextInt(overrides.size())));
            if (picked != null) {
                return picked;
            }
        }
        if (theme != null) {
            Identifier themed = themedTable(type, theme);
            if (exists(server, themed)) {
                return themed;
            }
        }
        return defaultTable(type);
    }

    public static List<ItemStack> roll(ServerLevel level, @Nullable Player player, Vec3 origin, BagType type, @Nullable String theme) {
        Identifier id = resolve(level.getServer(), type, theme, level.getRandom());
        LootTable table = table(level.getServer(), id);
        if (table == LootTable.EMPTY) {
            MobLootBagsCommon.LOG.warn("Loot table {} for {} bags is missing or empty", id, type.key());
            return List.of();
        }
        LootParams.Builder params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, origin);
        if (player != null) {
            params.withParameter(LootContextParams.THIS_ENTITY, player).withLuck(player.getLuck());
        }
        return table.getRandomItems(params.create(LootContextParamSets.CHEST));
    }

    public static Map<String, List<PreviewEntry>> previews() {
        return previews;
    }

    public static String previewKey(BagType type, @Nullable String theme) {
        return theme == null ? type.key() : type.key() + "/" + theme;
    }

    /** Samples every bag table (and every themed table that exists) to estimate what each bag can give. */
    public static void rebuildPreviews(MinecraftServer server) {
        if (!LootBagsConfig.bags().previewEnabled) {
            previews = Map.of();
            return;
        }
        int samples = LootBagsConfig.bags().previewSamples;
        ServerLevel level = server.overworld();
        RandomSource random = RandomSource.create(0x4D4C42L);
        Map<String, List<PreviewEntry>> result = new LinkedHashMap<>();
        List<String> themes = new ArrayList<>();
        for (ServerLevel dimension : server.getAllLevels()) {
            themes.add(dimension.dimension().identifier().getPath());
        }
        for (BagType type : BagType.values()) {
            if (type == BagType.CURSED) {
                continue;
            }
            List<String> overrides = LootBagsConfig.bags().tableOverrides.get(type);
            result.put(previewKey(type, null), sample(server, level, overrides.isEmpty() ? List.of(defaultTable(type).toString()) : overrides, samples, random));
            if (overrides.isEmpty()) {
                for (String theme : themes) {
                    Identifier themed = themedTable(type, theme);
                    if (exists(server, themed)) {
                        result.put(previewKey(type, theme), sample(server, level, List.of(themed.toString()), samples, random));
                    }
                }
            }
        }
        previews = result;
    }

    private static List<PreviewEntry> sample(MinecraftServer server, ServerLevel level, List<String> tables, int samples, RandomSource random) {
        Map<String, int[]> stats = new HashMap<>(); // [bags containing it, min count, max count]
        Map<String, ItemStack> icons = new HashMap<>();
        for (int i = 0; i < samples; i++) {
            Identifier id = Identifier.tryParse(tables.get(i % tables.size()));
            if (id == null) {
                continue;
            }
            LootTable table = table(server, id);
            LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.ZERO)
                    .create(LootContextParamSets.CHEST);
            Map<String, Integer> perBag = new HashMap<>();
            for (ItemStack stack : table.getRandomItems(params, random)) {
                String key = statKey(stack);
                perBag.merge(key, stack.getCount(), Integer::sum);
                icons.putIfAbsent(key, stack.copyWithCount(1));
            }
            perBag.forEach((key, count) -> {
                int[] s = stats.computeIfAbsent(key, k -> new int[] {0, Integer.MAX_VALUE, 0});
                s[0]++;
                s[1] = Math.min(s[1], count);
                s[2] = Math.max(s[2], count);
            });
        }
        List<PreviewEntry> entries = new ArrayList<>();
        stats.forEach((key, s) -> entries.add(new PreviewEntry(icons.get(key), s[0] / (float) samples, s[1], s[2])));
        entries.sort(Comparator.comparingDouble((PreviewEntry e) -> -e.chance()).thenComparing(e -> statKey(e.icon())));
        return entries.size() > 24 ? List.copyOf(entries.subList(0, 24)) : List.copyOf(entries);
    }

    /**
     * How rolled stacks are grouped for previews and /lootbags simulate: by item, and by potion as well, so a Potion of
     * Healing and a Potion of Swiftness are listed apart rather than as one nameless potion.
     */
    public static String statKey(ItemStack stack) {
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        PotionContents potion = stack.get(DataComponents.POTION_CONTENTS);
        return potion == null ? id : potion.potion().flatMap(holder -> holder.unwrapKey())
                .map(key -> id + "/" + key.identifier()).orElse(id);
    }

    /** One item a bag can give: the chance a bag contains it, and the count range seen while sampling. */
    public record PreviewEntry(ItemStack icon, float chance, int min, int max) {
    }
}
