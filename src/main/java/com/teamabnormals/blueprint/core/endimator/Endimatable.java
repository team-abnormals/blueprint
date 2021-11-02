package com.teamabnormals.blueprint.core.endimator;

import com.teamabnormals.blueprint.core.endimator.effects.EndimationEffectHandler;
import com.teamabnormals.blueprint.core.endimator.effects.EndimationEffectSource;
import com.teamabnormals.blueprint.core.endimator.effects.EndimationEffects;

/**
 * An interface representing an object that can process updates for {@link PlayableEndimation} instances.
 * <p>This gets mixin'd into {@link net.minecraft.world.entity.Entity}.</p>
 * <p>All methods in this interface are {@code default} because they have default behaviors or get mixin'd into {@link net.minecraft.world.entity.Entity}.</p>
 * <p>When implementing on classes not extending {@link net.minecraft.world.entity.Entity}, {@link #getEndimatedState()} must be overridden.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see com.teamabnormals.blueprint.core.mixin.EntityMixin
 * @see PlayableEndimation
 * @see EndimationEffects
 */
public interface Endimatable extends EndimationEffectSource {
	/**
	 * Gets or creates an {@link EndimatedState} that holds information about the object's current state of animation.
	 * <p><b>This must get overridden for implementations not extending {@link net.minecraft.world.entity.Entity}.</b></p>
	 *
	 * @return An {@link EndimatedState} that holds information about the object's current state of animation.
	 */
	default EndimatedState getEndimatedState() {
		throw new IllegalStateException("Not implemented!");
	}

	/**
	 * Gets the {@link PlayableEndimation} currently playing.
	 *
	 * @return The {@link PlayableEndimation} currently playing.
	 * @see EndimatedState#endimation
	 */
	default PlayableEndimation getPlayingEndimation() {
		return this.getEndimatedState().endimation;
	}

	/**
	 * Sets a new currently playing {@link PlayableEndimation}.
	 * <p>The {@link EndimatedState#animationTick} will get reset to 0.</p>
	 * <p>{@link #onEndimationEnd(PlayableEndimation, PlayableEndimation)} will get called for the previously playing {@link PlayableEndimation}.</p>
	 * <p>{@link #onEndimationStart(PlayableEndimation, PlayableEndimation)} will get called for the new {@link PlayableEndimation}.</p>
	 *
	 * @param endimationToPlay The new {@link PlayableEndimation} to play.
	 */
	default void setPlayingEndimation(PlayableEndimation endimationToPlay) {
		EndimatedState state = this.getEndimatedState();
		PlayableEndimation ending = state.endimation;
		if (ending != endimationToPlay) {
			state.effectHandler.reset();
		}
		this.onEndimationEnd(ending, endimationToPlay);
		this.onEndimationStart(endimationToPlay, ending);
		state.endimation = endimationToPlay;
		state.animationTick = 0;
	}

	/**
	 * Gets the tick the currently playing {@link PlayableEndimation} is at.
	 * <p>The tick value should always be positive.</p>
	 *
	 * @return The tick the currently playing {@link PlayableEndimation} is at.
	 * @see EndimatedState#animationTick
	 */
	default int getAnimationTick() {
		return this.getEndimatedState().animationTick;
	}

	/**
	 * Sets the {@link EndimatedState#animationTick} to a given value.
	 *
	 * @param animationTick The tick to set it at.
	 * @see EndimatedState#animationTick
	 */
	default void setAnimationTick(int animationTick) {
		this.getEndimatedState().animationTick = animationTick;
	}

	/**
	 * Gets the {@link EndimationEffectHandler} used for processing the playing {@link PlayableEndimation}.
	 *
	 * @return The {@link EndimationEffectHandler} used for processing the playing {@link PlayableEndimation}.
	 */
	default EndimationEffectHandler getEffectHandler() {
		return this.getEndimatedState().effectHandler;
	}

	/**
	 * Called when an {@link PlayableEndimation} is assigned as the new playing animation.
	 *
	 * @param endimation    The {@link PlayableEndimation} that just started.
	 * @param oldEndimation The {@link PlayableEndimation} that was previously assigned.
	 */
	default void onEndimationStart(PlayableEndimation endimation, PlayableEndimation oldEndimation) {
	}

	/**
	 * Called when the currently playing {@link PlayableEndimation} is unassigned as the playing animation.
	 *
	 * @param endimation    The {@link PlayableEndimation} that just ended.
	 * @param newEndimation The {@link PlayableEndimation} that just got assigned. This is equal to {@code endimation} if it loops.
	 */
	default void onEndimationEnd(PlayableEndimation endimation, PlayableEndimation newEndimation) {
	}

	/**
	 * Resets the currently playing {@link PlayableEndimation}.
	 * <p>Calls {@link #setPlayingEndimation(PlayableEndimation)}, using {@link PlayableEndimation#BLANK} as the new playing {@link PlayableEndimation}.</p>
	 */
	default void resetEndimation() {
		this.setPlayingEndimation(PlayableEndimation.BLANK);
	}

	/**
	 * Updates the internals of the interface.
	 */
	default void endimateTick() {
		EndimatedState endimatedState = this.getEndimatedState();
		PlayableEndimation endimation = endimatedState.endimation;
		if (endimation != PlayableEndimation.BLANK) {
			int duration = endimation.duration();
			if (++endimatedState.animationTick >= duration) {
				PlayableEndimation.LoopType loopType = endimation.loopType();
				if (loopType == PlayableEndimation.LoopType.LOOP) {
					this.setPlayingEndimation(endimation);
				} else if (loopType == PlayableEndimation.LoopType.HOLD) {
					endimatedState.animationTick = duration;
				} else {
					this.resetEndimation();
				}
			}
		}
	}

	/**
	 * Checks if the currently playing {@link PlayableEndimation} is {@link PlayableEndimation#BLANK}.
	 *
	 * @return If the currently playing {@link PlayableEndimation} is {@link PlayableEndimation#BLANK}.
	 */
	default boolean isNoEndimationPlaying() {
		return this.getPlayingEndimation() == PlayableEndimation.BLANK;
	}

	/**
	 * Checks if the currently playing {@link PlayableEndimation} is equal to a given {@link PlayableEndimation}.
	 *
	 * @return If the currently playing {@link PlayableEndimation} is equal to a given {@link PlayableEndimation}.
	 */
	default boolean isEndimationPlaying(PlayableEndimation endimation) {
		return this.getPlayingEndimation() == endimation;
	}

	/**
	 * A simple class that represents the state of an {@link Endimatable} object.
	 * <p>Having to declare all the fields used in this class manually is tedious for custom {@link Endimatable} implementations, which is why this class exists.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	final class EndimatedState {
		public final EndimationEffectHandler effectHandler;
		public int animationTick;
		public PlayableEndimation endimation = PlayableEndimation.BLANK;

		public EndimatedState(Endimatable endimatable) {
			this.effectHandler = new EndimationEffectHandler(endimatable);
		}
	}
}
