package com.teamabnormals.abnormals_core.core.library.api;

import java.util.Random;
import java.util.function.ToDoubleFunction;

import javax.annotation.Nullable;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * @author SmellyModder(Luke Tonon)
 */
public class AdvancedRandomPositionGenerator {

	/**
	 * Finds a random target within xz and y
	 */
	@Nullable
	public static Vec3d findRandomTarget(CreatureEntity creature, int xz, int y, boolean goDeep) {
		return findRandomTargetBlock(creature, xz, y, null, goDeep);
	}
	
	@Nullable
	private static Vec3d findRandomTargetBlock(CreatureEntity creature, int xz, int y, @Nullable Vec3d targetVec, boolean goDeep) {
		return generateRandomPos(creature, xz, y, targetVec, true, Math.PI / 2F, goDeep, creature::getBlockPathWeight);
	}
	
	@Nullable
	private static Vec3d generateRandomPos(CreatureEntity creature, int xz, int y, @Nullable Vec3d p_191379_3_, boolean p_191379_4_, double p_191379_5_, boolean goDeep, ToDoubleFunction<BlockPos> p_191379_7_) {
		PathNavigator pathnavigator = creature.getNavigator();
		Random random = creature.getRNG();
		boolean flag = creature.detachHome() ? creature.getHomePosition().withinDistance(creature.getPositionVec(), (double)(creature.getMaximumHomeDistance() + (float)xz) + 1.0D) : false;
		boolean flag1 = false;
		double d0 = Double.NEGATIVE_INFINITY;
		BlockPos blockpos = new BlockPos(creature);

		for(int i = 0; i < 10; ++i) {
			BlockPos blockpos1 = getBlockPos(random, xz, y, p_191379_3_, p_191379_5_, goDeep);
			if(blockpos1 != null) {
				int j = blockpos1.getX();
				int k = blockpos1.getY();
				int l = blockpos1.getZ();
				if(creature.detachHome() && xz > 1) {
					BlockPos blockpos2 = creature.getHomePosition();
					if(creature.getPosX() > (double)blockpos2.getX()) {
						j -= random.nextInt(xz / 2);
					} else {
						j += random.nextInt(xz / 2);
					}

					if(creature.getPosZ() > (double)blockpos2.getZ()) {
						l -= random.nextInt(xz / 2);
					} else {
						l += random.nextInt(xz / 2);
					}
				}

				BlockPos blockpos3 = new BlockPos((double)j + creature.getPosX(), (double)k + creature.getPosY(), (double)l + creature.getPosZ());
				if((!flag || creature.isWithinHomeDistanceFromPosition(blockpos3)) && pathnavigator.canEntityStandOnPos(blockpos3)) {
					if(!p_191379_4_) {
						blockpos3 = moveAboveSolid(blockpos3, creature);
						if(isWaterDestination(blockpos3, creature)) {
							continue;
						}
					}

					double d1 = p_191379_7_.applyAsDouble(blockpos3);
					if(d1 > d0) {
						d0 = d1;
						blockpos = blockpos3;
						flag1 = true;
					}
				}
			}
		}

		if(flag1) {
			return new Vec3d(blockpos);
		} else {
			return null;
		}
	}
	
	@Nullable
	private static BlockPos getBlockPos(Random rand, int xz, int y, @Nullable Vec3d vec3d, double angle, boolean goDeep) {
		if(vec3d != null && !(angle >= Math.PI)) {
			double d3 = MathHelper.atan2(vec3d.z, vec3d.x) - (double)((float)Math.PI / 2F);
			double d4 = d3 + (double)(2.0F * rand.nextFloat() - 1.0F) * angle;
			double d0 = Math.sqrt(rand.nextDouble()) * (double)MathHelper.SQRT_2 * (double)xz;
			double d1 = -d0 * Math.sin(d4);
			double d2 = d0 * Math.cos(d4);
			if(!(Math.abs(d1) > (double)xz) && !(Math.abs(d2) > (double)xz)) {
				double newY = rand.nextInt(2 * y + 1) - y;
				return new BlockPos(d1, newY, d2);
			} else {
				return null;
			}
		} else {
			int newX = rand.nextInt(2 * xz + 1) - xz;
			int newY = rand.nextInt(2 * y + 1) - y;
			int newZ = rand.nextInt(2 * xz + 1) - xz;
			if(goDeep) {
				newY = rand.nextInt(y + 1) - y * 2;
			}
			return new BlockPos(newX, newY, newZ);
		}
	}
	
	private static BlockPos moveAboveSolid(BlockPos pos, CreatureEntity creature) {
		if(!creature.world.getBlockState(pos).getMaterial().isSolid()) {
			return pos;
		} else {
			BlockPos blockpos;
			for(blockpos = pos.up(); blockpos.getY() < creature.world.getHeight() && creature.world.getBlockState(blockpos).getMaterial().isSolid(); blockpos = blockpos.up()) {
				;
			}

			return blockpos;
		}
	}

	private static boolean isWaterDestination(BlockPos pos, CreatureEntity creature) {
		return creature.world.getFluidState(pos).isTagged(FluidTags.WATER);
	}
}