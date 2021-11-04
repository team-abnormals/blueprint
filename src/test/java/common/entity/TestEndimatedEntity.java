package common.entity;

import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.endimator.TimedEndimation;
import com.teamabnormals.blueprint.core.endimator.effects.EndimationEffectHandler;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import common.entity.ai.TestEndimatedGoal;
import core.registry.TestEndimations;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TestEndimatedEntity extends PathfinderMob implements Endimatable {
	@OnlyIn(Dist.CLIENT)
	public EndimationEffectHandler idleEffectHandler;
	public TimedEndimation hurt = new TimedEndimation(5, 0);

	public TestEndimatedEntity(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		if (level.isClientSide) {
			this.idleEffectHandler = new EndimationEffectHandler(this);
		}
		this.hurt.setDecrementing(true);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new TestEndimatedGoal(this));
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isNoEndimationPlaying()) {
			NetworkUtil.setPlayingAnimation(this, this.random.nextBoolean() ? TestEndimations.HOVER : this.random.nextBoolean() ? TestEndimations.SINK : TestEndimations.COMPLEX);
		}
		if (this.level.isClientSide) {
			TimedEndimation hurt = this.hurt;
			hurt.tick();
			if (hurt.isMaxed()) {
				hurt.setDecrementing(true);
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (super.hurt(source, amount)) {
			this.level.broadcastEntityEvent(this, (byte) 8);
			return true;
		}
		return false;
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 8) {
			this.hurt.setDecrementing(false);
		} else {
			super.handleEntityEvent(id);
		}
	}
}