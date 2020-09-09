package com.teamabnormals.abnormals_core.common.blocks;

import com.teamabnormals.abnormals_core.core.examples.ExampleTileEntityRegistry;
import com.teamabnormals.abnormals_core.core.utils.ItemStackUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author bageldotjpg
 * <p> Small cleanups from SmellyModder (Luke Tonon) </p>
 */
public class ScentedCandleBlock extends CandleBlock {
	private static final Supplier<Block> PINK_CLOVER_BB_CANDLE = () -> ModList.get().isLoaded("buzzier_bees") ? ForgeRegistries.BLOCKS.getValue(new ResourceLocation("buzzier_bees", "pink_clover_scented_candle")) : null;

	public final Supplier<Effect> candleEffectInstance;
	public final int duration;
	public final int level;
	private final boolean isCompat;

	public ScentedCandleBlock(Properties properties, Supplier<Effect> candleEffectInstance) {
		this(properties, candleEffectInstance, 70, 0, false);
	}
	
	public ScentedCandleBlock(Properties properties, Supplier<Effect> candleEffectInstance, boolean isCompat) {
		this(properties, candleEffectInstance, 70, 0, isCompat);
	}

	public ScentedCandleBlock(Properties properties, Supplier<Effect> candleEffectInstance, int duration, int level, boolean isCompat) {
		super(properties);
		this.candleEffectInstance = candleEffectInstance;
		this.duration = duration;
		this.level = level;
		this.isCompat = true;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ExampleTileEntityRegistry.SCENTED_CANDLE.get().create();
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
		return 0.25F * state.get(CANDLES);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isCompat) {
			Block candle = PINK_CLOVER_BB_CANDLE.get();
			if (candle != null) {
				ItemStackUtils.fillAfterItemForGroup(this.asItem(), candle.asItem(), group, items);
				return;
			}
		}
		super.fillItemGroup(group, items);
	}
}
