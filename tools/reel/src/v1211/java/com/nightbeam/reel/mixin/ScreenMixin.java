package com.nightbeam.reel.mixin;

import com.nightbeam.reel.ReelCamera;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Shift as the director holds it (tooltips read the keyboard directly, which the virtual display never presses). */
@Mixin(Screen.class)
public abstract class ScreenMixin {
	@Inject(method = "hasShiftDown", at = @At("HEAD"), cancellable = true)
	private static void reel$shift(CallbackInfoReturnable<Boolean> cir) {
		if (ReelCamera.shift) cir.setReturnValue(true);
	}
}
