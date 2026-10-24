package tn.naizo.moblootbags.common.bag;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

/**
 * Per-stack bag data, stored in the stack's NBT under {@code mob_loot_bags}:
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
        CompoundTag tag = stack.getTag();
        return tag == null ? new CompoundTag() : tag.getCompound(ROOT);
    }

    private static void edit(ItemStack stack, java.util.function.Consumer<CompoundTag> editor) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag root = tag.getCompound(ROOT);
        editor.accept(root);
        if (root.isEmpty()) {
            tag.remove(ROOT);
        } else {
            tag.put(ROOT, root);
        }
        if (tag.isEmpty()) {
            stack.setTag(null);
        }
    }

    public static @Nullable String theme(ItemStack stack) {
        String theme = root(stack).getString(THEME);
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
        CompoundTag root = root(stack);
        return root.contains(RIPE_AT, Tag.TAG_ANY_NUMERIC) ? root.getLong(RIPE_AT) : -1L;
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
        return root(stack).getLong(RIPEN_TICKS);
    }

    public static boolean notified(ItemStack stack) {
        return root(stack).getBoolean(NOTIFIED);
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
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }
        if (!tag.contains("is_active") && !tag.contains("lb_timer")) {
            return false;
        }
        boolean ripe = stack.isEnchanted();
        long remaining = Math.max(0, tag.getInt("lb_timer"));
        tag.remove("is_active");
        tag.remove("lb_timer");
        if (ripe) {
            tag.remove(ItemStack.TAG_ENCH);
        }
        setRipeAt(stack, ripe ? now : now + remaining);
        return true;
    }
}
