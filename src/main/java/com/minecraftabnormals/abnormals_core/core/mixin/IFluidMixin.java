package com.minecraftabnormals.abnormals_core.core.mixin;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(Fluid.class)
public interface IFluidMixin {
    @Invoker void callAnimateTick(World worldIn, BlockPos pos, FluidState state, Random random);
}
