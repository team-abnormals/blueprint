package com.teamabnormals.abnormals_core.common.entity;

import com.teamabnormals.abnormals_core.core.library.endimator.Endimation;
import com.teamabnormals.abnormals_core.core.library.endimator.entity.EndimatedEntity;
import com.teamabnormals.abnormals_core.core.utils.NetworkUtil;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class ExampleEndimatedEntity extends EndimatedEntity {
	public static final Endimation SINK_ANIMATION = new Endimation(20);
	public static final Endimation GROW_ANIMATION = new Endimation(20);
	public static final Endimation DEATH_ANIMATION = new Endimation(30);

	public ExampleEndimatedEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	@Override
	public void tick() {
		super.tick();
//		if(this.world.getGameTime() % 40 == 0 && this.isServerWorld()) {
//			if(this.getRNG().nextFloat() < 0.5F) {
//				NetworkUtil.setPlayingAnimationMessage(this, SINK_ANIMATION);
//			} else {
//				NetworkUtil.setPlayingAnimationMessage(this, GROW_ANIMATION);
//			}
//		}
	}
	
	@Override
	public Endimation getDeathAnimation() {
		return DEATH_ANIMATION;
	}

	@Override
	public Endimation[] getEndimations() {
		return new Endimation[] {
			SINK_ANIMATION,
			GROW_ANIMATION,
			DEATH_ANIMATION
		};
	}
}