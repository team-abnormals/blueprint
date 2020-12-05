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
	public final String modid;
	public final String type;

	public AbnormalsTrappedChestBlock(String modid, String type, Properties props) {
		super(props, () -> ACTileEntities.TRAPPED_CHEST.get());
		this.modid = modid;
		this.type = type;
	}

	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new AbnormalsTrappedChestTileEntity();
	}

	@Override
	public String getChestName() {
		return type;
	}

	@Override
	public String getModid() {
		return modid;
	}

	@Override
	protected Stat<ResourceLocation> getOpenStat() {
		return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
	}

	@Override
	public boolean canProvidePower(BlockState p_149744_1_) {
		return true;
	}

	@Override
	public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
		return MathHelper.clamp(ChestTileEntity.getPlayersUsing(p_180656_2_, p_180656_3_), 0, 15);
	}

	@Override
	public int getStrongPower(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
		return p_176211_4_ == Direction.UP ? p_176211_1_.getWeakPower(p_176211_2_, p_176211_3_, p_176211_4_) : 0;
	}

	@Override
	public boolean isTrapped() {
		return true;
	}
}
