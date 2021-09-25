package com.minecraftabnormals.abnormals_core.common.blocks.sign;

import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class AbnormalsWallSignBlock extends WallSignBlock implements IAbnormalsSign {

	public AbnormalsWallSignBlock(Properties properties, WoodType woodType) {
		super(properties, woodType);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return ACTileEntities.SIGN.get().create();
	}

}