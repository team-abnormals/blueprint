package com.teamabnormals.abnormals_core.common.entity.ai;

import com.teamabnormals.abnormals_core.common.entity.ExampleEndimatedEntity;
import com.teamabnormals.abnormals_core.core.library.Test;
import com.teamabnormals.abnormals_core.core.library.endimator.entity.EndimatedGoal;
import com.teamabnormals.abnormals_core.core.library.endimator.Endimation;

@Test
public class ExampleEndimatedGoal extends EndimatedGoal<ExampleEndimatedEntity> {

	public ExampleEndimatedGoal(ExampleEndimatedEntity entity) {
		super(entity);
	}

	@Override
	protected Endimation getEndimation() {
		return ExampleEndimatedEntity.GROW_ANIMATION;
	}

	@Override
	public boolean shouldExecute() {
		return this.entity.isInLava();
	}
	
	@Override
	public void startExecuting() {
		this.playEndimation();
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		return this.isEndimationPlaying();
	}

}