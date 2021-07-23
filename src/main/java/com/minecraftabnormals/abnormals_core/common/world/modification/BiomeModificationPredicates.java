package com.minecraftabnormals.abnormals_core.common.world.modification;

import com.google.common.collect.Sets;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Set;
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
	 * Creates a {@link BiPredicate} true for any {@link Biome} {@link RegistryKey} in a set of {@link RegistryKey}s.
	 *
	 * @param biomeKeys A set of {@link RegistryKey}s to test.
	 * @return A {@link BiPredicate} true for any {@link Biome} {@link RegistryKey} in a set of {@link RegistryKey}s.
	 */
	public static BiPredicate<RegistryKey<Biome>, Biome> forBiomeKeys(Set<RegistryKey<Biome>> biomeKeys) {
		return (biomeRegistryKey, biome) -> biomeKeys.contains(biomeRegistryKey);
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
		return (biomeRegistryKey, biome) -> biome.getBiomeCategory() == category;
	}

	/**
	 * Creates a {@link BiPredicate} true for a set of {@link Biome.Category}s.
	 *
	 * @param categories An array of {@link Biome.Category}s.
	 * @return A {@link BiPredicate} true for a set of {@link Biome.Category}s.
	 */
	public static BiPredicate<RegistryKey<Biome>, Biome> forCategory(Biome.Category... categories) {
		return (biomeRegistryKey, biome) -> Sets.newHashSet(categories).contains(biome.getBiomeCategory());
	}

	/**
	 * Creates a {@link BiPredicate} that's true if a biome key contains a specified {@link BiomeDictionary.Type}.
	 *
	 * @param type A {@link BiomeDictionary.Type} to test.
	 * @return A {@link BiPredicate} that's true if a biome key contains a specified {@link BiomeDictionary.Type}.
	 */
	public static BiPredicate<RegistryKey<Biome>, Biome> forType(BiomeDictionary.Type type) {
		return (biomeRegistryKey, biome) -> BiomeDictionary.hasType(biomeRegistryKey, type);
	}

	/**
	 * Creates a {@link BiPredicate} that's true if a biome key contains all of the {@link BiomeDictionary.Type}s specified in an array of {@link BiomeDictionary.Type}s.
	 *
	 * @param types An array of {@link BiomeDictionary.Type}s to test.
	 * @return A {@link BiPredicate} that's true if a biome key contains all of the {@link BiomeDictionary.Type}s specified in an array of {@link BiomeDictionary.Type}s.
	 */
	public static BiPredicate<RegistryKey<Biome>, Biome> forTypes(BiomeDictionary.Type... types) {
		return (biomeRegistryKey, biome) -> {
			Set<BiomeDictionary.Type> dictTypes = BiomeDictionary.getTypes(biomeRegistryKey);
			for (BiomeDictionary.Type type : types) {
				if (!dictTypes.contains(type)) {
					return false;
				}
			}
			return true;
		};
	}

	/**
	 * Creates a {@link BiPredicate} that's true if a biome key contains one of the {@link BiomeDictionary.Type}s specified in an array of {@link BiomeDictionary.Type}s.
	 *
	 * @param types An array of {@link BiomeDictionary.Type}s to test.
	 * @return A {@link BiPredicate} that's true if a biome key contains one of the {@link BiomeDictionary.Type}s specified in an array of {@link BiomeDictionary.Type}s.
	 */
	public static BiPredicate<RegistryKey<Biome>, Biome> forAnyType(BiomeDictionary.Type... types) {
		return (biomeRegistryKey, biome) -> {
			Set<BiomeDictionary.Type> dictTypes = BiomeDictionary.getTypes(biomeRegistryKey);
			for (BiomeDictionary.Type type : types) {
				if (dictTypes.contains(type)) {
					return true;
				}
			}
			return false;
		};
	}
}
