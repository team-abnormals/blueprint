package com.minecraftabnormals.abnormals_core.core.mixin.client;

import net.minecraft.client.renderer.ActiveRenderInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ActiveRenderInfo.class)
public interface ActiveRenderInfoInvokerMixin {
	@Invoker
	void callMove(double z, double y, double x);
}
