package com.teamabnormals.blueprint.core.mixin.client;

import com.teamabnormals.blueprint.client.screen.shaking.ScreenShakeHandler;
import com.teamabnormals.blueprint.core.BlueprintConfig;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
	@Shadow
	protected abstract void move(float z, float y, float x);

	@Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V", shift = At.Shift.AFTER))
	private void shakeCamera(BlockGetter getter, Entity entity, boolean p_90578_, boolean p_90579_, float partialTicks, CallbackInfo info) {
		double screenShakeScale = BlueprintConfig.CLIENT.screenShakeScale;
		if (screenShakeScale <= 0.0D) return;
		ScreenShakeHandler shaker = ScreenShakeHandler.INSTANCE;
		double x = shaker.getIntensityX(partialTicks), y = shaker.getIntensityY(partialTicks), z = shaker.getIntensityZ(partialTicks);
		if (x != 0.0F || y != 0.0F || z != 0.0F) {
			this.move((float) (z * screenShakeScale), (float) (y * screenShakeScale), (float) (x * screenShakeScale));
		}
	}
}
