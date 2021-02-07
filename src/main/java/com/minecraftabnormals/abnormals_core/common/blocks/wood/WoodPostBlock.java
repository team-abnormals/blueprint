package com.minecraftabnormals.abnormals_core.common.blocks.wood;

import java.util.function.Supplier;
import com.minecraftabnormals.abnormals_core.core.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class WoodPostBlock extends StrippedWoodPostBlock {
	private final Supplier<Block> block;

	public WoodPostBlock(Supplier<Block> suppliedBlock, Properties properties) {
		super(properties);
		this.block = suppliedBlock;
	}
	
	@Override
	public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		if (toolType == ToolType.AXE)
			return block != null ? BlockUtil.transferAllBlockStates(state, this.block.get().getDefaultState()) : null;
		return super.getToolModifiedState(state, world, pos, player, stack, toolType);
	}

}