package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.world.modification.structure.RepalettedStructureStart;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureModificationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(Structure.class)
public final class StructureMixin {
	@Inject(method = "generate", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void captureStructureContextForRepaletters(
			RegistryAccess registryAccess, ChunkGenerator chunkGenerator,
			BiomeSource biomeSource, RandomState random, StructureTemplateManager manager,
			long seed, ChunkPos chunkPos, int references, LevelHeightAccessor heightAccessor,
			Predicate<Holder<Biome>> biomePredicate, CallbackInfoReturnable<StructureStart> info,
			Structure.GenerationContext context, Optional<Structure.GenerationStub> generationStub
	) {
		StructureStart start = info.getReturnValue();
		if (start.isValid() && generationStub.isPresent() && (Object) start instanceof RepalettedStructureStart repalettedStart) {
			repalettedStart.initializeRepaletters(new StructureModificationContext(context, generationStub.get().position()));
		}
	}
}
