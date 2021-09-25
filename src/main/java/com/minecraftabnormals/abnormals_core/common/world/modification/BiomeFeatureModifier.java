package com.minecraftabnormals.abnormals_core.common.world.modification;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A form of {@link BiomeModifier} for modifying features of a biome.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeFeatureModifier extends BiomeModifier {
	public static final Set<GenerationStep.Decoration> ALL_STAGES = Sets.newHashSet(GenerationStep.Decoration.values());

	private BiomeFeatureModifier(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Consumer<BiomeModificationContext> modifier) {
		super(shouldModify, modifier);
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for adding a {@link ConfiguredFeature} for a {@link GenerationStep}.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stage        The {@link GenerationStep} to add the {@link ConfiguredFeature} to.
	 * @param feature      A {@link ConfiguredFeature} to add.
	 * @return A {@link BiomeFeatureModifier} for adding a {@link ConfiguredFeature} for a {@link GenerationStep}.
	 */
	public static BiomeFeatureModifier createFeatureAdder(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, GenerationStep.Decoration stage, Supplier<ConfiguredFeature<?, ?>> feature) {
		return new BiomeFeatureModifier(shouldModify, context -> context.event.getGeneration().getFeatures(stage).add(feature));
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for adding multiple {@link ConfiguredFeature}s for a {@link GenerationStep}.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stage        The {@link GenerationStep} to add the {@link ConfiguredFeature} to.
	 * @param features     A set of {@link ConfiguredFeature}s to add.
	 * @return A {@link BiomeFeatureModifier} for adding multiple {@link ConfiguredFeature}s for a {@link GenerationStep}.
	 */
	public static BiomeFeatureModifier createMultiFeatureAdder(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, GenerationStep.Decoration stage, Set<Supplier<ConfiguredFeature<?, ?>>> features) {
		return new BiomeFeatureModifier(shouldModify, context -> context.event.getGeneration().getFeatures(stage).addAll(features));
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for adding multiple {@link ConfiguredFeature}s for multiple {@link GenerationStep}s.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stages       A set of {@link GenerationStep} to add the {@link ConfiguredFeature} to.
	 * @param features     A set of {@link ConfiguredFeature}s to add.
	 * @return A {@link BiomeFeatureModifier} for adding multiple {@link ConfiguredFeature}s for multiple {@link GenerationStep}s.
	 */
	public static BiomeFeatureModifier createMultiFeatureAdder(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Set<GenerationStep.Decoration> stages, Set<Supplier<ConfiguredFeature<?, ?>>> features) {
		return new BiomeFeatureModifier(shouldModify, context -> {
			BiomeGenerationSettingsBuilder builder = context.event.getGeneration();
			for (GenerationStep.Decoration stage : stages) {
				builder.getFeatures(stage).addAll(features);
			}
		});
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for removing a {@link Feature} for a set of {@link GenerationStep}s.
	 *
	 * @param shouldModify    A {@link BiPredicate} for what biomes it should modify.
	 * @param stages          A set of {@link GenerationStep} to remove the {@link Feature} for.
	 * @param featureSupplier The {@link Feature} to remove.
	 * @return A {@link BiomeFeatureModifier} for removing a {@link Feature} for a set of {@link GenerationStep}s.
	 */
	public static BiomeFeatureModifier createFeatureRemover(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Set<GenerationStep.Decoration> stages, Supplier<Feature<?>> featureSupplier) {
		return new BiomeFeatureModifier(shouldModify, context -> {
			BiomeGenerationSettingsBuilder settingsBuilder = context.event.getGeneration();
			for (GenerationStep.Decoration stage : stages) {
				Feature<?> toRemove = featureSupplier.get();
				settingsBuilder.getFeatures(stage).removeIf(configuredFeatureSupplier -> {
					FeatureConfiguration config = configuredFeatureSupplier.get().config;
					return config instanceof DecoratedFeatureConfiguration && toRemove == ((DecoratedFeatureConfiguration) config).feature.get().feature;
				});
			}
		});
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for removing a set of {@link Feature}s for a set of {@link GenerationStep}s.
	 *
	 * @param shouldModify     A {@link BiPredicate} for what biomes it should modify.
	 * @param stages           A set of {@link GenerationStep} to remove the {@link Feature} for.
	 * @param featureSuppliers A set of {@link Feature}s to remove.
	 * @return A {@link BiomeFeatureModifier} for removing a set of {@link Feature}s for a set of {@link GenerationStep}s.
	 */
	public static BiomeFeatureModifier createMultiFeatureRemover(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Set<GenerationStep.Decoration> stages, Set<Supplier<Feature<?>>> featureSuppliers) {
		return new BiomeFeatureModifier(shouldModify, context -> {
			Set<Feature<?>> features = Sets.newHashSet();
			featureSuppliers.forEach(featureSupplier -> features.add(featureSupplier.get()));
			BiomeGenerationSettingsBuilder settingsBuilder = context.event.getGeneration();
			for (GenerationStep.Decoration stage : stages) {
				settingsBuilder.getFeatures(stage).removeIf(configuredFeatureSupplier -> {
					FeatureConfiguration config = configuredFeatureSupplier.get().config;
					return config instanceof DecoratedFeatureConfiguration && features.contains(((DecoratedFeatureConfiguration) config).feature.get().feature);
				});
			}
		});
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for replacing {@link ConfiguredFeature}s for a {@link Feature} with another {@link ConfiguredFeature} for a set of {@link GenerationStep}s.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stages       A set of {@link GenerationStep} to remove the {@link Feature} for.
	 * @param toReplace    The {@link Feature} to replace.
	 * @param replacer     A {@link ConfiguredFeature} to replace the other {@link ConfiguredFeature} with.
	 * @return A {@link BiomeFeatureModifier} for replacing {@link ConfiguredFeature}s for a {@link Feature} with another {@link ConfiguredFeature} for a set of {@link GenerationStep}s.
	 */
	public static BiomeFeatureModifier createFeatureReplacer(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Set<GenerationStep.Decoration> stages, Supplier<Feature<?>> toReplace, Supplier<ConfiguredFeature<?, ?>> replacer) {
		return new BiomeFeatureModifier(shouldModify, context -> {
			BiomeGenerationSettingsBuilder settingsBuilder = context.event.getGeneration();
			Feature<?> replace = toReplace.get();
			for (GenerationStep.Decoration stage : stages) {
				Set<Supplier<ConfiguredFeature<?, ?>>> toRemove = Sets.newHashSet();
				List<Supplier<ConfiguredFeature<?, ?>>> features = settingsBuilder.getFeatures(stage);
				for (Supplier<ConfiguredFeature<?, ?>> configuredFeatureSupplier : features) {
					FeatureConfiguration config = configuredFeatureSupplier.get().config;
					if (config instanceof DecoratedFeatureConfiguration && ((DecoratedFeatureConfiguration) config).feature.get().feature == replace) {
						toRemove.add(configuredFeatureSupplier);
					}
				}
				toRemove.forEach(configuredFeature -> {
					features.remove(configuredFeature);
					features.add(replacer);
				});
			}
		});
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for replacing {@link ConfiguredFeature}s for multiple {@link Feature}s with another {@link ConfiguredFeature} for a set of {@link GenerationStep}s.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stages       A set of {@link GenerationStep} to remove the {@link Feature}s for.
	 * @param toReplace    A set of {@link Feature}s to replace.
	 * @param replacer     A {@link ConfiguredFeature} to replace the other {@link ConfiguredFeature}s with.
	 * @return A {@link BiomeFeatureModifier} for replacing {@link ConfiguredFeature}s for multiple {@link Feature}s with another {@link ConfiguredFeature} for a set of {@link GenerationStep}s.
	 */
	public static BiomeFeatureModifier createMultiFeatureReplacer(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Set<GenerationStep.Decoration> stages, Set<Supplier<Feature<?>>> toReplace, Supplier<ConfiguredFeature<?, ?>> replacer) {
		return new BiomeFeatureModifier(shouldModify, context -> {
			Set<Feature<?>> features = Sets.newHashSet();
			toReplace.forEach(featureSupplier -> features.add(featureSupplier.get()));
			BiomeGenerationSettingsBuilder settingsBuilder = context.event.getGeneration();
			for (GenerationStep.Decoration stage : stages) {
				Set<Supplier<ConfiguredFeature<?, ?>>> toRemove = Sets.newHashSet();
				List<Supplier<ConfiguredFeature<?, ?>>> configuredFeatures = settingsBuilder.getFeatures(stage);
				for (Supplier<ConfiguredFeature<?, ?>> configuredFeatureSupplier : configuredFeatures) {
					FeatureConfiguration config = configuredFeatureSupplier.get().config;
					if (config instanceof DecoratedFeatureConfiguration && features.contains(((DecoratedFeatureConfiguration) config).feature.get().feature)) {
						toRemove.add(configuredFeatureSupplier);
					}
				}
				toRemove.forEach(configuredFeature -> {
					configuredFeatures.remove(configuredFeature);
					configuredFeatures.add(replacer);
				});
			}
		});
	}
}
