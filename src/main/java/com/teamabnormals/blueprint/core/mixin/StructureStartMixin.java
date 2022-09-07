package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.core.api.StructureBlockStateReplacer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(StructureStart.class)
public class StructureStartMixin {

	@Shadow
	@Final
	private PiecesContainer pieceContainer;

	@Shadow
	@Final
	private ConfiguredStructureFeature<?, ?> feature;

	@Inject(method = "placeInChunk", at = @At("HEAD"))
	public void injectReference(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator generator, Random random, BoundingBox bounds, ChunkPos pos, CallbackInfo callback) {
		StructureBlockStateReplacer.setActiveStructure(feature, pieceContainer);
	}

	@Inject(method = "placeInChunk", at = @At("RETURN"))
	public void resetReference(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator generator, Random random, BoundingBox bounds, ChunkPos pos, CallbackInfo callback) {
		StructureBlockStateReplacer.setActiveStructure(null, null);
	}
}