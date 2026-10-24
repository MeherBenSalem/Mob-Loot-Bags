package tn.naizo.moblootbags.common.client;

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
    private static final ResourceLocation BACKGROUND = MobLootBagsCommon.id("toast/reveal");
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

    /** 1.21.1 draws and asks for the visibility in one call. */
    @Override
    public Toast.Visibility render(GuiGraphics graphics, ToastComponent toasts, long fullyVisibleForMs) {
        renderContents(graphics, toasts.getMinecraft().font);
        return fullyVisibleForMs >= displayMs * toasts.getNotificationDisplayTimeMultiplier()
                ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    private void renderContents(GuiGraphics graphics, Font font) {
        int color = 0xFF000000 | bag.color();
        graphics.blitSprite(BACKGROUND, 0, 0, width, height());
        graphics.fill(3, 3, 5, height() - 3, color);

        graphics.renderFakeItem(new ItemStack(ModContent.bag(bag)), 9, 13);
        graphics.drawString(font, title, 30, 6, color, false);

        if (items.isEmpty()) {
            graphics.drawString(font, Component.translatable("toast.mob_loot_bags.empty"), 30, 23, 0xFFA0A0A0, false);
            return;
        }
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
}
