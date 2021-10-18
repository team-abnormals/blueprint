package com.teamabnormals.blueprint.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

/**
 * A {@link Block} extension that has the same enchantment power bonus as the vanilla bookshelf.
 */
public class BookshelfBlock extends Block {

	public BookshelfBlock(Properties properties) {
		super(properties);
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
		return 1.0F;
	}

}