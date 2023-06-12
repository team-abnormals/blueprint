package com.teamabnormals.blueprint.common.block.sign;

import com.teamabnormals.blueprint.common.block.entity.BlueprintSignBlockEntity;
import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

import javax.annotation.Nullable;

/**
 * A {@link StandingSignBlock} extension used for Blueprint's standing signs.
 */
public class BlueprintStandingSignBlock extends StandingSignBlock {

	public BlueprintStandingSignBlock(Properties properties, WoodType woodType) {
		super(properties, woodType);
		BlueprintSignBlockEntity.VALID_BLOCKS.add(this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return BlueprintBlockEntityTypes.SIGN.get().create(pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_277367_, BlockState p_277896_, BlockEntityType<T> p_277724_) {
		return createTickerHelper(p_277724_, BlueprintBlockEntityTypes.SIGN.get(), SignBlockEntity::tick);
	}

}