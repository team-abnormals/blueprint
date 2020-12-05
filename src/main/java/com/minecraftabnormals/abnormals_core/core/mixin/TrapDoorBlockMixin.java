package com.minecraftabnormals.abnormals_core.core.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author bageldotjpg
 */
@Mixin(TrapDoorBlock.class)
public final class TrapDoorBlockMixin {

	//Temporary fix for a Forge issue
	@Inject(at = @At("RETURN"), method = "isLadder", cancellable = true, remap = false)
	private void isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity, CallbackInfoReturnable<Boolean> info) {
		if (state.get(TrapDoorBlock.OPEN)) {
			BlockState down = world.getBlockState(pos.down());
			if (down.getBlock() instanceof LadderBlock)
				info.setReturnValue(down.get(LadderBlock.FACING) == state.get(TrapDoorBlock.HORIZONTAL_FACING));
		}
	}

}
