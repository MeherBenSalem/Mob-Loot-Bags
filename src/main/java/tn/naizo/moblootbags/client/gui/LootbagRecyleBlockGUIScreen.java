package tn.naizo.moblootbags.client.gui;

import tn.naizo.moblootbags.world.inventory.LootbagRecyleBlockGUIMenu;
import tn.naizo.moblootbags.procedures.ReturnCurrentBlockStoredXpProcedure;
import tn.naizo.moblootbags.network.LootbagRecyleBlockGUIButtonMessage;
import tn.naizo.moblootbags.init.MobLootBagsModScreens;

import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;

public class LootbagRecyleBlockGUIScreen extends AbstractContainerScreen<LootbagRecyleBlockGUIMenu> implements MobLootBagsModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_addbutton;

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

	@Override
	public void updateMenuState(int elementType, String name, Object elementState) {
		menuStateUpdateActive = true;
		menuStateUpdateActive = false;
	}

	private static final ResourceLocation texture = ResourceLocation.parse("mob_loot_bags:textures/screens/lootbag_recyle_block_gui.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		boolean customTooltipShown = false;
		if (mouseX > leftPos + 81 && mouseX < leftPos + 97 && mouseY > topPos + 65 && mouseY < topPos + 81) {
			guiGraphics.setTooltipForNextFrame(font, Component.translatable("gui.mob_loot_bags.lootbag_recyle_block_gui.tooltip_take_out_the_xp"), mouseX, mouseY);
			customTooltipShown = true;
		}
		if (!customTooltipShown)
			this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
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
		guiGraphics.drawString(this.font, ReturnCurrentBlockStoredXpProcedure.execute(world, x, y, z), 24, 68, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.mob_loot_bags.lootbag_recyle_block_gui.label_xp"), 8, 68, -16777216, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_addbutton = new ImageButton(this.leftPos + 80, this.topPos + 65, 16, 16,
				new WidgetSprites(ResourceLocation.parse("mob_loot_bags:textures/screens/add-button.png"), ResourceLocation.parse("mob_loot_bags:textures/screens/add-button.png")), e -> {
					int x = LootbagRecyleBlockGUIScreen.this.x;
					int y = LootbagRecyleBlockGUIScreen.this.y;
					if (true) {
						ClientPacketDistributor.sendToServer(new LootbagRecyleBlockGUIButtonMessage(0, x, y, z));
						LootbagRecyleBlockGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(RenderPipelines.GUI_TEXTURED, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_addbutton);
	}
}