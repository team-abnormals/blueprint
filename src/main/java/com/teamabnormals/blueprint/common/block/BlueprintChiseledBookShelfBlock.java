package com.teamabnormals.blueprint.common.block;

import com.teamabnormals.blueprint.common.block.entity.BlueprintChiseledBookShelfBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlueprintChiseledBookShelfBlock extends ChiseledBookShelfBlock {

	public BlueprintChiseledBookShelfBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BlueprintChiseledBookShelfBlockEntity(pos, state);
	}

}
