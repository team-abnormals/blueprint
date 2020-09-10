package com.teamabnormals.abnormals_core.core.endimator.entity;

import com.teamabnormals.abnormals_core.core.endimator.Endimation;
import com.teamabnormals.abnormals_core.core.util.NetworkUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.Random;

/**
 * A {@link Goal} that makes goals with Endimations easier.
 * @author SmellyModder(Luke Tonon)
 * @param <E> - The entity for the Goal.
 */
public abstract class EndimatedGoal<E extends Entity & IEndimatedEntity> extends Goal {
	protected final E entity;
	protected final Endimation endimation;
	protected final Random random;
	
	public EndimatedGoal(E entity, Endimation endimation) {
		this.entity = entity;
		this.endimation = endimation;
		this.random = new Random();
	}

	protected void playEndimation() {
		NetworkUtil.setPlayingAnimationMessage(this.entity, this.endimation);
	}

	protected void playEndimation(Endimation endimation) {
		NetworkUtil.setPlayingAnimationMessage(this.entity, endimation);
	}
	
	protected boolean isEndimationPlaying() {
		return this.entity.isEndimationPlaying(this.endimation);
	}

	protected boolean isEndimationPlaying(Endimation endimation) {
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