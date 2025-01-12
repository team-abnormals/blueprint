package com.teamabnormals.blueprint.common.world.modification.chunk.modifiers;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.world.modification.chunk.ChunkGeneratorModifierSerializers;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.mixin.NoiseGeneratorSettingsMixin;
import com.teamabnormals.blueprint.core.registry.BlueprintSurfaceRules;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.ArrayList;

/**
 * A {@link ChunkGeneratorModifier} subclass that modifies the surface rule of a {@link ChunkGenerator} instance.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class SurfaceRuleModifier implements ChunkGeneratorModifier<SurfaceRuleModifier> {
	public static final Codec<SurfaceRuleModifier> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				SurfaceRules.RuleSource.CODEC.fieldOf("surface_rule").forGetter(modifier -> modifier.surfaceRule),
				Codec.BOOL.optionalFieldOf("replace", false).forGetter(modifier -> modifier.replace)
		).apply(instance, SurfaceRuleModifier::new);
	});
	private final SurfaceRules.RuleSource surfaceRule;
	private final boolean replace;

	public SurfaceRuleModifier(SurfaceRules.RuleSource surfaceRule, boolean replace) {
		this.surfaceRule = surfaceRule;
		this.replace = replace;
	}

	@Override
	public void modify(ChunkGenerator chunkGenerator) {
		if (chunkGenerator instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator) {
			SurfaceRules.RuleSource newRuleSource;
			NoiseGeneratorSettings settings = noiseBasedChunkGenerator.generatorSettings().value();
			if (this.replace) newRuleSource = this.surfaceRule;
			else {
				SurfaceRules.RuleSource ruleSource = settings.surfaceRule();
				if (ruleSource instanceof BlueprintSurfaceRules.TransientMergedRuleSource transientMergedRuleSource) {
					transientMergedRuleSource.sequence().add(0, this.surfaceRule);
					newRuleSource = ruleSource;
				} else if (ruleSource instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
					var sequence = sequenceRuleSource.sequence();
					ArrayList<SurfaceRules.RuleSource> newSequence = new ArrayList<>(sequence.size() + 1);
					newSequence.add(this.surfaceRule);
					newSequence.addAll(sequence);
					newRuleSource = new BlueprintSurfaceRules.TransientMergedRuleSource(newSequence, sequenceRuleSource);
				} else newRuleSource = new BlueprintSurfaceRules.TransientMergedRuleSource(Lists.newArrayList(this.surfaceRule, ruleSource), ruleSource);
			}
			((NoiseGeneratorSettingsMixin) (Object) settings).setSurfaceRule(newRuleSource);
		} else Blueprint.LOGGER.warn("Could not apply surface rule modifier because " + chunkGenerator + " was not an instance of NoiseBasedChunkGenerator");
	}

	@Override
	public Serializer getSerializer() {
		return ChunkGeneratorModifierSerializers.SURFACE_RULE;
	}

	public static final class Serializer implements ChunkGeneratorModifier.Serializer<SurfaceRuleModifier> {
		@Override
		public JsonElement serialize(SurfaceRuleModifier modifier, RegistryOps<JsonElement> additional) throws JsonParseException {
			var dataResult = CODEC.encodeStart(additional, modifier);
			var result = dataResult.result();
			if (result.isPresent()) return result.get();
			throw new JsonParseException(dataResult.error().get().message());
		}

		@Override
		public SurfaceRuleModifier deserialize(JsonElement element, RegistryOps<JsonElement> additional) throws JsonParseException {
			var dataResult = CODEC.decode(additional, element);
			var result = dataResult.result();
			if (result.isPresent()) return result.get().getFirst();
			throw new JsonParseException(dataResult.error().get().message());
		}
	}
}
