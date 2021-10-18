package com.teamabnormals.blueprint.common.world.modification;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A form of {@link BiomeModifier} for modifying the ambience of a biome.
 * <p>
 * TODO: Possibly add more uses of this class
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeAmbienceModifier extends BiomeModifier {

	private BiomeAmbienceModifier(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Consumer<BiomeModificationContext> modifier) {
		super(shouldModify, modifier);
	}

	/**
	 * Creates a new {@link BiomeAmbienceModifier} that replaces a biome's {@link BiomeSpecialEffects}.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param ambience     A {@link BiomeSpecialEffects} to replace it with.
	 * @return A new {@link BiomeAmbienceModifier} that replaces a biome's {@link BiomeSpecialEffects}.
	 */
	public static BiomeAmbienceModifier createAmbienceReplacer(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Supplier<BiomeSpecialEffects> ambience) {
		return new BiomeAmbienceModifier(shouldModify, context -> context.event.setEffects(ambience.get()));
	}

}
