package com.teamabnormals.blueprint.common.block;

import com.teamabnormals.blueprint.core.util.BlockUtil;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A {@link RotatedPillarBlock} extension that fills its item after the latest vanilla log item.
 */
public class LogBlock extends RotatedPillarBlock {
	private final Supplier<Block> strippedBlock;

	public LogBlock(Supplier<Block> strippedBlock, Properties properties) {
		super(properties);
		this.strippedBlock = strippedBlock;
	}

	@Override
	@Nullable
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		if (ItemAbilities.AXE_STRIP == itemAbility)
			return this.strippedBlock != null ? BlockUtil.transferAllBlockStates(state, this.strippedBlock.get().defaultBlockState()) : null;
		return super.getToolModifiedState(state, context, itemAbility, simulate);
	}
}