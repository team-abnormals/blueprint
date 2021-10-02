package com.minecraftabnormals.abnormals_core.core.mixin;

import com.minecraftabnormals.abnormals_core.common.blocks.wood.WoodPostBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LanternBlock.class)
public final class LanternBlockMixin {

	@Inject(method = "canSurvive", at = @At("RETURN"), cancellable = true)
	private void canSurvive(BlockState state, LevelReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
		if (state.getValue(LanternBlock.HANGING) && worldIn.getBlockState(pos.above()).getBlock() instanceof WoodPostBlock) {
			info.setReturnValue(true);
		}
	}

}
