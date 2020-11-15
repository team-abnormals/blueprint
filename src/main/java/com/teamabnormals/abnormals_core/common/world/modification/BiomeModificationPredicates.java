package com.teamabnormals.abnormals_core.common.world.modification;

import com.google.common.collect.Sets;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * A class for storing some basic predicates for biome modification.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeModificationPredicates {
	public static final BiPredicate<RegistryKey<Biome>, Biome> END_ONLY = forCategory(Biome.Category.THEEND);
	public static final BiPredicate<RegistryKey<Biome>, Biome> NETHER_ONLY = forCategory(Biome.Category.NETHER);
	public static final BiPredicate<RegistryKey<Biome>, Biome> OCEAN_ONLY = forCategory(Biome.Category.OCEAN);

	/**
	 * Creates a {@link BiPredicate} true for a certain {@link Biome} {@link RegistryKey}.
	 *
	 * @param biomeKey The {@link RegistryKey} of a {@link Biome}.
	 * @return A {@link BiPredicate} true for only a certain {@link Biome} {@link RegistryKey}.
	 */
	public static BiPredicate<RegistryKey<Biome>, Biome> forBiomeKey(RegistryKey<Biome> biomeKey) {
		return (biomeRegistryKey, biome) -> biomeRegistryKey == biomeKey;
	}

	/**
	 * Creates a {@link BiPredicate} true for a certain {@link Biome}.
	 *
	 * @param biomeIn A {@link Biome}.
	 * @return A {@link BiPredicate} true for only a certain {@link Biome}
	 */
	public static BiPredicate<RegistryKey<Biome>, Biome> forBiome(Biome biomeIn) {
		return (biomeRegistryKey, biome) -> biomeIn == biome;
	}

	/**
	 * Creates a {@link BiPredicate} true for a certain {@link Biome}.
	 * <p>Similar to {@link #forBiome(Biome)}, but modified to work with deferred registry.</p>
	 *
	 * @param biomeIn A {@link Supplier} for a {@link Biome}.
	 * @return A {@link BiPredicate} true for only a certain {@link Biome}
	 */
	public static BiPredicate<RegistryKey<Biome>, Biome> forBiome(Supplier<Biome> biomeIn) {
		return (biomeRegistryKey, biome) -> biomeIn.get() == biome;
	}

	/**
	 * Creates a {@link BiPredicate} true for a certain {@link Biome.Category}.
	 *
	 * @param category A {@link Biome.Category}.
	 * @return A {@link BiPredicate} true for a certain {@link Biome.Category}.
	 */
	public static BiPredicate<RegistryKey<Biome>, Biome> forCategory(Biome.Category category) {
		return (biomeRegistryKey, biome) -> biome.getCategory() == category;
	}

	/**
	 * Creates a {@link BiPredicate} true for a set of {@link Biome.Category}s.
	 *
	 * @param categories An array of {@link Biome.Category}s.
	 * @return A {@link BiPredicate} true for a set of {@link Biome.Category}s.
	 */
	public static BiPredicate<RegistryKey<Biome>, Biome> forCategory(Biome.Category... categories) {
		return (biomeRegistryKey, biome) -> Sets.newHashSet(categories).contains(biome.getCategory());
	}
}
