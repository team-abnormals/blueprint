package com.teamabnormals.blueprint.core.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * A class containing some useful entity methods.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class EntityUtil {
	/**
	 * Traces a given {@link Entity} for a given distance and delta.
	 *
	 * @param entity   An {@link Entity} to trace from.
	 * @param distance The distance of the trace.
	 * @param delta    The delta of the trace.
	 * @return The {@link HitResult} of the traced entity.
	 */
	public static HitResult rayTrace(Entity entity, double distance, float delta) {
		return entity.level.clip(new ClipContext(
				entity.getEyePosition(delta),
				entity.getEyePosition(delta).add(entity.getViewVector(delta).scale(distance)),
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				entity
		));
	}

	/**
	 * Traces a given {@link Entity} at a custom direction for a given distance and delta.
	 *
	 * @param entity   An {@link Entity} to trace from.
	 * @param pitch    The pitch for the trace.
	 * @param yaw      The yaw for the trace.
	 * @param distance The distance of the trace.
	 * @param delta    The delta of the trace.
	 * @return The {@link HitResult} of the traced entity.
	 */
	public static HitResult rayTraceWithCustomDirection(Entity entity, float pitch, float yaw, double distance, float delta) {
		return entity.level.clip(new ClipContext(
				entity.getEyePosition(delta),
				entity.getEyePosition(delta).add(getVectorForRotation(pitch, yaw).scale(distance)),
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				entity
		));
	}

	/**
	 * Upwardly traces a given {@link Entity} at a custom direction for a given distance and delta.
	 *
	 * @param entity   An {@link Entity} to trace from.
	 * @param pitch    The pitch for the trace.
	 * @param yaw      The yaw for the trace.
	 * @param distance The distance of the trace.
	 * @param delta    The delta of the trace.
	 * @return The {@link HitResult} of the traced entity.
	 */
	public static HitResult rayTraceUpWithCustomDirection(Entity entity, float pitch, float yaw, double distance, float delta) {
		return entity.level.clip(new ClipContext(
				entity.getEyePosition(delta),
				entity.getEyePosition(delta).add(getUpVectorForRotation(pitch, yaw).scale(distance)),
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				entity
		));
	}

	/**
	 * Gets the {@link Vec3} for a yaw and a pitch.
	 *
	 * @param pitch The pitch to use.
	 * @param yaw   The yaw to use.
	 * @return The {@link Vec3} for a yaw and a pitch.
	 */
	public static Vec3 getVectorForRotation(float pitch, float yaw) {
		float f = pitch * ((float) Math.PI / 180F);
		float f1 = -yaw * ((float) Math.PI / 180F);
		float f2 = Mth.cos(f1);
		float f3 = Mth.sin(f1);
		float f4 = Mth.cos(f);
		float f5 = Mth.sin(f);
		return new Vec3(f3 * f4, -f5, f2 * f4);
	}

	/**
	 * Gets the upward {@link Vec3} for a yaw and a pitch.
	 *
	 * @param pitch The pitch to use.
	 * @param yaw   The yaw to use.
	 * @return The upward {@link Vec3} for a yaw and a pitch.
	 */
	public static Vec3 getUpVectorForRotation(float pitch, float yaw) {
		float f = (pitch - 90.0F) * ((float) Math.PI / 180F);
		float f1 = -yaw * ((float) Math.PI / 180F);
		float f2 = Mth.cos(f1);
		float f3 = Mth.sin(f1);
		float f4 = Mth.cos(f);
		float f5 = Mth.sin(f);
		return new Vec3(f3 * f4, -f5, f2 * f4);
	}
}

