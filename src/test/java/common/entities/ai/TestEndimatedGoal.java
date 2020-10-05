package common.entities.ai;

import com.teamabnormals.abnormals_core.core.annotations.Test;
import com.teamabnormals.abnormals_core.core.endimator.entity.EndimatedGoal;
import common.entities.TestEndimatedEntity;

@Test
public class TestEndimatedGoal extends EndimatedGoal<TestEndimatedEntity> {

	public TestEndimatedGoal(TestEndimatedEntity entity) {
		super(entity, TestEndimatedEntity.GROW_ANIMATION);
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