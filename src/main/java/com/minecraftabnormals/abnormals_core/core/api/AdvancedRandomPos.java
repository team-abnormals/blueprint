package com.minecraftabnormals.abnormals_core.core.api;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.ToDoubleFunction;

/**
 * @author SmellyModder(Luke Tonon)
 */
public final class AdvancedRandomPos {
	/**
	 * Finds a random target within xz and y
	 */
	@Nullable
	public static Vec3 findRandomTarget(PathfinderMob pathfinder, int xz, int y, boolean goDeep) {
		return findRandomTargetBlock(pathfinder, xz, y, null, goDeep);
	}

	@Nullable
	private static Vec3 findRandomTargetBlock(PathfinderMob pathfinder, int xz, int y, @Nullable Vec3 targetVec, boolean goDeep) {
		return generateRandomPos(pathfinder, xz, y, targetVec, true, Math.PI / 2F, goDeep, pathfinder::getWalkTargetValue);
	}

	@Nullable
	private static Vec3 generateRandomPos(PathfinderMob pathfinder, int xz, int y, @Nullable Vec3 p_191379_3_, boolean p_191379_4_, double p_191379_5_, boolean goDeep, ToDoubleFunction<BlockPos> p_191379_7_) {
		PathNavigation pathnavigator = pathfinder.getNavigation();
		Random random = pathfinder.getRandom();
		boolean flag = pathfinder.hasRestriction() && pathfinder.getRestrictCenter().closerThan(pathfinder.position(), (double) (pathfinder.getRestrictRadius() + (float) xz) + 1.0D);
		boolean flag1 = false;
		double d0 = Double.NEGATIVE_INFINITY;
		BlockPos blockpos = new BlockPos(pathfinder.position());

		for (int i = 0; i < 10; ++i) {
			BlockPos blockpos1 = getBlockPos(random, xz, y, p_191379_3_, p_191379_5_, goDeep);
			if (blockpos1 != null) {
				int j = blockpos1.getX();
				int k = blockpos1.getY();
				int l = blockpos1.getZ();
				if (pathfinder.hasRestriction() && xz > 1) {
					BlockPos blockpos2 = pathfinder.getRestrictCenter();
					if (pathfinder.getX() > (double) blockpos2.getX()) {
						j -= random.nextInt(xz / 2);
					} else {
						j += random.nextInt(xz / 2);
					}

					if (pathfinder.getZ() > (double) blockpos2.getZ()) {
						l -= random.nextInt(xz / 2);
					} else {
						l += random.nextInt(xz / 2);
					}
				}

				BlockPos blockpos3 = new BlockPos((double) j + pathfinder.getX(), (double) k + pathfinder.getY(), (double) l + pathfinder.getZ());
				if ((!flag || pathfinder.isWithinRestriction(blockpos3)) && pathnavigator.isStableDestination(blockpos3)) {
					if (!p_191379_4_) {
						blockpos3 = moveAboveSolid(blockpos3, pathfinder);
						if (isWaterDestination(blockpos3, pathfinder)) {
							continue;
						}
					}

					double d1 = p_191379_7_.applyAsDouble(blockpos3);
					if (d1 > d0) {
						d0 = d1;
						blockpos = blockpos3;
						flag1 = true;
					}
				}
			}
		}

		if (flag1) {
			return Vec3.atCenterOf(blockpos);
		} else {
			return null;
		}
	}

	@Nullable
	private static BlockPos getBlockPos(Random rand, int xz, int y, @Nullable Vec3 Vector3d, double angle, boolean goDeep) {
		if (Vector3d != null && !(angle >= Math.PI)) {
			double d3 = Mth.atan2(Vector3d.z, Vector3d.x) - (double) ((float) Math.PI / 2F);
			double d4 = d3 + (double) (2.0F * rand.nextFloat() - 1.0F) * angle;
			double d0 = Math.sqrt(rand.nextDouble()) * (double) Mth.SQRT_OF_TWO * (double) xz;
			double d1 = -d0 * Math.sin(d4);
			double d2 = d0 * Math.cos(d4);
			if (!(Math.abs(d1) > (double) xz) && !(Math.abs(d2) > (double) xz)) {
				double newY = rand.nextInt(2 * y + 1) - y;
				return new BlockPos(d1, newY, d2);
			} else {
				return null;
			}
		} else {
			int newX = rand.nextInt(2 * xz + 1) - xz;
			int newY = rand.nextInt(2 * y + 1) - y;
			int newZ = rand.nextInt(2 * xz + 1) - xz;
			if (goDeep) {
				newY = rand.nextInt(y + 1) - y * 2;
			}
			return new BlockPos(newX, newY, newZ);
		}
	}

	private static BlockPos moveAboveSolid(BlockPos pos, PathfinderMob pathfinder) {
		if (!pathfinder.level.getBlockState(pos).getMaterial().isSolid()) {
			return pos;
		} else {
			BlockPos blockpos;
			for (blockpos = pos.above(); blockpos.getY() < pathfinder.level.getMaxBuildHeight() && pathfinder.level.getBlockState(blockpos).getMaterial().isSolid(); blockpos = blockpos.above()) {
			}

			return blockpos;
		}
	}

	private static boolean isWaterDestination(BlockPos pos, PathfinderMob pathfinder) {
		return pathfinder.level.getFluidState(pos).is(FluidTags.WATER);
	}
}