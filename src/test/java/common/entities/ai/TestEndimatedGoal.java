package common.entities.ai;

import com.minecraftabnormals.abnormals_core.core.annotations.Test;
import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatedGoal;
import common.entities.TestEndimatedEntity;

@Test
public class TestEndimatedGoal extends EndimatedGoal<TestEndimatedEntity> {

	public TestEndimatedGoal(TestEndimatedEntity entity) {
		super(entity, TestEndimatedEntity.GROW_ANIMATION);
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