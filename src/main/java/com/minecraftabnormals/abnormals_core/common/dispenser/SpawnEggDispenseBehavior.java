package com.minecraftabnormals.abnormals_core.common.dispenser;

import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.BlockSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.core.Direction;

public final class SpawnEggDispenseBehavior extends DefaultDispenseItemBehavior {

	@Override
	public ItemStack execute(BlockSource source, ItemStack stack) {
		Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
		EntityType<?> entitytype = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
		entitytype.spawn(source.getLevel(), stack, (Player) null, source.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
		stack.shrink(1);
		return stack;
	}

}
