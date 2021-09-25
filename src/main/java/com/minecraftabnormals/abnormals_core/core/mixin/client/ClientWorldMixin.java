package com.minecraftabnormals.abnormals_core.core.mixin.client;

import com.minecraftabnormals.abnormals_core.core.config.ACConfig;
import com.minecraftabnormals.abnormals_core.core.events.AnimateFluidTickEvent;
import com.minecraftabnormals.abnormals_core.core.events.AnimateTickEvent;
import com.minecraftabnormals.abnormals_core.core.mixin.FluidInvokerMixin;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.CubicSampler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientWorldMixin extends Level {
    @Shadow
    @Final
    private DimensionSpecialEffects effects;
    @Shadow
    private int skyFlashTime;

    protected ClientWorldMixin(WritableLevelData p_i241925_1_, ResourceKey<Level> p_i241925_2_, DimensionType p_i241925_3_, Supplier<ProfilerFiller> p_i241925_4_, boolean p_i241925_5_, boolean p_i241925_6_, long p_i241925_7_) {
        super(p_i241925_1_, p_i241925_2_, p_i241925_3_, p_i241925_4_, p_i241925_5_, p_i241925_6_, p_i241925_7_);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;animateTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"), method = "doAnimateTick(IIIILjava/util/Random;ZLnet/minecraft/util/math/BlockPos$Mutable;)V")
    private void animateTick(Block block, BlockState state, Level world, BlockPos pos, Random rand) {
        if (!AnimateTickEvent.onAnimateTick(state, world, pos, rand)) {
            block.animateTick(state, world, pos, rand);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;animateTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"), method = "doAnimateTick(IIIILjava/util/Random;ZLnet/minecraft/util/math/BlockPos$Mutable;)V")
    private void animateFluidTick(FluidState state, Level world, BlockPos pos, Random random) {
        if (!AnimateFluidTickEvent.onAnimateFluidTick(world, pos, state, random)) {
            ((FluidInvokerMixin) state.getType()).callAnimateTick(world, pos, state, random);
        }
    }

    @Inject(at = @At("HEAD"), method = "getSkyColor", cancellable = true)
    private void getSkyColor(BlockPos blockPos, float partialTicks, CallbackInfoReturnable<Vec3> info) {
        if (ACConfig.ValuesHolder.isSmoothSkyColorEnabled() && this.effects.skyType() == DimensionSpecialEffects.SkyType.NORMAL) {
            Vec3 scaledOffset = Vec3.atLowerCornerOf(blockPos).subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
            BiomeManager biomemanager = this.getBiomeManager();
            Vec3 sampledSkyColor = CubicSampler.gaussianSampleVec3(scaledOffset, (x, y, z) -> {
                return Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).getSkyColor());
            });
            float celestialAngleFactor = Mth.clamp(Mth.cos(this.getTimeOfDay(partialTicks) * ((float) Math.PI * 2F)) * 2.0F + 0.5F, 0.0F, 1.0F);
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

            info.setReturnValue(new Vec3(r, g, b));
        }
    }
}
