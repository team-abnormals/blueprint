package com.teamabnormals.abnormals_core.common.entity;

import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.library.endimator.Endimation;
import com.teamabnormals.abnormals_core.core.library.endimator.EndimationDataManager;
import com.teamabnormals.abnormals_core.core.library.endimator.EndimationDataManager.EndimationInstruction;
import com.teamabnormals.abnormals_core.core.library.endimator.entity.EndimatedEntity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class ExampleEndimatedEntity extends EndimatedEntity {
	public static final Endimation SINK_ANIMATION = new Endimation(20);
	public static final Endimation GROW_ANIMATION = new Endimation(20);
	public static final Endimation DEATH_ANIMATION = new Endimation(AbnormalsCore.REGISTRY_HELPER.prefix("death/example"), 30);

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
//		Funnies ;)
//		if(this.world.getGameTime() % 40 == 0 && this.isServerWorld()) {
//			BlockPos pos = this.getPosition();
//			int x = pos.getX();
//			int y = pos.getY();
//			int z = pos.getZ();
//			GenerationUtils.fillAreaWithBlockCube(this.world, this.getRNG(), x - 2, y - 1, z - 2, x + 2, y + 1, z + 2, GenerationUtils.IS_AIR, new BlockPlacementEntry(Blocks.ANVIL.getDefaultState(), 1), new BlockPlacementEntry(Blocks.BEDROCK.getDefaultState(), 4), new BlockPlacementEntry(Blocks.BIRCH_WOOD.getDefaultState(), 8));
//		}
		if(!this.world.isRemote) {
			EndimationDataManager.ENDIMATIONS.forEach((in, out) -> {
				for(EndimationInstruction insns : out.getInstructions()) {
					System.out.println(insns.type);
					System.out.println(insns.tickLength);
				}
			});
		}
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