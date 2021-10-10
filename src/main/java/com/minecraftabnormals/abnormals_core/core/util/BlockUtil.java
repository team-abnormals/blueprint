package com.minecraftabnormals.abnormals_core.core.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.core.BlockSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * @author - SmellyModder(Luke Tonon)
 */
public final class BlockUtil {

	public static boolean isBlockInWater(Level world, BlockPos pos) {
		if (world.getBlockState(pos).getFluidState().is(FluidTags.WATER)) {
			return true;
		}
		for (Direction direction : Direction.values()) {
			if (world.getFluidState(pos.relative(direction)).is(FluidTags.WATER)) {
				return true;
			}
		}
		return false;
	}

	public static boolean canPlace(Level world, Player player, BlockPos pos, BlockState state) {
		CollisionContext selectionContext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
		VoxelShape voxelshape = state.getCollisionShape(world, pos, selectionContext);
		VoxelShape offsetShape = world.getBlockState(pos).getCollisionShape(world, pos);
		return (offsetShape.isEmpty() || world.getBlockState(pos).getMaterial().isReplaceable()) && state.canSurvive(world, pos) && world.isUnobstructed(null, voxelshape.move(pos.getX(), pos.getY(), pos.getZ()));
	}

	public static SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, Player entity) {
		return state.getSoundType(world, pos, entity).getPlaceSound();
	}

	public static boolean isPosNotTouchingBlock(LevelAccessor world, BlockPos pos, Block blockToCheck, Direction... blacklistedDirections) {
		for (Direction directions : Direction.values()) {
			List<Direction> blacklistedDirectionsList = Arrays.asList(blacklistedDirections);
			if (!blacklistedDirectionsList.contains(directions)) {
				if (world.getBlockState(pos.relative(directions)).getBlock() == blockToCheck) {
					return false;
				}
			}
		}
		return true;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static BlockState transferAllBlockStates(BlockState initial, BlockState after) {
		BlockState block = after;
		for (Property property : initial.getBlock().getStateDefinition().getProperties()) {
			if (after.hasProperty(property) && initial.getValue(property) != null) {
				block = block.setValue(property, initial.getValue(property));
			}
		}
		return block;
	}

	public static AABB rotateHorizontalBB(AABB bb, BBRotation rotation) {
		return rotation.rotateBB(bb);
	}

	public enum BBRotation {
		REVERSE_X((bb) -> {
			final float minX = 1.0F - (float) bb.maxX;
			return new AABB(minX, bb.minY, bb.minZ, bb.maxX >= 1.0F ? bb.maxX - bb.minX : bb.maxX + minX, bb.maxY, bb.maxZ);
		}),
		REVERSE_Z((bb) -> {
			final float minZ = 1.0F - (float) bb.maxZ;
			return new AABB(bb.minX, bb.minY, minZ, bb.maxX, bb.maxY, bb.maxZ >= 1.0F ? bb.maxZ - bb.minZ : bb.maxZ + minZ);
		}),
		RIGHT((bb) -> {
			return new AABB(bb.minZ, bb.minY, bb.minX, bb.maxZ, bb.maxY, bb.maxX);
		}),
		LEFT((bb) -> {
			return REVERSE_X.rotateBB(RIGHT.rotateBB(bb));
		});

		private final UnaryOperator<AABB> modifier;

		BBRotation(UnaryOperator<AABB> modifier) {
			this.modifier = modifier;
		}

		public AABB rotateBB(AABB bb) {
			return this.modifier.apply(bb);
		}

		public static BBRotation getRotationForDirection(Direction currentDirection, Direction startingDirection) {
			int currentIndex = currentDirection.get3DDataValue() - 2;
			int startingIndex = startingDirection.get3DDataValue() - 2;
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
	 * @param source The {@link BlockSource} to get the position from.
	 * @return The position in front of the dispenser's output face.
	 *
	 * @author abigailfails
	 */
	public static BlockPos offsetPos(BlockSource source) {
		return source.getPos().relative(source.getBlockState().getValue(DirectionalBlock.FACING));
	}

	/**
	 * Gets the {@link BlockState} at the position returned by {@link #offsetPos(BlockSource source)}.
	 *
	 * @param source The {@link BlockSource} to get the position from.
	 * @return The {@link BlockState} at the offset position.
	 * @see #offsetPos(BlockSource source)
	 */
	public static BlockState getStateAtOffsetPos(BlockSource source) {
		return source.getLevel().getBlockState(offsetPos(source));
	}

	/**
	 * Gets a {@link List} of type {@link T} at the position returned by {@link #offsetPos(BlockSource source)}.
	 *
	 * @param source The {@link BlockSource} to get the position from.
	 * @param entityType The class extending {@link T} to search for. Set to {@code Entity.class} to get all entities, regardless of type.
	 *
	 * @return A {@link List} of entities at the at the offset position.
	 * @see #offsetPos(BlockSource source)
	 */
	public static<T extends Entity> List<T> getEntitiesAtOffsetPos(BlockSource source, Class<T> entityType) {
		return source.getLevel().getEntitiesOfClass(entityType, new AABB(offsetPos(source)));
	}

	/**
	 * Gets a {@link List} of type {@link T} that match a {@link Predicate} at the position returned by {@link #offsetPos(BlockSource source)}.
	 *
	 * @param source The {@link BlockSource} to get the position from.
	 * @param entityType The class extending {@link T} to search for. Set to {@code Entity.class} to get all entities, regardless of type.
	 * @param predicate The predicate that takes a superclass of {@link T} as an argument to check against.
	 *
	 * @return A {@link List} of entities at the at the offset position.
	 * @see #offsetPos(BlockSource source)
	 */
	public static<T extends Entity> List<T> getEntitiesAtOffsetPos(BlockSource source, Class<T> entityType, Predicate<? super T> predicate) {
		return source.getLevel().getEntitiesOfClass(entityType, new AABB(offsetPos(source)), predicate);
	}
}
