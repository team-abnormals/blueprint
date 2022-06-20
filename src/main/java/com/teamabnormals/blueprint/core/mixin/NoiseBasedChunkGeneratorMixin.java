package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.world.modification.HasModdedBiomeSource;
import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSource;
import net.minecraft.core.Registry;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(NoiseBasedChunkGenerator.class)
public final class NoiseBasedChunkGeneratorMixin implements HasModdedBiomeSource {
	@Unique
	@Nullable
	private ModdedBiomeSource moddedBiomeSource;

	@Inject(method = "Lnet/minecraft/world/level/levelgen/NoiseBasedChunkGenerator;buildSurface(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/blending/Blender;)V", at = @At("HEAD"))
	private void provideModdedBiomeSource(ChunkAccess p_224262_, WorldGenerationContext context, RandomState randomState, StructureManager p_224265_, BiomeManager p_224266_, Registry<Biome> p_224267_, Blender p_224268_, CallbackInfo info) {
		((HasModdedBiomeSource) randomState.surfaceSystem()).setModdedBiomeSource(this.moddedBiomeSource);
	}

	@Nullable
	@Override
	public ModdedBiomeSource getModdedBiomeSource() {
		return this.moddedBiomeSource;
	}

	@Override
	public void setModdedBiomeSource(@Nullable ModdedBiomeSource moddedBiomeSource) {
		this.moddedBiomeSource = moddedBiomeSource;
	}
}
