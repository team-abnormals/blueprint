package com.teamabnormals.blueprint.common.world.modification;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
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
	public static final BiPredicate<ResourceKey<Biome>, Biome> END_ONLY = forCategory(Biome.BiomeCategory.THEEND);
	public static final BiPredicate<ResourceKey<Biome>, Biome> NETHER_ONLY = forCategory(Biome.BiomeCategory.NETHER);
	public static final BiPredicate<ResourceKey<Biome>, Biome> OCEAN_ONLY = forCategory(Biome.BiomeCategory.OCEAN);

	/**
	 * Creates a {@link BiPredicate} true for a certain {@link Biome} {@link ResourceKey}.
	 *
	 * @param biomeKey The {@link ResourceKey} of a {@link Biome}.
	 * @return A {@link BiPredicate} true for only a certain {@link Biome} {@link ResourceKey}.
	 */
	public static BiPredicate<ResourceKey<Biome>, Biome> forBiomeKey(ResourceKey<Biome> biomeKey) {
		return (biomeResourceKey, biome) -> biomeResourceKey == biomeKey;
	}

	/**
	 * Creates a {@link BiPredicate} true for any {@link Biome} {@link ResourceKey} in a set of {@link ResourceKey}s.
	 *
	 * @param biomeKeys A set of {@link ResourceKey}s to test.
	 * @return A {@link BiPredicate} true for any {@link Biome} {@link ResourceKey} in a set of {@link ResourceKey}s.
	 */
	public static BiPredicate<ResourceKey<Biome>, Biome> forBiomeKeys(Set<ResourceKey<Biome>> biomeKeys) {
		return (biomeResourceKey, biome) -> biomeKeys.contains(biomeResourceKey);
	}

	/**
	 * Creates a {@link BiPredicate} true for a certain {@link Biome}.
	 *
	 * @param biomeIn A {@link Biome}.
	 * @return A {@link BiPredicate} true for only a certain {@link Biome}
	 */
	public static BiPredicate<ResourceKey<Biome>, Biome> forBiome(Biome biomeIn) {
		return (biomeResourceKey, biome) -> biomeIn == biome;
	}

	/**
	 * Creates a {@link BiPredicate} true for a certain {@link Biome}.
	 * <p>Similar to {@link #forBiome(Biome)}, but modified to work with deferred registry.</p>
	 *
	 * @param biomeIn A {@link Supplier} for a {@link Biome}.
	 * @return A {@link BiPredicate} true for only a certain {@link Biome}
	 */
	public static BiPredicate<ResourceKey<Biome>, Biome> forBiome(Supplier<Biome> biomeIn) {
		return (biomeResourceKey, biome) -> biomeIn.get() == biome;
	}

	/**
	 * Creates a {@link BiPredicate} true for a certain {@link Biome.BiomeCategory}.
	 *
	 * @param category A {@link Biome.BiomeCategory}.
	 * @return A {@link BiPredicate} true for a certain {@link Biome.BiomeCategory}.
	 */
	public static BiPredicate<ResourceKey<Biome>, Biome> forCategory(Biome.BiomeCategory category) {
		return (biomeResourceKey, biome) -> biome.getBiomeCategory() == category;
	}

	/**
	 * Creates a {@link BiPredicate} true for a set of {@link Biome.BiomeCategory}s.
	 *
	 * @param categories An array of {@link Biome.BiomeCategory}s.
	 * @return A {@link BiPredicate} true for a set of {@link Biome.BiomeCategory}s.
	 */
	public static BiPredicate<ResourceKey<Biome>, Biome> forCategory(Biome.BiomeCategory... categories) {
		return (biomeResourceKey, biome) -> Sets.newHashSet(categories).contains(biome.getBiomeCategory());
	}

	/**
	 * Creates a {@link BiPredicate} that's true if a biome key contains a specified {@link BiomeDictionary.Type}.
	 *
	 * @param type A {@link BiomeDictionary.Type} to test.
	 * @return A {@link BiPredicate} that's true if a biome key contains a specified {@link BiomeDictionary.Type}.
	 */
	public static BiPredicate<ResourceKey<Biome>, Biome> forType(BiomeDictionary.Type type) {
		return (biomeResourceKey, biome) -> BiomeDictionary.hasType(biomeResourceKey, type);
	}

	/**
	 * Creates a {@link BiPredicate} that's true if a biome key contains all of the {@link BiomeDictionary.Type}s specified in an array of {@link BiomeDictionary.Type}s.
	 *
	 * @param types An array of {@link BiomeDictionary.Type}s to test.
	 * @return A {@link BiPredicate} that's true if a biome key contains all of the {@link BiomeDictionary.Type}s specified in an array of {@link BiomeDictionary.Type}s.
	 */
	public static BiPredicate<ResourceKey<Biome>, Biome> forTypes(BiomeDictionary.Type... types) {
		return (biomeResourceKey, biome) -> {
			Set<BiomeDictionary.Type> dictTypes = BiomeDictionary.getTypes(biomeResourceKey);
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
	public static BiPredicate<ResourceKey<Biome>, Biome> forAnyType(BiomeDictionary.Type... types) {
		return (biomeResourceKey, biome) -> {
			Set<BiomeDictionary.Type> dictTypes = BiomeDictionary.getTypes(biomeResourceKey);
			for (BiomeDictionary.Type type : types) {
				if (dictTypes.contains(type)) {
					return true;
				}
			}
			return false;
		};
	}
}
