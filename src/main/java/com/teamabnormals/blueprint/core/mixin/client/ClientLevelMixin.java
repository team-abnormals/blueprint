package com.teamabnormals.blueprint.core.mixin.client;

import com.teamabnormals.blueprint.core.events.AnimateTickEvents;
import com.teamabnormals.blueprint.core.mixin.FluidInvokerMixin;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("deprecation")
@Mixin(ClientLevel.class)
public final class ClientLevelMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;animateTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V"), method = "doAnimateTick(IIIILnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos$MutableBlockPos;)V")
    private void animateTick(Block block, BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (AnimateTickEvents.onAnimateTick(state, level, pos, randomSource)) {
            block.animateTick(state, level, pos, randomSource);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;animateTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V"), method = "doAnimateTick(IIIILnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos$MutableBlockPos;)V")
    private void animateFluidTick(FluidState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (AnimateTickEvents.onAnimateFluidTick(state, level, pos, randomSource)) {
            ((FluidInvokerMixin) state.getType()).callAnimateTick(level, pos, state, randomSource);
        }
    }
}
