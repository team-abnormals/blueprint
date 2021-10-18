package com.teamabnormals.blueprint.common.block.sign;

import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * A {@link WallSignBlock} extension used for Blueprint's wall signs.
 */
public class AbnormalsWallSignBlock extends WallSignBlock implements IAbnormalsSign {

	public AbnormalsWallSignBlock(Properties properties, WoodType woodType) {
		super(properties, woodType);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return BlueprintBlockEntityTypes.SIGN.get().create(pos, state);
	}

}