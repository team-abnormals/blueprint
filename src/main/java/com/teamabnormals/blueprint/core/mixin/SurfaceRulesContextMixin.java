package com.teamabnormals.blueprint.core.mixin;

import com.google.common.base.Suppliers;
import com.teamabnormals.blueprint.common.world.modification.HasModdedBiomeSource;
import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSource;
import com.teamabnormals.blueprint.common.world.modification.ModdednessSliceGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(targets = "net.minecraft.world.level.levelgen.SurfaceRules$Context")
public final class SurfaceRulesContextMixin implements ModdednessSliceGetter {
	@Unique
	@Nullable
	private Supplier<ResourceLocation> moddedBiomeSlice;
	@Unique
	@Nullable
	private ModdedBiomeSource moddedBiomeSource;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void initModdedBiomeSource(SurfaceSystem surfaceSystem, RandomState p_224617_, ChunkAccess p_224618_, NoiseChunk p_224619_, Function<BlockPos, Holder<Biome>> p_224620_, Registry<Biome> p_224621_, WorldGenerationContext p_224622_, CallbackInfo info) {
		this.moddedBiomeSource = ((HasModdedBiomeSource) surfaceSystem).getModdedBiomeSource();
	}

	@Inject(at = @At("RETURN"), method = "updateY")
	private void updateModdedBiomeSlice(int stoneDepthAbove, int stoneDepthBelow, int waterHeight, int x, int y, int z, CallbackInfo info) {
		ModdedBiomeSource moddedBiomeSource = this.moddedBiomeSource;
		if (moddedBiomeSource != null) {
			this.moddedBiomeSlice = Suppliers.memoize(() -> moddedBiomeSource.getSliceWithVanillaZoom(x, y, z).name());
		}
	}

	@Override
	public boolean cannotGetSlices() {
		return this.moddedBiomeSource == null;
	}

	@Override
	public ResourceLocation getSliceName() {
		return this.moddedBiomeSlice.get();
	}
}
