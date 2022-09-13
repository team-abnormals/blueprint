package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepalleterManager;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureStart.class)
public final class StructureStartMixin {
	@Shadow
	@Final
	private Structure structure;

	@Inject(method = "placeInChunk", at = @At("HEAD"))
	public void updateStructureRepalleterManager(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource randomSource, BoundingBox bounds, ChunkPos pos, CallbackInfo callback) {
		StructureRepalleterManager.update(level.registryAccess().registry(Registry.STRUCTURE_REGISTRY).orElseThrow().getKey(this.structure), randomSource);
	}

	@Inject(method = "placeInChunk", at = @At("RETURN"))
	public void resetStructureRepalleterManager(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource randomSource, BoundingBox bounds, ChunkPos pos, CallbackInfo callback) {
		StructureRepalleterManager.reset();
	}
}
