package com.teamabnormals.abnormals_core.common.items;

import javax.annotation.Nullable;

import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsSignTileEntity;
import com.teamabnormals.abnormals_core.core.utils.NetworkUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AbnormalsSignItem extends WallOrFloorItem {
	
	public AbnormalsSignItem(Item.Properties propertiesIn, Block floorBlockIn, Block wallBlockIn) {
		super(floorBlockIn, wallBlockIn, propertiesIn);
	}
	
	protected boolean onBlockPlaced(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
		boolean flag = super.onBlockPlaced(pos, world, player, stack, state);
		if(!world.isRemote && !flag && player != null) {
			NetworkUtil.openSignEditor(player, (AbnormalsSignTileEntity) world.getTileEntity(pos));
		}

		return flag;
	}

}