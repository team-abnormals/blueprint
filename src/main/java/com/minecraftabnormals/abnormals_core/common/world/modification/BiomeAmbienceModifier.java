package com.minecraftabnormals.abnormals_core.common.world.modification;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;

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

	private BiomeAmbienceModifier(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Consumer<BiomeModificationContext> modifier) {
		super(shouldModify, modifier);
	}

	/**
	 * Creates a new {@link BiomeAmbienceModifier} that replaces a biome's {@link BiomeAmbience}.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param ambience     A {@link BiomeAmbience} to replace it with.
	 * @return A new {@link BiomeAmbienceModifier} that replaces a biome's {@link BiomeAmbience}.
	 */
	public static BiomeAmbienceModifier createAmbienceReplacer(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Supplier<BiomeAmbience> ambience) {
		return new BiomeAmbienceModifier(shouldModify, context -> context.event.setEffects(ambience.get()));
	}

}
