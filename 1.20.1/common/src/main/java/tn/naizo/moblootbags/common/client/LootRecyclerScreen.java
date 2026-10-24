package tn.naizo.moblootbags.common.client;

import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.machine.LootRecyclerMenu;

public class LootRecyclerScreen extends AbstractContainerScreen<LootRecyclerMenu> {
    private static final ResourceLocation TEXTURE = MobLootBagsCommon.id("textures/gui/container/loot_recycler.png");
    private static final int BUTTON_X = 139;
    private static final int BUTTON_Y = 54;
    private static final int BUTTON_W = 30;
    private static final int BUTTON_H = 14;

    public LootRecyclerScreen(LootRecyclerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 73;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.blit(TEXTURE, x, y, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);

        int arrow = Math.round(22.0F * menu.progress() / menu.ticksPerItem());
        if (arrow > 0) {
            graphics.blit(TEXTURE, x + 66, y + 36, 176.0F, 0.0F, arrow, 15, 256, 256);
        }
        int fill = Math.min(52, Math.round(52.0F * menu.points() / menu.pointsPerBag()));
        if (fill > 0) {
            graphics.blit(TEXTURE, x + 94, y + 18 + 52 - fill, 198.0F, 52.0F - fill, 14, fill, 256, 256);
        }
        int xp = menu.storedXp();
        float v = xp <= 0 ? 80.0F : overButton(mouseX, mouseY) ? 66.0F : 52.0F;
        graphics.blit(TEXTURE, x + BUTTON_X, y + BUTTON_Y, 176.0F, v, BUTTON_W, BUTTON_H, 256, 256);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int xm, int ym) {
        super.renderLabels(graphics, xm, ym);
        String amount = compact(menu.storedXp());
        graphics.drawString(font, amount, 154 - font.width(amount) / 2, 40, 0xFF80FF20, true);
        Component take = Component.translatable("gui.mob_loot_bags.recycler.take");
        int color = menu.storedXp() > 0 ? 0xFFFFFFFF : 0xFFA0A0A0;
        graphics.drawString(font, take, BUTTON_X + BUTTON_W / 2 - font.width(take) / 2, BUTTON_Y + 3, color, true);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        int rx = mouseX - leftPos;
        int ry = mouseY - topPos;
        if (rx >= 93 && rx < 109 && ry >= 17 && ry < 71) {
            graphics.renderTooltip(font, List.of(
                    Component.translatable("gui.mob_loot_bags.recycler.salvage", menu.points(), menu.pointsPerBag()),
                    Component.translatable("gui.mob_loot_bags.recycler.salvage.desc").withStyle(ChatFormatting.GRAY)),
                    Optional.empty(), mouseX, mouseY);
        } else if (rx >= 138 && rx < 170 && ry >= 17 && ry < 52) {
            graphics.renderTooltip(font, List.of(
                    Component.translatable("gui.mob_loot_bags.recycler.xp", menu.storedXp()),
                    Component.translatable("gui.mob_loot_bags.recycler.xp.desc").withStyle(ChatFormatting.GRAY)),
                    Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && overButton(mouseX, mouseY) && menu.storedXp() > 0 && minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, LootRecyclerMenu.BUTTON_COLLECT);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean overButton(double mouseX, double mouseY) {
        return mouseX >= leftPos + BUTTON_X && mouseX < leftPos + BUTTON_X + BUTTON_W
                && mouseY >= topPos + BUTTON_Y && mouseY < topPos + BUTTON_Y + BUTTON_H;
    }

    private static String compact(int value) {
        if (value >= 1_000_000) {
            return String.format(java.util.Locale.ROOT, "%.1fM", value / 1_000_000.0);
        }
        if (value >= 10_000) {
            return String.format(java.util.Locale.ROOT, "%.1fk", value / 1_000.0);
        }
        return Integer.toString(value);
    }
}
