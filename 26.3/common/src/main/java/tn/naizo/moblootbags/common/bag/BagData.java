package tn.naizo.moblootbags.common.bag;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jspecify.annotations.Nullable;

/**
 * Per-stack bag data, stored in {@code minecraft:custom_data} under {@code mob_loot_bags}:
 * <ul>
 *     <li>{@code theme}: the dimension path whose themed loot table this bag uses (e.g. {@code the_nether}).</li>
 *     <li>{@code ripe_at}: game time at which an Hourglass bag ripens.</li>
 * </ul>
 */
public final class BagData {
    private static final String ROOT = "mob_loot_bags";
    private static final String THEME = "theme";
    private static final String RIPE_AT = "ripe_at";
    private static final String RIPEN_TICKS = "ripen_ticks";
    private static final String NOTIFIED = "notified";

    private BagData() {
    }

    private static CompoundTag root(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag().getCompoundOrEmpty(ROOT);
    }

    private static void edit(ItemStack stack, java.util.function.Consumer<CompoundTag> editor) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            CompoundTag root = tag.getCompoundOrEmpty(ROOT);
            editor.accept(root);
            if (root.isEmpty()) {
                tag.remove(ROOT);
            } else {
                tag.put(ROOT, root);
            }
        });
    }

    public static @Nullable String theme(ItemStack stack) {
        String theme = root(stack).getStringOr(THEME, "");
        return theme.isEmpty() ? null : theme;
    }

    public static void setTheme(ItemStack stack, @Nullable String theme) {
        edit(stack, root -> {
            if (theme == null || theme.isEmpty()) {
                root.remove(THEME);
            } else {
                root.putString(THEME, theme);
            }
        });
    }

    /** Game time the bag ripens at, or -1 if its timer has not started yet. */
    public static long ripeAt(ItemStack stack) {
        return root(stack).getLongOr(RIPE_AT, -1L);
    }

    public static void setRipeAt(ItemStack stack, long gameTime) {
        edit(stack, root -> root.putLong(RIPE_AT, gameTime));
    }

    /** Starts an Hourglass timer: ripe {@code ticks} from {@code now}. */
    public static void startRipening(ItemStack stack, long now, long ticks) {
        edit(stack, root -> {
            root.putLong(RIPE_AT, now + ticks);
            root.putLong(RIPEN_TICKS, Math.max(1L, ticks));
        });
    }

    public static long ripenTicks(ItemStack stack) {
        return root(stack).getLongOr(RIPEN_TICKS, 0L);
    }

    public static boolean notified(ItemStack stack) {
        return root(stack).getBooleanOr(NOTIFIED, false);
    }

    public static void setNotified(ItemStack stack) {
        edit(stack, root -> root.putBoolean(NOTIFIED, true));
    }

    public static boolean isRipe(ItemStack stack, long now) {
        long ripeAt = ripeAt(stack);
        return ripeAt >= 0 && now >= ripeAt;
    }

    /**
     * 1.x Timed bags kept a tick countdown in {@code lb_timer} and marked ripe bags with Unbreaking. Converts them once.
     * Returns true if the stack was changed.
     */
    public static boolean migrateLegacyTimer(ItemStack stack, long now) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return false;
        }
        CompoundTag tag = data.copyTag();
        if (!tag.contains("is_active") && !tag.contains("lb_timer")) {
            return false;
        }
        boolean ripe = stack.isEnchanted();
        long remaining = Math.max(0, tag.getIntOr("lb_timer", 0));
        tag.remove("is_active");
        tag.remove("lb_timer");
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        if (ripe) {
            stack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        }
        setRipeAt(stack, ripe ? now : now + remaining);
        return true;
    }
}
