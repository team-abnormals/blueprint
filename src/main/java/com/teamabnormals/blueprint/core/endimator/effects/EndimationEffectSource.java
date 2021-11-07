package com.teamabnormals.blueprint.core.endimator.effects;

import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;

/**
 * The interface for defining an object that can process {@link ConfiguredEndimationEffect} instances.
 * <p>This gets mixin'd into {@link net.minecraft.world.entity.Entity}.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface EndimationEffectSource {
	/**
	 * Gets the {@link Position} of this source.
	 * <p>This gets used by effects that need to know where to process.</p>
	 *
	 * @return The {@link Position} of this source.
	 */
	default Position getPos() {
		return Vec3.ZERO;
	}

	/**
	 * If this source is active.
	 * <p>This gets used by effects that should not continue if their sources are not active.</p>
	 *
	 * @return If this source is active.
	 */
	default boolean isActive() {
		return true;
	}
}
