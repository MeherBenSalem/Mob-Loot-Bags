package tn.naizo.moblootbags.common.client;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.content.BagType;
import tn.naizo.moblootbags.common.content.ModContent;

/** Shows what a bag just gave: the bag, its tier colour, and up to eight item stacks. */
public class RevealToast implements Toast {
    /** A 160x32 nine-slice with a 4px border; 1.20.1 has no GUI sprite scaling, so {@link #blitBackground} slices it. */
    private static final ResourceLocation BACKGROUND = MobLootBagsCommon.id("textures/gui/sprites/toast/reveal.png");
    private static final int TEXTURE_WIDTH = 160;
    private static final int TEXTURE_HEIGHT = 32;
    private static final int BORDER = 4;
    private static final int MAX_ICONS = 8;

    private final BagType bag;
    private final List<ItemStack> items;
    private final long displayMs;
    private final Component title;
    private final int width;

    public RevealToast(BagType bag, int opened, List<ItemStack> items, long displayMs) {
        this.bag = bag;
        this.items = items;
        this.displayMs = displayMs;
        this.title = opened > 1
                ? Component.translatable("toast.mob_loot_bags.opened_many", opened, Component.translatable(bag.translationKey()))
                : Component.translatable("toast.mob_loot_bags.opened", Component.translatable(bag.translationKey()));
        // Wide enough for the title as well as the icons, so long names in any language stay inside the toast.
        int icons = Math.min(MAX_ICONS, Math.max(4, items.size()));
        int iconsWidth = 30 + icons * 18 + (items.size() > MAX_ICONS ? 20 : 6);
        this.width = Math.max(iconsWidth, 30 + Minecraft.getInstance().font.width(title) + 8);
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return 42;
    }

    @Override
    public Toast.Visibility render(GuiGraphics graphics, ToastComponent manager, long fullyVisibleForMs) {
        Font font = manager.getMinecraft().font;
        int color = 0xFF000000 | bag.color();
        blitBackground(graphics);
        graphics.fill(3, 3, 5, height() - 3, color);

        graphics.renderFakeItem(new ItemStack(ModContent.bag(bag)), 9, 13);
        graphics.drawString(font, title, 30, 6, color, false);

        if (items.isEmpty()) {
            graphics.drawString(font, Component.translatable("toast.mob_loot_bags.empty"), 30, 23, 0xFFA0A0A0, false);
        } else {
            int shown = Math.min(MAX_ICONS, items.size());
            for (int i = 0; i < shown; i++) {
                ItemStack stack = items.get(i);
                int x = 30 + i * 18;
                graphics.renderFakeItem(stack, x, 19);
                graphics.renderItemDecorations(font, stack, x, 19);
            }
            if (items.size() > MAX_ICONS) {
                graphics.drawString(font, Component.literal("+" + (items.size() - MAX_ICONS)), 32 + shown * 18, 24, 0xFFD0D0D0, false);
            }
        }
        return fullyVisibleForMs >= displayMs * manager.getNotificationDisplayTimeMultiplier()
                ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    /** Draws the background stretched to the toast's size: fixed corners, stretched edges and centre. */
    private void blitBackground(GuiGraphics graphics) {
        int[] xs = {0, BORDER, width - BORDER};
        int[] widths = {BORDER, width - 2 * BORDER, BORDER};
        int[] us = {0, BORDER, TEXTURE_WIDTH - BORDER};
        int[] uWidths = {BORDER, TEXTURE_WIDTH - 2 * BORDER, BORDER};
        int[] ys = {0, BORDER, height() - BORDER};
        int[] heights = {BORDER, height() - 2 * BORDER, BORDER};
        int[] vs = {0, BORDER, TEXTURE_HEIGHT - BORDER};
        int[] vHeights = {BORDER, TEXTURE_HEIGHT - 2 * BORDER, BORDER};
        RenderSystem.enableBlend();
        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 3; row++) {
                graphics.blit(BACKGROUND, xs[column], ys[row], widths[column], heights[row], us[column], vs[row],
                        uWidths[column], vHeights[row], TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }
        }
        RenderSystem.disableBlend();
    }
}
