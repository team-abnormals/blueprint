package com.minecraftabnormals.abnormals_core.core.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.Property;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

/**
 * @author - SmellyModder(Luke Tonon)
 */
public final class BlockUtil {

	public static boolean isBlockInWater(World world, BlockPos pos) {
		if (world.getBlockState(pos).getFluidState().isTagged(FluidTags.WATER)) {
			return true;
		}
		for (Direction direction : Direction.values()) {
			if (world.getFluidState(pos.offset(direction)).isTagged(FluidTags.WATER)) {
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
		for (Direction directions : Direction.values()) {
			List<Direction> blacklistedDirectionsList = Arrays.asList(blacklistedDirections);
			if (!blacklistedDirectionsList.contains(directions)) {
				if (world.getBlockState(pos.offset(directions)).getBlock() == blockToCheck) {
					return false;
				}
			}
		}
		return true;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static BlockState transferAllBlockStates(BlockState initial, BlockState after) {
		BlockState block = after;
		for (Property property : initial.getBlock().getStateContainer().getProperties()) {
			if (after.hasProperty(property) && initial.get(property) != null) {
				block = block.with(property, initial.get(property));
			}
		}
		return block;
	}

	public static AxisAlignedBB rotateHorizontalBB(AxisAlignedBB bb, BBRotation rotation) {
		AxisAlignedBB newBB = bb;
		return rotation.rotateBB(newBB);
	}

	public enum BBRotation {
		REVERSE_X((bb) -> {
			final float minX = 1.0F - (float) bb.maxX;
			return new AxisAlignedBB(minX, bb.minY, bb.minZ, bb.maxX >= 1.0F ? bb.maxX - bb.minX : bb.maxX + minX, bb.maxY, bb.maxZ);
		}),
		REVERSE_Z((bb) -> {
			final float minZ = 1.0F - (float) bb.maxZ;
			return new AxisAlignedBB(bb.minX, bb.minY, minZ, bb.maxX, bb.maxY, bb.maxZ >= 1.0F ? bb.maxZ - bb.minZ : bb.maxZ + minZ);
		}),
		RIGHT((bb) -> {
			return new AxisAlignedBB(bb.minZ, bb.minY, bb.minX, bb.maxZ, bb.maxY, bb.maxX);
		}),
		LEFT((bb) -> {
			return REVERSE_X.rotateBB(RIGHT.rotateBB(bb));
		});

		private final UnaryOperator<AxisAlignedBB> modifier;

		BBRotation(UnaryOperator<AxisAlignedBB> modifier) {
			this.modifier = modifier;
		}

		public AxisAlignedBB rotateBB(AxisAlignedBB bb) {
			return this.modifier.apply(bb);
		}

		public static BBRotation getRotationForDirection(Direction currentDirection, Direction startingDirection) {
			int currentIndex = currentDirection.getIndex() - 2;
			int startingIndex = startingDirection.getIndex() - 2;
			int index = (currentIndex - startingIndex) % 4;

			switch (index) {
				default:
				case 0:
					return BBRotation.REVERSE_X;
				case 1:
					return BBRotation.REVERSE_Z;
				case 2:
					return BBRotation.RIGHT;
				case 3:
					return BBRotation.LEFT;
			}
		}
	}

	/**
	 * <p>Returns the {@link BlockPos} offset by 1 in the direction of {@code source}'s {@link BlockState}'s
	 * {@link DirectionalBlock#FACING} property.</p>
	 * This requires the {@link BlockState} stored in {@code source} to have a {@link DirectionalBlock#FACING} property.
	 *
	 * @param source The {@link IBlockSource} to get the position from.
	 * @return The position in front of the dispenser's output face.
	 *
	 * @author abigailfails
	 */
	public static BlockPos offsetPos(IBlockSource source) {
		return source.getBlockPos().offset(source.getBlockState().get(DirectionalBlock.FACING));
	}

	/**
	 * Gets the {@link BlockState} at the position returned by {@link #offsetPos(IBlockSource source)}.
	 *
	 * @param source The {@link IBlockSource} to get the position from.
	 * @return The {@link BlockState} at the offset position.
	 * @see #offsetPos(IBlockSource source)
	 */
	public static BlockState stateAtOffsetPos(IBlockSource source) {
		return source.getWorld().getBlockState(offsetPos(source));
	}

	/**
	 * Gets a {@link List} of entities at the position returned by {@link #offsetPos(IBlockSource source)}.
	 *
	 * @param source The {@link IBlockSource} to get the position from.
	 * @param entityType The class extending {@link Entity} to search for. Set to {@code Entity.class} to get all entities, regardless of type.
	 *
	 * @return A {@link List} of entities at the at the offset position.
	 * @see #offsetPos(IBlockSource source)
	 */
	public static List<Entity> entitiesAtOffsetPos(IBlockSource source, Class<Entity> entityType) {
		return source.getWorld().getEntitiesWithinAABB(entityType, new AxisAlignedBB(offsetPos(source)));
	}
}
