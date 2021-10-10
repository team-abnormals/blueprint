package com.minecraftabnormals.abnormals_core.common.dispenser;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;

public final class SpawnEggDispenseItemBehavior extends DefaultDispenseItemBehavior {

	@Override
	public ItemStack execute(BlockSource source, ItemStack stack) {
		Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
		EntityType<?> entitytype = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
		entitytype.spawn(source.getLevel(), stack, null, source.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
		stack.shrink(1);
		return stack;
	}

}
