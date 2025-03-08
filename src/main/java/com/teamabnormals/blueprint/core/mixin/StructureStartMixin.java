package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.world.modification.structure.*;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Mixin(StructureStart.class)
public final class StructureStartMixin implements RepalettedStructureStart {
	@Shadow
	@Final
	private Structure structure;
	@Unique
	@Nullable
	private ArrayList<StructureRepaletterManager.Entry> repaletters;

	@Override
	public void initializeRepaletters(StructureModificationContext context) {
		var key = context.getGenerationContext().registryAccess().registryOrThrow(Registries.STRUCTURE).getResourceKey(this.structure).orElseThrow();
		StructureRepaletterEntry[] entries = StructureRepaletterManager.getRepalettersForStructure(key);
		if (entries == null) return;
		ArrayList<StructureRepaletterManager.Entry> repaletters = new ArrayList<>();
		for (StructureRepaletterEntry entry : entries) {
			if (entry.condition().isPresent() && !entry.condition().get().test(context)) continue;
			StructureRepaletter.Replacer replacer = entry.repaletter().createReplacer(context);
			if (replacer == null) continue;
			repaletters.add(new StructureRepaletterManager.Entry(entry.pieces(), entry.shouldApplyToAfterPlace(), replacer));
		}
		if (!repaletters.isEmpty()) this.repaletters = repaletters;
	}

	@Override
	public void setRepaletters(@Nullable ArrayList<StructureRepaletterManager.Entry> repaletters) {
		this.repaletters = repaletters;
	}

	@Inject(method = "createTag", at = @At("RETURN"))
	private void serializeRepaletters(StructurePieceSerializationContext context, ChunkPos pos, CallbackInfoReturnable<CompoundTag> info) {
		ArrayList<StructureRepaletterManager.Entry> repaletters = this.repaletters;
		if (repaletters == null) return;
		ListTag repalettersTag = new ListTag();
		for (StructureRepaletterManager.Entry entry : repaletters) {
			var result = StructureRepaletterManager.Entry.CODEC.encodeStart(NbtOps.INSTANCE, entry);
			var error = result.error();
			if (error.isPresent()) {
				Blueprint.LOGGER.error("Failed to encode Structure Repaletter Entry: {}", error.get().message());
				continue;
			}
			repalettersTag.add(result.result().get());
		}
		info.getReturnValue().put(StructureRepaletterManager.Entry.KEY, repalettersTag);
	}

	@Inject(method = "loadStaticStart", at = @At("RETURN"))
	private static void deserializeRepaletters(StructurePieceSerializationContext context, CompoundTag tag, long seed, CallbackInfoReturnable<StructureStart> info) {
		StructureStart start = info.getReturnValue();
		if (!((Object) start instanceof RepalettedStructureStart repalettedStructureStart)) return;
		ListTag repalettersTag = tag.getList(StructureRepaletterManager.Entry.KEY, 10);
		if (repalettersTag.isEmpty()) return;
		ArrayList<StructureRepaletterManager.Entry> repaletters = new ArrayList<>();
		for (int i = 0; i < repalettersTag.size(); i++) {
			CompoundTag compoundTag = repalettersTag.getCompound(i);
			var result = StructureRepaletterManager.Entry.CODEC.decode(NbtOps.INSTANCE, compoundTag);
			var error = result.error();
			if (error.isPresent()) {
				Blueprint.LOGGER.error("Failed to decode Structure Repaletter Entry: {}", error.get().message());
				continue;
			}
			repaletters.add(result.result().get().getFirst());
		}
		repalettedStructureStart.setRepaletters(repaletters);
	}

	@Inject(method = "placeInChunk", at = @At("HEAD"))
	public void updateStructureRepalleterRandomSource(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource randomSource, BoundingBox bounds, ChunkPos pos, CallbackInfo info) {
		StructureRepaletterManager.updateRandomSource(randomSource);
	}

	@Inject(method = "placeInChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/StructurePiece;postProcess(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/core/BlockPos;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void updateActiveRepalletersForPieceType(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource randomSource, BoundingBox bounds, ChunkPos pos, CallbackInfo info, List<StructurePiece> list, BoundingBox boundingbox, BlockPos blockpos, BlockPos blockpos1, Iterator<StructurePiece> iterator, StructurePiece structurepiece) {
		var structurePieces = level.registryAccess().registryOrThrow(Registries.STRUCTURE_PIECE);
		StructureRepaletterManager.updateActiveRepaletters(this.repaletters, structurePieces.getHolderOrThrow(structurePieces.getResourceKey(structurepiece.getType()).orElseThrow()));
	}

	@Inject(method = "placeInChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/Structure;afterPlace(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/levelgen/structure/pieces/PiecesContainer;)V"))
	private void updateActiveRepalletersForAfterPlace(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource randomSource, BoundingBox bounds, ChunkPos pos, CallbackInfo info) {
		StructureRepaletterManager.updateActiveRepaletters(this.repaletters, null);
	}

	@Inject(method = "placeInChunk", at = @At("RETURN"))
	public void resetStructureRepalleterManager(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource randomSource, BoundingBox bounds, ChunkPos pos, CallbackInfo info) {
		StructureRepaletterManager.reset();
	}
}
