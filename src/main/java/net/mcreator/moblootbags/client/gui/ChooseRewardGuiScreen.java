package net.mcreator.moblootbags.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.moblootbags.world.inventory.ChooseRewardGuiMenu;
import net.mcreator.moblootbags.network.ChooseRewardGuiButtonMessage;
import net.mcreator.moblootbags.MobLootBagsMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class ChooseRewardGuiScreen extends AbstractContainerScreen<ChooseRewardGuiMenu> {
	private final static HashMap<String, Object> guistate = ChooseRewardGuiMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_select;
	Button button_select1;
	Button button_select2;

	public ChooseRewardGuiScreen(ChooseRewardGuiMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("mob_loot_bags:textures/screens/choose_reward_gui.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.mob_loot_bags.choose_reward_gui.label_chooose_your_reward"), 40, 7, -16777216, false);
	}

	@Override
	public void init() {
		super.init();
		button_select = Button.builder(Component.translatable("gui.mob_loot_bags.choose_reward_gui.button_select"), e -> {
			if (true) {
				MobLootBagsMod.PACKET_HANDLER.sendToServer(new ChooseRewardGuiButtonMessage(0, x, y, z));
				ChooseRewardGuiButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 120, this.topPos + 46, 43, 20).build();
		guistate.put("button:button_select", button_select);
		this.addRenderableWidget(button_select);
		button_select1 = Button.builder(Component.translatable("gui.mob_loot_bags.choose_reward_gui.button_select1"), e -> {
			if (true) {
				MobLootBagsMod.PACKET_HANDLER.sendToServer(new ChooseRewardGuiButtonMessage(1, x, y, z));
				ChooseRewardGuiButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		}).bounds(this.leftPos + 65, this.topPos + 46, 44, 20).build();
		guistate.put("button:button_select1", button_select1);
		this.addRenderableWidget(button_select1);
		button_select2 = Button.builder(Component.translatable("gui.mob_loot_bags.choose_reward_gui.button_select2"), e -> {
			if (true) {
				MobLootBagsMod.PACKET_HANDLER.sendToServer(new ChooseRewardGuiButtonMessage(2, x, y, z));
				ChooseRewardGuiButtonMessage.handleButtonAction(entity, 2, x, y, z);
			}
		}).bounds(this.leftPos + 10, this.topPos + 46, 45, 20).build();
		guistate.put("button:button_select2", button_select2);
		this.addRenderableWidget(button_select2);
	}
}
