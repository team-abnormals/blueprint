package com.minecraftabnormals.abnormals_core.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecraftabnormals.abnormals_core.common.blocks.wood.StrippedWoodPostBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.LanternBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

@Mixin(LanternBlock.class)
public final class LanternBlockMixin {

	@Inject(method = "isValidPosition", at = @At("RETURN"), cancellable = true)
	private void isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(callbackInfoReturnable.getReturnValue() || (state.get(LanternBlock.HANGING) && worldIn.getBlockState(pos.up()).getBlock() instanceof StrippedWoodPostBlock));
	}

}
