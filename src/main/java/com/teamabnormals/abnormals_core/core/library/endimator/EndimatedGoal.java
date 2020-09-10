package com.teamabnormals.abnormals_core.core.library.endimator;

import com.teamabnormals.abnormals_core.core.library.endimator.entity.IEndimatedEntity;
import com.teamabnormals.abnormals_core.core.utils.NetworkUtil;

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
	protected final Random random;
	
	public EndimatedGoal(E entity) {
		this.entity = entity;
		this.random = new Random();
	}

	protected void playEndimation() {
		NetworkUtil.setPlayingAnimationMessage(this.entity, this.getEndimation());
	}

	protected void playEndimation(Endimation endimation) {
		NetworkUtil.setPlayingAnimationMessage(this.entity, endimation);
	}
	
	protected boolean isEndimationPlaying() {
		return this.entity.isEndimationPlaying(this.getEndimation());
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

	/*
	 * Converted to field in AC 1.16.2 - Breaking Change
	 */
	protected abstract Endimation getEndimation();
}