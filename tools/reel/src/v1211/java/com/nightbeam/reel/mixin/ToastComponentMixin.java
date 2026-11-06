package com.nightbeam.reel.mixin;

import com.nightbeam.reel.ReelCamera;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Moves the toasts down, clear of the phone app's own top bar. */
@Mixin(ToastComponent.class)
public abstract class ToastComponentMixin {
	@Inject(method = "render", at = @At("HEAD"))
	private void reel$push(GuiGraphics graphics, CallbackInfo ci) {
		graphics.pose().pushPose();
		graphics.pose().translate(-ReelCamera.toastOffset[0], ReelCamera.toastOffset[1], 0);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void reel$pop(GuiGraphics graphics, CallbackInfo ci) {
		graphics.pose().popPose();
	}
}
