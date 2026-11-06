package com.nightbeam.reel.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The cursor position screens read: there is no real mouse on the virtual display. */
@Mixin(MouseHandler.class)
public interface MouseHandlerAccessor {
	@Accessor("xpos")
	void reel$setX(double x);

	@Accessor("ypos")
	void reel$setY(double y);
}
