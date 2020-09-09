package com.teamabnormals.abnormals_core.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.teamabnormals.abnormals_core.core.utils.ItemStackUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author bageldotjpg
 * <p> Small cleanups from SmellyModder (Luke Tonon) </p>
 */
public class CandleBlock extends Block implements IWaterLoggable {
	public static final IntegerProperty CANDLES = IntegerProperty.create("candles", 1, 4);
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

	private static final double XZ_PARTICLE_SPEED = 0.002F;
	private static final double Y_PARTICLE_SPEED = 0.01F;
	private static final Supplier<Block> BLACK_BB_CANDLE = () -> ModList.get().isLoaded("buzzier_bees") ? ForgeRegistries.BLOCKS.getValue(new ResourceLocation("buzzier_bees", "black_candle")) : null;

	protected static final VoxelShape[] SHAPES = new VoxelShape[] {
			Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 9.0D, 10.0D),
			Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 9.0D, 13.0D),
			Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 9.0D, 13.0D),
			Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 9.0D, 13.0D)
	};
	
	private final boolean isCompat;
	
	public CandleBlock(Properties properties) {
		this(properties, false);
	}

	public CandleBlock(Properties properties, boolean isCompat) {
		super(properties);
		this.setDefaultState(this.getDefaultState().with(CANDLES, 1).with(WATERLOGGED, true).with(LIT, true));
		this.isCompat = isCompat;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState blockstate = context.getWorld().getBlockState(context.getPos());
		Direction direction = context.getPlacementHorizontalFacing();
		if (blockstate.getBlock() == this) {
			return blockstate.with(FACING, direction).with(CANDLES, Math.min(4, blockstate.get(CANDLES) + 1));
		} else {
			FluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
			boolean flag = ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8;
			return this.getDefaultState().with(FACING, direction).with(WATERLOGGED, flag).with(LIT, !flag);
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		boolean lit = state.get(LIT);
		boolean remote = world.isRemote;
		if (!lit && player.getHeldItem(handIn).getItem() instanceof FlintAndSteelItem) {
			world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
			world.setBlockState(pos, state.with(BlockStateProperties.LIT, true), 11);
			if (player != null) {
				player.getHeldItem(handIn).damageItem(1, player, (entity) -> {
					entity.sendBreakAnimation(handIn);
				});
			}
			return ActionResultType.func_233537_a_(remote);
		} else if (lit && player.getHeldItem(handIn).getItem() instanceof ShovelItem) {
			if (!remote) {
				world.playEvent((PlayerEntity) null, 1009, pos, 0);
			}
			if (player != null) {
				player.getHeldItem(handIn).damageItem(1, player, (entity) -> {
					entity.sendBreakAnimation(handIn);
				});
			}
			world.setBlockState(pos, state.with(BlockStateProperties.LIT, false));
			return ActionResultType.func_233537_a_(remote);
		}
		return ActionResultType.PASS;
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

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return hasEnoughSolidSide(worldIn, pos.down(), Direction.UP);
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
		return (0.1F * state.get(CANDLES));
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (!state.isValidPosition(world, currentPos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			if (state.get(WATERLOGGED)) {
				if (state.get(LIT))
					world.setBlockState(currentPos, state.with(LIT, false), 2);
				world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			}
			return state;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
		return useContext.getItem().getItem() == this.asItem() && state.get(CANDLES) < 4 || super.isReplaceable(state, useContext);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPES[state.get(CANDLES) - 1];
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(CANDLES, WATERLOGGED, LIT, FACING);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return type == PathType.AIR && !this.canCollide ? true : super.allowsMovement(state, worldIn, pos, type);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isCompat) {
			Block candle = BLACK_BB_CANDLE.get();
			if (candle != null) {
				ItemStackUtils.fillAfterItemForGroup(this.asItem(), candle.asItem(), group, items);
				return;
			}
		}
		super.fillItemGroup(group, items);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (state.get(LIT)) {
			double x = pos.getX();
			double y = pos.getY();
			double z = pos.getZ();
			IParticleData smokeParticle = this.getSmokeParticle();
			IParticleData flameParticle = this.getFlameParticle();

			switch (state.get(CANDLES)) {
				case 1:
					this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.5D, y + 0.75D, z + 0.5D);
					break;
				case 2:
					switch (state.get(FACING)) {
						case NORTH:
							this.addCandleParticleEffects(world, flameParticle, smokeParticle,x + 0.5625D, y + 0.75D, z + 0.3125D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle,x + 0.375D, y + 0.625D, z + 0.625D);
							break;
						case EAST:
							this.addCandleParticleEffects(world, flameParticle, smokeParticle,x + 0.6875D, y + 0.75D, z + 0.5625D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle,x + 0.375D, y + 0.625D, z + 0.375D);
							break;
						case SOUTH:
							this.addCandleParticleEffects(world, flameParticle, smokeParticle,x + 0.4375D, y + 0.75D, z + 0.6875D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle,x + 0.625D, y + 0.625D, z + 0.375D);
							break;
						case WEST:
							this.addCandleParticleEffects(world, flameParticle, smokeParticle,x + 0.3125D, y + 0.75D, z + 0.4375D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle,x + 0.625D, y + 0.625D, z + 0.625D);
							break;
					}
					break;
				case 3:
					switch (state.get(FACING)) {
						case NORTH:
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.6875D, y + 0.75D, z + 0.375D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.5D, y + 0.6875D, z + 0.6875D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.3125D, y + 0.625D, z + 0.3125D);
							break;
						case EAST:
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.625D, y + 0.75D, z + 0.6875D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.3125D, y + 0.6875D, z + 0.5D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.6875D, y + 0.625D, z + 0.3125D);
							break;
						case SOUTH:
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.3125D, y + 0.75D, z + 0.625D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.5D, y + 0.6875D, z + 0.3125D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.6875D, y + 0.625D, z + 0.6875D);
							break;
						case WEST:
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.375D, y + 0.75D, z + 0.3125D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.6875D, y + 0.6875D, z + 0.5D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.3125D, y + 0.625D, z + 0.6875D);
							break;
					}
					break;
				default:
				case 4:
					switch (state.get(FACING)) {
						case NORTH:
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.6875D, y + 0.75D, z + 0.3125D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.625D, y + 0.6875D, z + 0.625D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.3125D, y + 0.625D, z + 0.375D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.3125D, y + 0.4375D, z + 0.6875D);
							break;
						case EAST:
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.6875D, y + 0.75D, z + 0.6875D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.375D, y + 0.6875D, z + 0.625D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.625D, y + 0.625D, z + 0.3125D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.3125D, y + 0.4375D, z + 0.3125D);
							break;
						case SOUTH:
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.3125D, y + 0.75D, z + 0.6875D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.375D, y + 0.6875D, z + 0.375D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.6875D, y + 0.625D, z + 0.625D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.6875D, y + 0.4375D, z + 0.3125D);
							break;
						case WEST:
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.3125D, y + 0.75D, z + 0.3125D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.625D, y + 0.6875D, z + 0.375D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.375D, y + 0.625D, z + 0.6875D);
							this.addCandleParticleEffects(world, flameParticle, smokeParticle, x + 0.6875D, y + 0.4375D, z + 0.6875D);
							break;
					}
					break;
			}
		}
	}

	private void addCandleParticleEffects(World world, IParticleData flameParticle, IParticleData smokePartice, double x, double y, double z) {
		world.addParticle(flameParticle, x, y, z, XZ_PARTICLE_SPEED, Y_PARTICLE_SPEED, XZ_PARTICLE_SPEED);
		world.addParticle(smokePartice, x , y, z, XZ_PARTICLE_SPEED, Y_PARTICLE_SPEED, XZ_PARTICLE_SPEED);
	}

	/**
	 * Gets the flame particle for this candle.
	 * Override this to change the flame particle.
	 * @see #animateTick(BlockState, World, BlockPos, Random).
	 * @return The flame particle to use for the candle particle effects.
	 */
	protected IParticleData getFlameParticle() {
		return ParticleTypes.FLAME;
	}

	/**
	 * Gets the smoke particle for this candle.
	 * Override this to change the smoke particle.
	 * @see #animateTick(BlockState, World, BlockPos, Random).
	 * @return The smoke particle to use for the candle particle effects.
	 */
	protected IParticleData getSmokeParticle() {
		return ParticleTypes.SMOKE;
	}
}
