package com.minecraftabnormals.abnormals_core.core.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * @author - SmellyModder(Luke Tonon)
 */
public final class EntityUtil {

	public static HitResult rayTrace(Entity entity, double distance, float delta) {
		return entity.level.clip(new ClipContext(
				entity.getEyePosition(delta),
				entity.getEyePosition(delta).add(entity.getViewVector(delta).scale(distance)),
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				entity
		));
	}

	public static HitResult rayTraceWithCustomDirection(Entity entity, float pitch, float yaw, double distance, float delta) {
		return entity.level.clip(new ClipContext(
				entity.getEyePosition(delta),
				entity.getEyePosition(delta).add(getVectorForRotation(pitch, yaw).scale(distance)),
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				entity
		));
	}

	public static HitResult rayTraceUpWithCustomDirection(Entity entity, float pitch, float yaw, double distance, float delta) {
		return entity.level.clip(new ClipContext(
				entity.getEyePosition(delta),
				entity.getEyePosition(delta).add(getUpVectorForRotation(pitch, yaw).scale(distance)),
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				entity
		));
	}

	public static final Vec3 getVectorForRotation(float pitch, float yaw) {
		float f = pitch * ((float) Math.PI / 180F);
		float f1 = -yaw * ((float) Math.PI / 180F);
		float f2 = Mth.cos(f1);
		float f3 = Mth.sin(f1);
		float f4 = Mth.cos(f);
		float f5 = Mth.sin(f);
		return new Vec3((double) (f3 * f4), (double) (-f5), (double) (f2 * f4));
	}

	public static final Vec3 getUpVectorForRotation(float pitch, float yaw) {
		float f = (pitch - 90.0F) * ((float) Math.PI / 180F);
		float f1 = -yaw * ((float) Math.PI / 180F);
		float f2 = Mth.cos(f1);
		float f3 = Mth.sin(f1);
		float f4 = Mth.cos(f);
		float f5 = Mth.sin(f);
		return new Vec3((double) (f3 * f4), (double) (-f5), (double) (f2 * f4));
	}
}

