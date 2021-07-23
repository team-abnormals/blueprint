package com.minecraftabnormals.abnormals_core.core.mixin.client;

import com.minecraftabnormals.abnormals_core.core.config.ACConfig;
import com.minecraftabnormals.abnormals_core.core.events.AnimateFluidTickEvent;
import com.minecraftabnormals.abnormals_core.core.events.AnimateTickEvent;
import com.minecraftabnormals.abnormals_core.core.mixin.FluidInvokerMixin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.fluid.FluidState;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.storage.ISpawnWorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {
    @Shadow
    @Final
    private DimensionRenderInfo effects;
    @Shadow
    private int skyFlashTime;

    protected ClientWorldMixin(ISpawnWorldInfo p_i241925_1_, RegistryKey<World> p_i241925_2_, DimensionType p_i241925_3_, Supplier<IProfiler> p_i241925_4_, boolean p_i241925_5_, boolean p_i241925_6_, long p_i241925_7_) {
        super(p_i241925_1_, p_i241925_2_, p_i241925_3_, p_i241925_4_, p_i241925_5_, p_i241925_6_, p_i241925_7_);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;animateTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"), method = "doAnimateTick(IIIILjava/util/Random;ZLnet/minecraft/util/math/BlockPos$Mutable;)V")
    private void animateTick(Block block, BlockState state, World world, BlockPos pos, Random rand) {
        if (!AnimateTickEvent.onAnimateTick(state, world, pos, rand)) {
            block.animateTick(state, world, pos, rand);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;animateTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"), method = "doAnimateTick(IIIILjava/util/Random;ZLnet/minecraft/util/math/BlockPos$Mutable;)V")
    private void animateFluidTick(FluidState state, World world, BlockPos pos, Random random) {
        if (!AnimateFluidTickEvent.onAnimateFluidTick(world, pos, state, random)) {
            ((FluidInvokerMixin) state.getType()).callAnimateTick(world, pos, state, random);
        }
    }

    @Inject(at = @At("HEAD"), method = "getSkyColor", cancellable = true)
    private void getSkyColor(BlockPos blockPos, float partialTicks, CallbackInfoReturnable<Vector3d> info) {
        if (ACConfig.ValuesHolder.isSmoothSkyColorEnabled() && this.effects.skyType() == DimensionRenderInfo.FogType.NORMAL) {
            Vector3d scaledOffset = Vector3d.atLowerCornerOf(blockPos).subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
            BiomeManager biomemanager = this.getBiomeManager();
            Vector3d sampledSkyColor = CubicSampler.gaussianSampleVec3(scaledOffset, (x, y, z) -> {
                return Vector3d.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).getSkyColor());
            });
            float celestialAngleFactor = MathHelper.clamp(MathHelper.cos(this.getTimeOfDay(partialTicks) * ((float) Math.PI * 2F)) * 2.0F + 0.5F, 0.0F, 1.0F);
            float r = (float) sampledSkyColor.x * celestialAngleFactor;
            float g = (float) sampledSkyColor.y * celestialAngleFactor;
            float b = (float) sampledSkyColor.z * celestialAngleFactor;
            float rainStrength = this.getRainLevel(partialTicks);
            if (rainStrength > 0.0F) {
                float colorModifier = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.6F;
                float strengthFactor = 1.0F - rainStrength * 0.75F;
                r = r * strengthFactor + colorModifier * (1.0F - strengthFactor);
                g = g * strengthFactor + colorModifier * (1.0F - strengthFactor);
                b = b * strengthFactor + colorModifier * (1.0F - strengthFactor);
            }

            float thunderStrength = this.getThunderLevel(partialTicks);
            if (thunderStrength > 0.0F) {
                float colorModifier = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.2F;
                float strengthFactor = 1.0F - thunderStrength * 0.75F;
                r = r * strengthFactor + colorModifier * (1.0F - strengthFactor);
                g = g * strengthFactor + colorModifier * (1.0F - strengthFactor);
                b = b * strengthFactor + colorModifier * (1.0F - strengthFactor);
            }

            if (this.skyFlashTime > 0) {
                float flash = (float) this.skyFlashTime - partialTicks;
                if (flash > 1.0F) {
                    flash = 1.0F;
                }

                flash *= 0.45F;
                r = r * (1.0F - flash) + 0.8F * flash;
                g = g * (1.0F - flash) + 0.8F * flash;
                b = b * (1.0F - flash) + 1.0F * flash;
            }

            info.setReturnValue(new Vector3d(r, g, b));
        }
    }
}
