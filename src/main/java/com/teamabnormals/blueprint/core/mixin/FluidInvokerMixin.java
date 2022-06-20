package com.teamabnormals.blueprint.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Fluid.class)
public interface FluidInvokerMixin {
    @Invoker
    void callAnimateTick(Level level, BlockPos pos, FluidState state, RandomSource randomSource);
}
