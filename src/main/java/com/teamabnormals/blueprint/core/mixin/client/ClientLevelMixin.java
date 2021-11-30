package com.teamabnormals.blueprint.core.mixin.client;

import com.teamabnormals.blueprint.core.events.AnimateFluidTickEvent;
import com.teamabnormals.blueprint.core.events.AnimateTickEvent;
import com.teamabnormals.blueprint.core.mixin.FluidInvokerMixin;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(ClientLevel.class)
public final class ClientLevelMixin {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;animateTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/Random;)V"), method = "doAnimateTick(IIIILjava/util/Random;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos$MutableBlockPos;)V")
    private void animateTick(Block block, BlockState state, Level world, BlockPos pos, Random rand) {
        if (!AnimateTickEvent.onAnimateTick(state, world, pos, rand)) {
            block.animateTick(state, world, pos, rand);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;animateTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/Random;)V"), method = "doAnimateTick(IIIILjava/util/Random;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos$MutableBlockPos;)V")
    private void animateFluidTick(FluidState state, Level world, BlockPos pos, Random random) {
        if (!AnimateFluidTickEvent.onAnimateFluidTick(world, pos, state, random)) {
            ((FluidInvokerMixin) state.getType()).callAnimateTick(world, pos, state, random);
        }
    }
}
