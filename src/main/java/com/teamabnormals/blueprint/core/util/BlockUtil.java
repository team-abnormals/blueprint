package com.teamabnormals.blueprint.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * A class containing some utility methods related to blocks.
 *
 * @author SmellyModder (Luke Tonon)
 * @author abigailfails
 */
public final class BlockUtil {
	/**
	 * Checks if there is a block in water at a given {@link BlockPos} in a {@link Level}.
	 *
	 * @param level A {@link Level} to use for checking the state at the given pos.
	 * @param pos   A {@link BlockPos} to check at.
	 * @return If there is a block in water at a given {@link BlockPos} in a {@link Level}.
	 */
	public static boolean isBlockInWater(Level level, BlockPos pos) {
		if (level.getBlockState(pos).getFluidState().is(FluidTags.WATER)) {
			return true;
		}
		for (Direction direction : Direction.values()) {
			if (level.getFluidState(pos.relative(direction)).is(FluidTags.WATER)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a player can place a {@link BlockState} at a given {@link BlockPos} in a given {@link Level}.
	 *
	 * @param level  A {@link Level} to use for checking the state at the given pos.
	 * @param player A {@link Player} to use for collision context, or null for no context.
	 * @param pos    A {@link BlockPos} to check at.
	 * @param state  A {@link BlockState} to check.
	 * @return If a player can place a {@link BlockState} at a given {@link BlockPos} in a given {@link Level}.
	 */
	public static boolean canPlace(Level level, @Nullable Player player, BlockPos pos, BlockState state) {
		CollisionContext selectionContext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
		VoxelShape voxelshape = state.getCollisionShape(level, pos, selectionContext);
		VoxelShape offsetShape = level.getBlockState(pos).getCollisionShape(level, pos);
		return (offsetShape.isEmpty() || level.getBlockState(pos).canBeReplaced()) && state.canSurvive(level, pos) && level.isUnobstructed(null, voxelshape.move(pos.getX(), pos.getY(), pos.getZ()));
	}

	/**
	 * Gets the place sound of a given {@link BlockState} for given a {@link Level}, {@link BlockPos}, and {@link Entity}.
	 *
	 * @param state  A {@link BlockState} to get the place sound of.
	 * @param level  A {@link Level} to use.
	 * @param pos    A {@link BlockPos} to use.
	 * @param entity A nullable {@link Entity} to use.
	 * @return The place sound of a given {@link BlockState} for a given {@link Level}, {@link BlockPos}, and {@link Entity}.
	 */
	public static SoundEvent getPlaceSound(BlockState state, Level level, BlockPos pos, @Nullable Entity entity) {
		return state.getSoundType(level, pos, entity).getPlaceSound();
	}

	/**
	 * Checks if a given {@link BlockPos} is not touching another specified block.
	 *
	 * @param level                 A {@link Level} to use.
	 * @param pos                   A {@link BlockPos} to check.
	 * @param blockToCheck          A {@link Block} to check.
	 * @param blacklistedDirections An array of directions to not check if the position is touching.
	 * @return If a given {@link BlockPos} is not touching another specified block.
	 */
	public static boolean isPosNotTouchingBlock(LevelAccessor level, BlockPos pos, Block blockToCheck, Direction... blacklistedDirections) {
		for (Direction directions : Direction.values()) {
			List<Direction> blacklistedDirectionsList = Arrays.asList(blacklistedDirections);
			if (!blacklistedDirectionsList.contains(directions)) {
				if (level.getBlockState(pos.relative(directions)).getBlock() == blockToCheck) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Copies all the similar properties of one {@link BlockState} to another.
	 *
	 * @param initial The {@link BlockState} to copy from.
	 * @param after   The {@link BlockState} to copy to.
	 * @return A {@link BlockState} containing all the similar properties of one {@link BlockState} copied from another.
	 */
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

	/**
	 * <p>Returns the {@link BlockPos} offset by 1 in the direction of {@code source}'s {@link BlockState}'s
	 * {@link DirectionalBlock#FACING} property.</p>
	 * This requires the {@link BlockState} stored in {@code source} to have a {@link DirectionalBlock#FACING} property.
	 *
	 * @param source The {@link BlockSource} to get the position from.
	 * @return The position in front of the dispenser's output face.
	 * @author abigailfails
	 */
	public static BlockPos offsetPos(BlockSource source) {
		return source.pos().relative(source.state().getValue(DirectionalBlock.FACING));
	}

	/**
	 * Gets the {@link BlockState} at the position returned by {@link #offsetPos(BlockSource source)}.
	 *
	 * @param source The {@link BlockSource} to get the position from.
	 * @return The {@link BlockState} at the offset position.
	 * @see #offsetPos(BlockSource source)
	 */
	public static BlockState getStateAtOffsetPos(BlockSource source) {
		return source.level().getBlockState(offsetPos(source));
	}

	/**
	 * Gets a {@link List} of type {@link T} at the position returned by {@link #offsetPos(BlockSource source)}.
	 *
	 * @param source     The {@link BlockSource} to get the position from.
	 * @param entityType The class extending {@link T} to search for. Set to {@code Entity.class} to get all entities, regardless of type.
	 * @return A {@link List} of entities at the offset position.
	 * @see #offsetPos(BlockSource source)
	 */
	public static <T extends Entity> List<T> getEntitiesAtOffsetPos(BlockSource source, Class<T> entityType) {
		return source.level().getEntitiesOfClass(entityType, new AABB(offsetPos(source)));
	}

	/**
	 * Gets a {@link List} of type {@link T} that match a {@link Predicate} at the position returned by {@link #offsetPos(BlockSource source)}.
	 *
	 * @param source     The {@link BlockSource} to get the position from.
	 * @param entityType The class extending {@link T} to search for. Set to {@code Entity.class} to get all entities, regardless of type.
	 * @param predicate  The predicate that takes a superclass of {@link T} as an argument to check against.
	 * @return A {@link List} of entities at the offset position.
	 * @see #offsetPos(BlockSource source)
	 */
	public static <T extends Entity> List<T> getEntitiesAtOffsetPos(BlockSource source, Class<T> entityType, Predicate<? super T> predicate) {
		return source.level().getEntitiesOfClass(entityType, new AABB(offsetPos(source)), predicate);
	}
}
