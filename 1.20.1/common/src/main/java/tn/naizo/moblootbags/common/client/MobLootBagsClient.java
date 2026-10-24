package tn.naizo.moblootbags.common.client;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tn.naizo.moblootbags.common.config.LootBagsConfig;
import tn.naizo.moblootbags.common.loot.LootService;
import tn.naizo.moblootbags.common.loot.LootService.PreviewEntry;
import tn.naizo.moblootbags.common.network.RevealPayload;
import tn.naizo.moblootbags.common.network.SyncPayload;

/** Client entry point shared by both loaders. Only loaded on the physical client. */
public final class MobLootBagsClient {
    private static Map<String, List<PreviewEntry>> previews = Map.of();
    private static int previewLines = 8;

    private MobLootBagsClient() {
    }

    public static void init() {
        ClientBridge.install(
                () -> {
                    Minecraft minecraft = Minecraft.getInstance();
                    return minecraft.level != null ? minecraft.level.getGameTime() : 0L;
                },
                (stack, type, theme, tooltip) -> {
                    if (!LootBagsConfig.client().previewTooltips) {
                        return;
                    }
                    List<PreviewEntry> entries = previews.get(LootService.previewKey(type, theme));
                    if (entries == null && theme != null) {
                        entries = previews.get(LootService.previewKey(type, null));
                    }
                    if (entries == null || entries.isEmpty()) {
                        return;
                    }
                    if (!Screen.hasShiftDown()) {
                        tooltip.accept(Component.translatable("tooltip.mob_loot_bags.preview_hint").withStyle(ChatFormatting.DARK_GRAY));
                        return;
                    }
                    tooltip.accept(Component.translatable("tooltip.mob_loot_bags.preview_header").withStyle(ChatFormatting.GOLD));
                    int shown = Math.min(previewLines, entries.size());
                    for (int i = 0; i < shown; i++) {
                        PreviewEntry entry = entries.get(i);
                        String count = entry.min() == entry.max() ? "×" + entry.min() : "×" + entry.min() + "-" + entry.max();
                        tooltip.accept(Component.literal(String.format(Locale.ROOT, " %3.0f%% ", Math.max(1.0F, entry.chance() * 100.0F)))
                                .withStyle(ChatFormatting.YELLOW)
                                .append(entry.icon().getHoverName().copy().withStyle(entry.icon().getRarity().color))
                                .append(Component.literal(" " + count).withStyle(ChatFormatting.DARK_GRAY)));
                    }
                    if (entries.size() > shown) {
                        tooltip.accept(Component.translatable("tooltip.mob_loot_bags.preview_more", entries.size() - shown)
                                .withStyle(ChatFormatting.DARK_GRAY));
                    }
                });
    }

    public static void handleSync(SyncPayload payload) {
        ClientBridge.setSyncedHoldTicks(payload.holdTicks());
        previews = payload.previews();
        previewLines = payload.previewLines();
    }

    public static void handleReveal(RevealPayload payload) {
        if (!LootBagsConfig.client().revealToast) {
            return;
        }
        Minecraft.getInstance().getToasts().addToast(new RevealToast(payload.bag(), payload.opened(), payload.items(),
                (long) (LootBagsConfig.client().toastSeconds * 1000.0)));
    }

    /** Clears server-provided state when leaving a world. */
    public static void reset() {
        ClientBridge.setSyncedHoldTicks(null);
        previews = Map.of();
    }
}
