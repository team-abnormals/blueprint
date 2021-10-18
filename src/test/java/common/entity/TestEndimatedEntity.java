package common.entity;

import com.teamabnormals.blueprint.core.endimator.Endimation;
import com.teamabnormals.blueprint.core.endimator.entity.EndimatedEntity;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import core.BlueprintTest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class TestEndimatedEntity extends EndimatedEntity {
	public static final Endimation SINK_ANIMATION = new Endimation(20);
	public static final Endimation GROW_ANIMATION = new Endimation(20);
	public static final Endimation DEATH_ANIMATION = new Endimation(BlueprintTest.REGISTRY_HELPER.prefix("death_test"), 30);

	public TestEndimatedEntity(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level.getGameTime() % 40 == 0 && this.isEffectiveAi()) {
			if (this.random.nextFloat() < 0.5F) {
				NetworkUtil.setPlayingAnimationMessage(this, SINK_ANIMATION);
			} else {
				NetworkUtil.setPlayingAnimationMessage(this, GROW_ANIMATION);
			}
		}
//		Funnies ;)
//		if(this.world.getGameTime() % 40 == 0 && this.isServerWorld()) {
//			BlockPos pos = this.getPosition();
//			int x = pos.getX();
//			int y = pos.getY();
//			int z = pos.getZ();
//			GenerationUtils.fillAreaWithBlockCube(this.world, this.getRNG(), x - 2, y - 1, z - 2, x + 2, y + 1, z + 2, GenerationUtils.IS_AIR, new WeightedStateEntry(Blocks.ANVIL.getDefaultState(), 1), new WeightedStateEntry(Blocks.BEDROCK.getDefaultState(), 4), new WeightedStateEntry(Blocks.BIRCH_WOOD.getDefaultState(), 8));
//		}
	}

	@Override
	public Endimation getDeathAnimation() {
		return DEATH_ANIMATION;
	}

	@Override
	public Endimation[] getEndimations() {
		return new Endimation[]{
				SINK_ANIMATION,
				GROW_ANIMATION,
				DEATH_ANIMATION
		};
	}
}