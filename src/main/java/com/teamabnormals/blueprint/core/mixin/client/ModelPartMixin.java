package com.teamabnormals.blueprint.core.mixin.client;

import com.teamabnormals.blueprint.core.endimator.EndimatablePart;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelPart.class)
public final class ModelPartMixin implements EndimatablePart {
	@Shadow
	public float x;
	@Shadow
	public float y;
	@Shadow
	public float z;
	@Shadow
	public float xRot;
	@Shadow
	public float yRot;
	@Shadow
	public float zRot;

	@Override
	public void addPos(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
	}

	@Override
	public void addRotation(float x, float y, float z) {
		this.xRot += x;
		this.yRot += y;
		this.zRot += z;
	}
}
