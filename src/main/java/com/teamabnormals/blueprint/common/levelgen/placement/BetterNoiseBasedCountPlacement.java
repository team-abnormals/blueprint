package com.teamabnormals.blueprint.common.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.registry.BlueprintPlacementModifierTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class BetterNoiseBasedCountPlacement extends PlacementModifier {
	public static final Codec<BetterNoiseBasedCountPlacement> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				NormalNoise.NoiseParameters.CODEC.fieldOf("noise").forGetter(placement -> placement.noiseParameters),
				Codec.INT.fieldOf("noise_to_count_ratio").forGetter((placement) -> placement.noiseToCountRatio),
				Codec.DOUBLE.fieldOf("noise_offset").orElse(0.0D).forGetter((placement) -> placement.noiseOffset)
		).apply(instance, BetterNoiseBasedCountPlacement::new);
	});
	private final Holder<NormalNoise.NoiseParameters> noiseParameters;
	private final int noiseToCountRatio;
	private final double noiseOffset;
	private volatile boolean initialized;
	private NormalNoise noise;

	public BetterNoiseBasedCountPlacement(Holder<NormalNoise.NoiseParameters> noiseParameters, int noiseToCountRatio, double noiseOffset) {
		this.noiseParameters = noiseParameters;
		this.noiseToCountRatio = noiseToCountRatio;
		this.noiseOffset = noiseOffset;
	}

	@Override
	public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
		if (!this.initialized) {
			synchronized (this) {
				if (!this.initialized) {
					this.noise = NormalNoise.create(WorldgenRandom.Algorithm.LEGACY.newInstance(context.getLevel().getSeed()).forkPositional().fromHashOf(this.noiseParameters.unwrapKey().orElseThrow().location()), this.noiseParameters.value());
					this.initialized = true;
				}
			}
		}
		return IntStream.range(0, (int) Math.ceil((this.noise.getValue(pos.getX(), 0.0F, pos.getZ()) + this.noiseOffset) * this.noiseToCountRatio)).mapToObj((i) -> pos);
	}

	@Override
	public PlacementModifierType<?> type() {
		return BlueprintPlacementModifierTypes.BETTER_NOISE_BASED_COUNT.get();
	}
}