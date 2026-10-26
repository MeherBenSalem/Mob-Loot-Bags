package com.nightbeam.reel.mixin;

import com.nightbeam.reel.ReelCamera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * HUD for the reel: "none" draws nothing; "full" is the normal HUD minus the layers the shot hides (hudHide), shifted
 * up by hudOffset GUI pixels so the hotbar clears the captions phone apps draw over the bottom of the video. Titles
 * move by titleOffset on top of that, into their own band between the toasts and the hotbar.
 */
@Mixin(Gui.class)
public abstract class GuiMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void reel$hud(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if ("none".equals(ReelCamera.hud)) {
			ci.cancel();
			return;
		}
		graphics.pose().pushPose();
		graphics.pose().translate(0, -ReelCamera.hudOffset, 0);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void reel$hudEnd(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		graphics.pose().popPose();
	}

	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void reel$crosshair(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ReelCamera.hides("crosshair")) ci.cancel();
	}

	@Inject(method = {"renderHotbarAndDecorations", "renderExperienceLevel"}, at = @At("HEAD"), cancellable = true)
	private void reel$hotbar(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ReelCamera.hides("hotbar")) ci.cancel();
	}

	@Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
	private void reel$effects(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ReelCamera.hides("effects")) ci.cancel();
	}

	@Inject(method = "renderChat", at = @At("HEAD"), cancellable = true)
	private void reel$chat(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ReelCamera.hides("chat")) ci.cancel();
	}

	@Inject(method = "renderOverlayMessage", at = @At("HEAD"), cancellable = true)
	private void reel$actionbar(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ReelCamera.hides("actionbar")) ci.cancel();
	}

	@Inject(method = "renderTitle", at = @At("HEAD"), cancellable = true)
	private void reel$title(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ReelCamera.hides("title")) {
			ci.cancel();
			return;
		}
		graphics.pose().pushPose();
		graphics.pose().translate(0, ReelCamera.titleOffset, 0);
	}

	@Inject(method = "renderTitle", at = @At("TAIL"))
	private void reel$titleEnd(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		graphics.pose().popPose();
	}

	@Inject(method = {"renderScoreboardSidebar", "renderTabList", "renderDemoOverlay", "renderCameraOverlays"}, at = @At("HEAD"),
			cancellable = true)
	private void reel$misc(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ReelCamera.hides("misc")) ci.cancel();
	}
}
