package com.teamabnormals.abnormals_core.core.utils;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IProperty;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

/** 
 * @author - SmellyModder(Luke Tonon)
 */
public class BlockUtils {

	public static boolean isBlockInWater(World world, BlockPos pos) {
		if(world.getBlockState(pos).getFluidState().isTagged(FluidTags.WATER)) {
			return true;
		}
		for(Direction direction : Direction.values()) {
			if(world.getFluidState(pos.offset(direction)).isTagged(FluidTags.WATER)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean canPlace(World world, PlayerEntity player, BlockPos pos, BlockState state) {
		ISelectionContext selectionContext = player == null ? ISelectionContext.dummy() : ISelectionContext.forEntity(player);
		VoxelShape voxelshape = state.getCollisionShape(world, pos, selectionContext);
		VoxelShape offsetShape = world.getBlockState(pos).getCollisionShape(world, pos);
		return (offsetShape.isEmpty() || world.getBlockState(pos).getMaterial().isReplaceable()) && state.isValidPosition(world, pos) && world.checkNoEntityCollision(null, voxelshape.withOffset(pos.getX(), pos.getY(), pos.getZ()));
	}
	
	public static SoundEvent getPlaceSound(BlockState state, World world, BlockPos pos, PlayerEntity entity) {
		return state.getSoundType(world, pos, entity).getPlaceSound();
	}
	
	public static boolean isPosNotTouchingBlock(IWorld world, BlockPos pos, Block blockToCheck, Direction... blacklistedDirections) {
		for(Direction directions : Direction.values()) {
			List<Direction> blacklistedDirectionsList = Arrays.asList(blacklistedDirections);
			if(!blacklistedDirectionsList.contains(directions)) {
				if(world.getBlockState(pos.offset(directions)).getBlock() == blockToCheck) {
					return false;
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static BlockState transferAllBlockStates(BlockState initial, BlockState after) {
		for(IProperty property : initial.getBlock().getStateContainer().getProperties()) {
			after = after.with(property, initial.get(property));
		}
		return after;
	}
	
}
