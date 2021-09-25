package com.minecraftabnormals.abnormals_core.core.mixin;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(Fluid.class)
public interface FluidInvokerMixin {
    @Invoker
    void callAnimateTick(Level worldIn, BlockPos pos, FluidState state, Random random);
}
