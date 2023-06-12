package com.teamabnormals.blueprint.common.block.sign;

import com.teamabnormals.blueprint.common.block.entity.BlueprintHangingSignBlockEntity;
import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

import javax.annotation.Nullable;

/**
 * A {@link WallHangingSignBlock} extension used for Blueprint's wall hanging signs.
 */
public class BlueprintWallHangingSignBlock extends WallHangingSignBlock {

	public BlueprintWallHangingSignBlock(Properties properties, WoodType woodType) {
		super(properties, woodType);
		BlueprintHangingSignBlockEntity.VALID_BLOCKS.add(this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return BlueprintBlockEntityTypes.HANGING_SIGN.get().create(pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_277367_, BlockState p_277896_, BlockEntityType<T> p_277724_) {
		return createTickerHelper(p_277724_, BlueprintBlockEntityTypes.HANGING_SIGN.get(), SignBlockEntity::tick);
	}

}
