package com.minecraftabnormals.abnormals_core.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecraftabnormals.abnormals_core.common.blocks.wood.WoodPostBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.LanternBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

@Mixin(LanternBlock.class)
public final class LanternBlockMixin {

	@Inject(method = "canSurvive", at = @At("RETURN"), cancellable = true)
	private void isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
		if (state.getValue(LanternBlock.HANGING) && worldIn.getBlockState(pos.above()).getBlock() instanceof WoodPostBlock) {
			info.setReturnValue(true);
		}
	}
}
