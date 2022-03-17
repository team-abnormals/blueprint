package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.block.wood.WoodPostBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
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
