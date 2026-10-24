package tn.naizo.moblootbags.common.client;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.content.BagType;
import tn.naizo.moblootbags.common.content.ModContent;

/** Shows what a bag just gave: the bag, its tier colour, and up to eight item stacks. */
public class RevealToast implements Toast {
    private static final Identifier BACKGROUND = MobLootBagsCommon.id("toast/reveal");
    private static final int MAX_ICONS = 8;

    private final BagType bag;
    private final List<ItemStack> items;
    private final long displayMs;
    private final Component title;
    private final int width;
    private Toast.Visibility visibility = Toast.Visibility.SHOW;

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
    public Toast.Visibility getWantedVisibility() {
        return visibility;
    }

    @Override
    public void update(ToastManager manager, long fullyVisibleForMs) {
        visibility = fullyVisibleForMs >= displayMs * manager.getNotificationDisplayTimeMultiplier()
                ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs) {
        int color = 0xFF000000 | bag.color();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 0, 0, width, height());
        graphics.fill(3, 3, 5, height() - 3, color);

        graphics.fakeItem(new ItemStack(ModContent.bag(bag)), 9, 13);
        graphics.text(font, title, 30, 6, color, false);

        if (items.isEmpty()) {
            graphics.text(font, Component.translatable("toast.mob_loot_bags.empty"), 30, 23, 0xFFA0A0A0, false);
            return;
        }
        int shown = Math.min(MAX_ICONS, items.size());
        for (int i = 0; i < shown; i++) {
            ItemStack stack = items.get(i);
            int x = 30 + i * 18;
            graphics.fakeItem(stack, x, 19);
            graphics.itemDecorations(font, stack, x, 19);
        }
        if (items.size() > MAX_ICONS) {
            graphics.text(font, Component.literal("+" + (items.size() - MAX_ICONS)), 32 + shown * 18, 24, 0xFFD0D0D0, false);
        }
    }
}
