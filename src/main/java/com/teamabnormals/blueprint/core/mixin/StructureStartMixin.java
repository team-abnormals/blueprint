package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletterEntry;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepalleterManager;
import com.teamabnormals.blueprint.core.util.DataUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

@Mixin(StructureStart.class)
public final class StructureStartMixin {
	@Shadow
	@Final
	private Structure structure;
	@Unique
	private Function<WorldGenLevel, StructureRepaletterEntry[]> repalettersGetter = DataUtil.memoize(worldGenLevel -> StructureRepalleterManager.getRepalettersForStructure(worldGenLevel.registryAccess().registryOrThrow(Registries.STRUCTURE).getResourceKey(this.structure).orElseThrow()));

	@Inject(method = "placeInChunk", at = @At("HEAD"))
	public void updateStructureRepalleterRandomSource(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource randomSource, BoundingBox bounds, ChunkPos pos, CallbackInfo info) {
		StructureRepalleterManager.updateRandomSource(randomSource);
	}

	@Inject(method = "placeInChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/StructurePiece;postProcess(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/core/BlockPos;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void updateActiveRepalletersForPieceType(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource randomSource, BoundingBox bounds, ChunkPos pos, CallbackInfo info, List<StructurePiece> list, BoundingBox boundingbox, BlockPos blockpos, BlockPos blockpos1, Iterator<StructurePiece> iterator, StructurePiece structurepiece) {
		var structurePieces = level.registryAccess().registryOrThrow(Registries.STRUCTURE_PIECE);
		StructureRepalleterManager.updateActiveRepaletters(this.repalettersGetter.apply(level), structurePieces.getHolderOrThrow(structurePieces.getResourceKey(structurepiece.getType()).orElseThrow()));
	}

	@Inject(method = "placeInChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/Structure;afterPlace(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/levelgen/structure/pieces/PiecesContainer;)V"))
	private void updateActiveRepalletersForAfterPlace(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource randomSource, BoundingBox bounds, ChunkPos pos, CallbackInfo info) {
		StructureRepalleterManager.updateActiveRepaletters(this.repalettersGetter.apply(level), null);
	}

	@Inject(method = "placeInChunk", at = @At("RETURN"))
	public void resetStructureRepalleterManager(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource randomSource, BoundingBox bounds, ChunkPos pos, CallbackInfo info) {
		StructureRepalleterManager.reset();
	}
}
