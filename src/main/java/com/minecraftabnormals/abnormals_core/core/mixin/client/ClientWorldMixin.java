package com.minecraftabnormals.abnormals_core.core.mixin.client;

import com.minecraftabnormals.abnormals_core.core.events.AnimateFluidTickEvent;
import com.minecraftabnormals.abnormals_core.core.events.AnimateTickEvent;
import com.minecraftabnormals.abnormals_core.core.mixin.FluidInvokerMixin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(ClientWorld.class)
public final class ClientWorldMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;animateTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"), method = "animateTick(IIIILjava/util/Random;ZLnet/minecraft/util/math/BlockPos$Mutable;)V")
    private void animateTick(Block block, BlockState state, World world, BlockPos pos, Random rand) {
        if (!AnimateTickEvent.onAnimateTick(state, world, pos, rand)) {
            block.animateTick(state, world, pos, rand);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;animateTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"), method = "animateTick(IIIILjava/util/Random;ZLnet/minecraft/util/math/BlockPos$Mutable;)V")
    private void animateFluidTick(FluidState state, World world, BlockPos pos, Random random) {
        if (!AnimateFluidTickEvent.onAnimateFluidTick(world, pos, state, random)) {
            ((FluidInvokerMixin) state.getFluid()).callAnimateTick(world, pos, state, random);
        }
    }
}
