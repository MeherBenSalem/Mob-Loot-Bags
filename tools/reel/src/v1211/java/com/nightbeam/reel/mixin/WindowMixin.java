package com.nightbeam.reel.mixin;

import com.mojang.blaze3d.platform.Window;
import com.nightbeam.reel.ReelCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The shot's GUI scale, even above the game's limit: vanilla wants 320 GUI pixels across, which caps a 1080-wide
 * portrait window at 3, too small to read on a phone. The HUD and the screens in the reel fit in 270.
 */
@Mixin(Window.class)
public abstract class WindowMixin {
	@Inject(method = "calculateScale", at = @At("HEAD"), cancellable = true)
	private void reel$scale(int guiScale, boolean forceUnicode, CallbackInfoReturnable<Integer> cir) {
		if (ReelCamera.guiScale > 0) cir.setReturnValue(ReelCamera.guiScale);
	}
}
