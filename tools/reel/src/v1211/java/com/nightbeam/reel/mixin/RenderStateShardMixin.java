package com.nightbeam.reel.mixin;

import com.nightbeam.reel.ReelClock;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** The enchantment glint scrolls on the video's clock, like everything else. */
@Mixin(RenderStateShard.class)
public abstract class RenderStateShardMixin {
	@Redirect(method = "setupGlintTexturing", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J"))
	private static long reel$millis() {
		return ReelClock.uiMillis();
	}
}
