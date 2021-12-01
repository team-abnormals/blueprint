package com.teamabnormals.blueprint.common.world.modification;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
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
	 * Creates a {@link BiomeFeatureModifier} for adding a {@link PlacedFeature} for a {@link GenerationStep}.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stage        The {@link GenerationStep} to add the {@link PlacedFeature} to.
	 * @param feature      A {@link PlacedFeature} to add.
	 * @return A {@link BiomeFeatureModifier} for adding a {@link PlacedFeature} for a {@link GenerationStep}.
	 */
	public static BiomeFeatureModifier createFeatureAdder(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, GenerationStep.Decoration stage, Supplier<PlacedFeature> feature) {
		return new BiomeFeatureModifier(shouldModify, context -> context.event.getGeneration().getFeatures(stage).add(feature));
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for adding multiple {@link PlacedFeature}s for a {@link GenerationStep}.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stage        The {@link GenerationStep} to add the {@link PlacedFeature} to.
	 * @param features     A set of {@link PlacedFeature}s to add.
	 * @return A {@link BiomeFeatureModifier} for adding multiple {@link PlacedFeature}s for a {@link GenerationStep}.
	 */
	public static BiomeFeatureModifier createMultiFeatureAdder(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, GenerationStep.Decoration stage, Set<Supplier<PlacedFeature>> features) {
		return new BiomeFeatureModifier(shouldModify, context -> context.event.getGeneration().getFeatures(stage).addAll(features));
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for adding multiple {@link PlacedFeature}s for multiple {@link GenerationStep}s.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stages       A set of {@link GenerationStep} to add the {@link PlacedFeature} to.
	 * @param features     A set of {@link PlacedFeature}s to add.
	 * @return A {@link BiomeFeatureModifier} for adding multiple {@link PlacedFeature}s for multiple {@link GenerationStep}s.
	 */
	public static BiomeFeatureModifier createMultiFeatureAdder(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Set<GenerationStep.Decoration> stages, Set<Supplier<PlacedFeature>> features) {
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
				settingsBuilder.getFeatures(stage).removeIf(PlacedFeatureSupplier -> {
					FeatureConfiguration config = PlacedFeatureSupplier.get().config;
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
				settingsBuilder.getFeatures(stage).removeIf(PlacedFeatureSupplier -> {
					FeatureConfiguration config = PlacedFeatureSupplier.get().config;
					return config instanceof DecoratedFeatureConfiguration && features.contains(((DecoratedFeatureConfiguration) config).feature.get().feature);
				});
			}
		});
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for replacing {@link PlacedFeature}s for a {@link Feature} with another {@link PlacedFeature} for a set of {@link GenerationStep}s.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stages       A set of {@link GenerationStep} to remove the {@link Feature} for.
	 * @param toReplace    The {@link Feature} to replace.
	 * @param replacer     A {@link PlacedFeature} to replace the other {@link PlacedFeature} with.
	 * @return A {@link BiomeFeatureModifier} for replacing {@link PlacedFeature}s for a {@link Feature} with another {@link PlacedFeature} for a set of {@link GenerationStep}s.
	 */
	public static BiomeFeatureModifier createFeatureReplacer(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Set<GenerationStep.Decoration> stages, Supplier<Feature<?>> toReplace, Supplier<PlacedFeature> replacer) {
		return new BiomeFeatureModifier(shouldModify, context -> {
			BiomeGenerationSettingsBuilder settingsBuilder = context.event.getGeneration();
			Feature<?> replace = toReplace.get();
			for (GenerationStep.Decoration stage : stages) {
				Set<Supplier<PlacedFeature>> toRemove = Sets.newHashSet();
				List<Supplier<PlacedFeature>> features = settingsBuilder.getFeatures(stage);
				for (Supplier<PlacedFeature> PlacedFeatureSupplier : features) {
					FeatureConfiguration config = PlacedFeatureSupplier.get().config;
					if (config instanceof DecoratedFeatureConfiguration && ((DecoratedFeatureConfiguration) config).feature.get().feature == replace) {
						toRemove.add(PlacedFeatureSupplier);
					}
				}
				toRemove.forEach(PlacedFeature -> {
					features.remove(PlacedFeature);
					features.add(replacer);
				});
			}
		});
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for replacing {@link PlacedFeature}s for multiple {@link Feature}s with another {@link PlacedFeature} for a set of {@link GenerationStep}s.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stages       A set of {@link GenerationStep} to remove the {@link Feature}s for.
	 * @param toReplace    A set of {@link Feature}s to replace.
	 * @param replacer     A {@link PlacedFeature} to replace the other {@link PlacedFeature}s with.
	 * @return A {@link BiomeFeatureModifier} for replacing {@link PlacedFeature}s for multiple {@link Feature}s with another {@link PlacedFeature} for a set of {@link GenerationStep}s.
	 */
	public static BiomeFeatureModifier createMultiFeatureReplacer(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Set<GenerationStep.Decoration> stages, Set<Supplier<Feature<?>>> toReplace, Supplier<PlacedFeature> replacer) {
		return new BiomeFeatureModifier(shouldModify, context -> {
			Set<Feature<?>> features = Sets.newHashSet();
			toReplace.forEach(featureSupplier -> features.add(featureSupplier.get()));
			BiomeGenerationSettingsBuilder settingsBuilder = context.event.getGeneration();
			for (GenerationStep.Decoration stage : stages) {
				Set<Supplier<PlacedFeature>> toRemove = Sets.newHashSet();
				List<Supplier<PlacedFeature>> PlacedFeatures = settingsBuilder.getFeatures(stage);
				for (Supplier<PlacedFeature> PlacedFeatureSupplier : PlacedFeatures) {
					FeatureConfiguration config = PlacedFeatureSupplier.get().config;
					if (config instanceof DecoratedFeatureConfiguration && features.contains(((DecoratedFeatureConfiguration) config).feature.get().feature)) {
						toRemove.add(PlacedFeatureSupplier);
					}
				}
				toRemove.forEach(PlacedFeature -> {
					PlacedFeatures.remove(PlacedFeature);
					PlacedFeatures.add(replacer);
				});
			}
		});
	}
}
