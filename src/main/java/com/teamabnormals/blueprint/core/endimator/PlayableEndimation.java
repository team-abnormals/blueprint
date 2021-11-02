package com.teamabnormals.blueprint.core.endimator;

import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * A record class that represents a loop-able animation processed over a given tick duration.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record PlayableEndimation(ResourceLocation location, int duration, LoopType loopType) {
	public static final PlayableEndimation BLANK = new PlayableEndimation(new ResourceLocation(Blueprint.MOD_ID, "blank"), 0, LoopType.NONE);

	/**
	 * Looks up the {@link #location} in {@link Blueprint#ENDIMATION_LOADER} to get its corresponding {@link Endimation}.
	 *
	 * @return The corresponding {@link Endimation} of the {@link #location}.
	 */
	@Nullable
	public Endimation asEndimation() {
		return Blueprint.ENDIMATION_LOADER.getEndimation(this.location);
	}

	/**
	 * This enum represents the three types of animation looping that Endimator supports.
	 * <p>{@link #NONE} for no looping.</p>
	 * <p>{@link #LOOP} to loop.</p>
	 * <p>{@link #HOLD} to hold on the last tick.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public enum LoopType {
		NONE, LOOP, HOLD
	}
}
