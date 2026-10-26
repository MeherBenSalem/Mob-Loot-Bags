package com.nightbeam.reel.mixin;

import com.nightbeam.reel.ReelClock;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Toasts slide and time out on the video's clock instead of the wall clock. */
@Mixin(targets = "net.minecraft.client.gui.components.toasts.ToastComponent$ToastInstance")
public abstract class ToastInstanceMixin {
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J"))
	private long reel$millis() {
		return ReelClock.uiMillis();
	}
}
