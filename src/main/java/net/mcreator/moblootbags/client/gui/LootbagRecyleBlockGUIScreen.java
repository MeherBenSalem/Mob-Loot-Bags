package net.mcreator.moblootbags.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.moblootbags.world.inventory.LootbagRecyleBlockGUIMenu;
import net.mcreator.moblootbags.procedures.ReturnCurrentBlockStoredXpProcedure;
import net.mcreator.moblootbags.network.LootbagRecyleBlockGUIButtonMessage;
import net.mcreator.moblootbags.MobLootBagsMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class LootbagRecyleBlockGUIScreen extends AbstractContainerScreen<LootbagRecyleBlockGUIMenu> {
	private final static HashMap<String, Object> guistate = LootbagRecyleBlockGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	ImageButton imagebutton_addbutton;

	public LootbagRecyleBlockGUIScreen(LootbagRecyleBlockGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("mob_loot_bags:textures/screens/lootbag_recyle_block_gui.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		if (mouseX > leftPos + 79 && mouseX < leftPos + 96 && mouseY > topPos + 64 && mouseY < topPos + 82)
			guiGraphics.renderTooltip(font, Component.literal(ReturnCurrentBlockStoredXpProcedure.execute(world, x, y, z)), mouseX, mouseY);
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
	}

	@Override
	public void init() {
		super.init();
		imagebutton_addbutton = new ImageButton(this.leftPos + 80, this.topPos + 65, 16, 16, 0, 0, 16, new ResourceLocation("mob_loot_bags:textures/screens/atlas/imagebutton_addbutton.png"), 16, 32, e -> {
			if (true) {
				MobLootBagsMod.PACKET_HANDLER.sendToServer(new LootbagRecyleBlockGUIButtonMessage(0, x, y, z));
				LootbagRecyleBlockGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		});
		guistate.put("button:imagebutton_addbutton", imagebutton_addbutton);
		this.addRenderableWidget(imagebutton_addbutton);
	}
}
