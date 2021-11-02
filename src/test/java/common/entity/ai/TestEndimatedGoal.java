package common.entity.ai;

import com.teamabnormals.blueprint.core.endimator.entity.EndimatedGoal;
import common.entity.TestEndimatedEntity;
import core.registry.TestEndimations;

public class TestEndimatedGoal extends EndimatedGoal<TestEndimatedEntity> {

	public TestEndimatedGoal(TestEndimatedEntity entity) {
		super(entity, TestEndimations.ROTATE);
	}

	@Override
	public boolean canUse() {
		return this.entity.isInLava();
	}

	@Override
	public void start() {
		this.playEndimation();
	}

	@Override
	public boolean canContinueToUse() {
		return this.isEndimationPlaying();
	}

}