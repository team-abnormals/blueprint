package com.minecraftabnormals.abnormals_core.common.blocks.sign;

import com.minecraftabnormals.abnormals_core.core.registry.ACBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * A {@link StandingSignBlock} extension used for Abnormals Core's standing signs.
 */
public class AbnormalsStandingSignBlock extends StandingSignBlock implements IAbnormalsSign {

	public AbnormalsStandingSignBlock(Properties properties, WoodType woodType) {
		super(properties, woodType);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ACBlockEntities.SIGN.get().create(pos, state);
	}

}