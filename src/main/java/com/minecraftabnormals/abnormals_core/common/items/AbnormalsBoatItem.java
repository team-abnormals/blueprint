package com.minecraftabnormals.abnormals_core.common.items;

import com.minecraftabnormals.abnormals_core.common.entity.AbnormalsBoatEntity;
import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.BlockSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntitySelector;

public class AbnormalsBoatItem extends Item {
	private static final TargetedItemGroupFiller FILLER = new TargetedItemGroupFiller(() -> Items.DARK_OAK_BOAT);
	private static final Predicate<Entity> COLLISION_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
	private final String type;

	public AbnormalsBoatItem(String type, Item.Properties properties) {
		super(properties);
		this.type = type;
		DispenserBlock.registerBehavior(this, new DispenserBoatBehavior(type));
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this, group, items);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		HitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.ANY);
		if (raytraceresult.getType() == HitResult.Type.MISS) {
			return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
		} else {
			Vec3 vec3d = playerIn.getViewVector(1.0F);
			List<Entity> list = worldIn.getEntities(playerIn, playerIn.getBoundingBox().expandTowards(vec3d.scale(5.0D)).inflate(1.0D), COLLISION_PREDICATE);
			if (!list.isEmpty()) {
				Vec3 vec3d1 = playerIn.getEyePosition(1.0F);

				for (Entity entity : list) {
					AABB axisalignedbb = entity.getBoundingBox().inflate((double) entity.getPickRadius());
					if (axisalignedbb.contains(vec3d1)) {
						return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
					}
				}
			}

			if (raytraceresult.getType() == HitResult.Type.BLOCK) {
				AbnormalsBoatEntity boatentity = new AbnormalsBoatEntity(worldIn, raytraceresult.getLocation().x, raytraceresult.getLocation().y, raytraceresult.getLocation().z);
				boatentity.setBoat(this.type);
				boatentity.yRot = playerIn.yRot;
				if (!worldIn.noCollision(boatentity, boatentity.getBoundingBox().inflate(-0.1D))) {
					return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
				} else {
					if (!worldIn.isClientSide) {
						worldIn.addFreshEntity(boatentity);
					}

					if (!playerIn.abilities.instabuild) {
						itemstack.shrink(1);
					}

					playerIn.awardStat(Stats.ITEM_USED.get(this));
					return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
				}
			} else {
				return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
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
		public ItemStack execute(BlockSource iBlockSource, ItemStack stack) {
			Direction direction = iBlockSource.getBlockState().getValue(DispenserBlock.FACING);
			Level world = iBlockSource.getLevel();
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

		protected void playSound(BlockSource iBlockSource) {
			iBlockSource.getLevel().levelEvent(1000, iBlockSource.getPos(), 0);
		}
	}
}