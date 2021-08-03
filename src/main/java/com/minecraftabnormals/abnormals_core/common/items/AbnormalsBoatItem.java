package com.minecraftabnormals.abnormals_core.common.items;

import com.minecraftabnormals.abnormals_core.common.entity.AbnormalsBoatEntity;
import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

public class AbnormalsBoatItem extends Item {
	private static final TargetedItemGroupFiller FILLER = new TargetedItemGroupFiller(() -> Items.DARK_OAK_BOAT);
	private static final Predicate<Entity> COLLISION_PREDICATE = EntityPredicates.NO_SPECTATORS.and(Entity::isPickable);
	private final String type;

	public AbnormalsBoatItem(String type, Item.Properties properties) {
		super(properties);
		this.type = type;
		DispenserBlock.registerBehavior(this, new DispenserBoatBehavior(type));
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this, group, items);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		RayTraceResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, RayTraceContext.FluidMode.ANY);
		if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
			return new ActionResult<>(ActionResultType.PASS, itemstack);
		} else {
			Vector3d vec3d = playerIn.getViewVector(1.0F);
			List<Entity> list = worldIn.getEntities(playerIn, playerIn.getBoundingBox().expandTowards(vec3d.scale(5.0D)).inflate(1.0D), COLLISION_PREDICATE);
			if (!list.isEmpty()) {
				Vector3d vec3d1 = playerIn.getEyePosition(1.0F);

				for (Entity entity : list) {
					AxisAlignedBB axisalignedbb = entity.getBoundingBox().inflate((double) entity.getPickRadius());
					if (axisalignedbb.contains(vec3d1)) {
						return new ActionResult<>(ActionResultType.PASS, itemstack);
					}
				}
			}

			if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
				AbnormalsBoatEntity boatentity = new AbnormalsBoatEntity(worldIn, raytraceresult.getLocation().x, raytraceresult.getLocation().y, raytraceresult.getLocation().z);
				boatentity.setBoat(this.type);
				boatentity.yRot = playerIn.yRot;
				if (!worldIn.noCollision(boatentity, boatentity.getBoundingBox().inflate(-0.1D))) {
					return new ActionResult<>(ActionResultType.FAIL, itemstack);
				} else {
					if (!worldIn.isClientSide) {
						worldIn.addFreshEntity(boatentity);
					}

					if (!playerIn.abilities.instabuild) {
						itemstack.shrink(1);
					}

					playerIn.awardStat(Stats.ITEM_USED.get(this));
					return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
				}
			} else {
				return new ActionResult<>(ActionResultType.PASS, itemstack);
			}
		}
	}

	static class DispenserBoatBehavior extends DefaultDispenseItemBehavior {
		private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
		private final String type;

		public DispenserBoatBehavior(String type) {
			this.type = type;
		}

		@SuppressWarnings("deprecation")
		public ItemStack execute(IBlockSource iBlockSource, ItemStack stack) {
			Direction direction = iBlockSource.getBlockState().getValue(DispenserBlock.FACING);
			World world = iBlockSource.getLevel();
			double x = iBlockSource.x() + (double) ((float) direction.getStepX() * 1.125f);
			double y = iBlockSource.y() + (double) ((float) direction.getStepY() * 1.125f);
			double z = iBlockSource.z() + (double) ((float) direction.getStepZ() * 1.125f);
			BlockPos pos = iBlockSource.getPos().relative(direction);
			double adjustY;
			if (world.getFluidState(pos).is(FluidTags.WATER)) {
				adjustY = 1d;
			} else {
				if (!world.getBlockState(pos).isAir() || !world.getFluidState(pos.below()).is(FluidTags.WATER)) {
					return this.defaultDispenseItemBehavior.dispense(iBlockSource, stack);
				}
				adjustY = 0d;
			}
			AbnormalsBoatEntity boat = new AbnormalsBoatEntity(world, x, y + adjustY, z);
			boat.setBoat(this.type);
			boat.yRot = direction.toYRot();
			world.addFreshEntity(boat);
			stack.shrink(1);
			return stack;
		}

		protected void playSound(IBlockSource iBlockSource) {
			iBlockSource.getLevel().levelEvent(1000, iBlockSource.getPos(), 0);
		}
	}
}