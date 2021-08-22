package com.minecraftabnormals.abnormals_core.common.blocks.chest;

import com.minecraftabnormals.abnormals_core.common.tileentity.AbnormalsTrappedChestTileEntity;
import com.minecraftabnormals.abnormals_core.core.api.IChestBlock;
import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class AbnormalsTrappedChestBlock extends ChestBlock implements IChestBlock {
	public final String type;

	public AbnormalsTrappedChestBlock(String type, Properties props) {
		super(props, () -> ACTileEntities.TRAPPED_CHEST.get());
		this.type = type;
	}

	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return false;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new AbnormalsTrappedChestTileEntity();
	}

	@Override
	public String getChestType() {
		return this.type;
	}

	@Override
	protected Stat<ResourceLocation> getOpenChestStat() {
		return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return MathHelper.clamp(ChestTileEntity.getOpenCount(world, pos), 0, 15);
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return face == Direction.UP ? state.getSignal(world, pos, face) : 0;
	}
}
