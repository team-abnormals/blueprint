package com.minecraftabnormals.abnormals_core.common.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class BELWRFuelBlockItem extends BELWRBlockItem {
	private final int burnTime;

	public BELWRFuelBlockItem(Block block, Properties properties, Supplier<LazyBELWR> belwr, int burnTime) {
		super(block, properties, belwr);
		this.burnTime = burnTime;
	}

	@Override
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
		return this.burnTime;
	}
}
