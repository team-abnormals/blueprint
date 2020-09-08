package com.teamabnormals.abnormals_core.common.blocks;

import javax.annotation.Nullable;

import com.teamabnormals.abnormals_core.core.registry.ACTileEntities;
import com.teamabnormals.abnormals_core.core.util.ItemStackUtil;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.world.IBlockReader;

public class AbnormalsBeehiveBlock extends BeehiveBlock {
	public AbnormalsBeehiveBlock(Properties properties) {
		super(properties);
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ACTileEntities.BEEHIVE.get().create();
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		ItemStackUtil.fillAfterItemForGroup(this.asItem(), Items.BEEHIVE, group, items);
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return mirrorIn == Mirror.NONE ? state : state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}
}