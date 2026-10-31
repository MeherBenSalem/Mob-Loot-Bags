package com.nightbeam.reel.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nightbeam.reel.ReelCamera;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * First-person hands turned in toward the middle of the view, as if the head turned to them: they sit at the edges of
 * a landscape view, which a 9:16 frame (about 40 degrees across) crops away. Rotating about the eye keeps each item
 * seen from the angle the player normally sees it.
 */
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
	@Inject(method = "renderArmWithItem", at = @At("HEAD"))
	private void reel$inset(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress,
			ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
		poseStack.pushPose();
		HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
		float side = arm == HumanoidArm.RIGHT ? 1 : -1;
		poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(side * ReelCamera.handInset[0]));
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(ReelCamera.handInset[1]));
	}

	@Inject(method = "renderArmWithItem", at = @At("RETURN"))
	private void reel$insetEnd(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress,
			ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
		poseStack.popPose();
	}
}
