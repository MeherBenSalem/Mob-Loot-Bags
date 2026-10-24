package tn.naizo.moblootbags.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.machine.BagOpenerBlockEntity;
import tn.naizo.moblootbags.common.machine.BagOpenerMenu;

public class BagOpenerScreen extends AbstractContainerScreen<BagOpenerMenu> {
    private static final ResourceLocation TEXTURE = MobLootBagsCommon.id("textures/gui/container/bag_opener.png");
    private static final int BUTTON_X = 157;
    private static final int BUTTON_Y = 3;
    private static final int BUTTON_SIZE = 12;
    private static final String[] MODES = {"always", "high", "low", "pulse"};

    public BagOpenerScreen(BagOpenerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 184;
        this.inventoryLabelY = 91;
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

        int status = menu.status();
        int barRow = switch (status) {
            case BagOpenerBlockEntity.STATUS_FULL -> 5;
            case BagOpenerBlockEntity.STATUS_WAITING -> 10;
            default -> 0;
        };
        int width = status == BagOpenerBlockEntity.STATUS_WORKING
                ? Math.round(160.0F * menu.progress() / menu.ticksPerBag())
                : status == BagOpenerBlockEntity.STATUS_IDLE ? 0 : 160;
        if (width > 0) {
            graphics.blit(TEXTURE, x + 8, y + 39, 0.0F, 184.0F + barRow, width, 5, 256, 256);
        }

        int mode = Math.floorMod(menu.mode(), MODES.length);
        boolean hovered = overButton(mouseX, mouseY);
        graphics.blit(TEXTURE, x + BUTTON_X, y + BUTTON_Y, 176.0F + mode * BUTTON_SIZE,
                hovered ? BUTTON_SIZE : 0.0F, BUTTON_SIZE, BUTTON_SIZE, 256, 256);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        if (overButton(mouseX, mouseY)) {
            String mode = MODES[Math.floorMod(menu.mode(), MODES.length)];
            graphics.renderTooltip(font, java.util.List.of(
                    Component.translatable("gui.mob_loot_bags.opener.mode." + mode),
                    Component.translatable("gui.mob_loot_bags.opener.mode." + mode + ".desc").withStyle(net.minecraft.ChatFormatting.GRAY),
                    Component.translatable("gui.mob_loot_bags.opener.mode.click").withStyle(net.minecraft.ChatFormatting.DARK_GRAY)),
                    java.util.Optional.empty(), mouseX, mouseY);
        } else if (mouseX >= leftPos + 7 && mouseX < leftPos + 169 && mouseY >= topPos + 37 && mouseY < topPos + 46) {
            String key = switch (menu.status()) {
                case BagOpenerBlockEntity.STATUS_WORKING -> "working";
                case BagOpenerBlockEntity.STATUS_FULL -> "full";
                case BagOpenerBlockEntity.STATUS_WAITING -> "waiting";
                default -> "idle";
            };
            graphics.renderTooltip(font, Component.translatable("gui.mob_loot_bags.opener.status." + key), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && overButton(mouseX, mouseY) && minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, BagOpenerMenu.BUTTON_MODE);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean overButton(double mouseX, double mouseY) {
        return mouseX >= leftPos + BUTTON_X && mouseX < leftPos + BUTTON_X + BUTTON_SIZE
                && mouseY >= topPos + BUTTON_Y && mouseY < topPos + BUTTON_Y + BUTTON_SIZE;
    }
}
