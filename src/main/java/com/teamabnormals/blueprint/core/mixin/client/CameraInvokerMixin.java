package com.teamabnormals.blueprint.core.mixin.client;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraInvokerMixin {
	@Invoker
	void callMove(double z, double y, double x);
}
