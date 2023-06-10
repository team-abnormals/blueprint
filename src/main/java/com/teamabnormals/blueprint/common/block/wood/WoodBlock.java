package com.teamabnormals.blueprint.common.block.wood;

import com.teamabnormals.blueprint.core.util.BlockUtil;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A {@link RotatedPillarBlock} extension that fills its item after the latest vanilla wood item.
 */
public class WoodBlock extends RotatedPillarBlock {
	private final Supplier<Block> block;

	public WoodBlock(Supplier<Block> strippedBlock, Properties properties) {
		super(properties);
		this.block = strippedBlock;
	}

	@Override
	@Nullable
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction action, boolean simulate) {
		if (action == ToolActions.AXE_STRIP)
			return this.block != null ? BlockUtil.transferAllBlockStates(state, this.block.get().defaultBlockState()) : null;
		return super.getToolModifiedState(state, context, action, simulate);
	}
}