package tn.naizo.moblootbags.common.client;

import java.util.function.Consumer;
import java.util.function.LongSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import tn.naizo.moblootbags.common.content.BagType;

/**
 * Lets common code (items, tooltips) reach client state without loading client classes on a dedicated server.
 * {@code MobLootBagsClient.init} installs the real hooks.
 */
public final class ClientBridge {
    private static LongSupplier gameTime = () -> 0L;
    private static PreviewAppender previews = (stack, type, theme, tooltip) -> {
    };
    private static @Nullable Integer syncedHoldTicks;

    @FunctionalInterface
    public interface PreviewAppender {
        void append(ItemStack stack, BagType type, @Nullable String theme, Consumer<Component> tooltip);
    }

    private ClientBridge() {
    }

    public static void install(LongSupplier clientGameTime, PreviewAppender previewAppender) {
        gameTime = clientGameTime;
        previews = previewAppender;
    }

    public static long gameTime() {
        return gameTime.getAsLong();
    }

    public static void appendPreview(ItemStack stack, BagType type, @Nullable String theme, Consumer<Component> tooltip) {
        previews.append(stack, type, theme, tooltip);
    }

    public static @Nullable Integer syncedHoldTicks() {
        return syncedHoldTicks;
    }

    public static void setSyncedHoldTicks(@Nullable Integer ticks) {
        syncedHoldTicks = ticks;
    }
}
