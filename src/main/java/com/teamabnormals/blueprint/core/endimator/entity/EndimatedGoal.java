package com.teamabnormals.blueprint.core.endimator.entity;

import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimation;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.Random;

/**
 * A {@link Goal} extension that eases the creation of animated goals.
 *
 * @param <E> The type of {@link Endimatable} entity.
 * @author SmellyModder (Luke Tonon)
 */
public abstract class EndimatedGoal<E extends Entity & Endimatable> extends Goal {
	protected final E entity;
	protected final PlayableEndimation endimation;
	protected final Random random;

	public EndimatedGoal(E entity, PlayableEndimation endimation) {
		this.entity = entity;
		this.endimation = endimation;
		this.random = new Random();
	}

	protected void playEndimation() {
		NetworkUtil.setPlayingAnimation(this.entity, this.endimation);
	}

	protected void playEndimation(PlayableEndimation endimation) {
		NetworkUtil.setPlayingAnimation(this.entity, endimation);
	}

	protected boolean isEndimationPlaying() {
		return this.entity.isEndimationPlaying(this.endimation);
	}

	protected boolean isEndimationPlaying(PlayableEndimation endimation) {
		return this.entity.isEndimationPlaying(endimation);
	}

	protected boolean isNoEndimationPlaying() {
		return this.entity.isNoEndimationPlaying();
	}

	protected boolean isEndimationAtTick(int tick) {
		return this.entity.getAnimationTick() == tick;
	}

	protected boolean isEndimationPastTick(int tick) {
		return this.entity.getAnimationTick() > tick;
	}

	protected boolean isEndimationPastOrAtTick(int tick) {
		return this.entity.getAnimationTick() >= tick;
	}

	protected boolean isEndimationBeforeTick(int tick) {
		return this.entity.getAnimationTick() < tick;
	}

	protected boolean isEndimationBeforeOrAtTick(int tick) {
		return this.entity.getAnimationTick() <= tick;
	}
}