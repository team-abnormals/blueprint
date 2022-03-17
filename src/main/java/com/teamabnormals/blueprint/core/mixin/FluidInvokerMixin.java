package com.teamabnormals.blueprint.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(Fluid.class)
public interface FluidInvokerMixin {
    @Invoker
    void callAnimateTick(Level worldIn, BlockPos pos, FluidState state, Random random);
}
