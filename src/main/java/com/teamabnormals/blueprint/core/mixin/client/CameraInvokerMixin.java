package com.teamabnormals.blueprint.core.mixin.client;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraInvokerMixin {
	@Invoker
	void callMove(float z, float y, float x);
}
