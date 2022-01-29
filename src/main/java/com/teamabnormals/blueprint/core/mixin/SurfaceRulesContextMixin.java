package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSource;
import com.teamabnormals.blueprint.common.world.modification.ModdedSurfaceSystem;
import com.teamabnormals.blueprint.common.world.modification.ModdednessSliceGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Function;

@Mixin(targets = "net.minecraft.world.level.levelgen.SurfaceRules$Context")
public final class SurfaceRulesContextMixin implements ModdednessSliceGetter {
	@Unique
	@Nullable
	private ModdedBiomeSource moddedBiomeSource;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void initModdedBiomeSource(SurfaceSystem surfaceSystem, ChunkAccess p_189563_, NoiseChunk p_189564_, Function<BlockPos, Biome> p_189565_, Registry<Biome> p_189566_, WorldGenerationContext p_189567_, CallbackInfo info) {
		this.moddedBiomeSource = ((ModdedSurfaceSystem) surfaceSystem).getModdedBiomeSource();
	}

	@Override
	public boolean cannotGetSlices() {
		return this.moddedBiomeSource == null;
	}

	@Override
	public ResourceLocation getSliceName(int x, int z) {
		return this.moddedBiomeSource.getSliceName(x, z);
	}
}
