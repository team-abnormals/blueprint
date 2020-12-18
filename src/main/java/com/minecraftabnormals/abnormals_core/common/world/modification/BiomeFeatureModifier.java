package com.minecraftabnormals.abnormals_core.common.world.modification;

import com.google.common.collect.Sets;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
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
	public static final Set<GenerationStage.Decoration> ALL_STAGES = Sets.newHashSet(GenerationStage.Decoration.values());

	private BiomeFeatureModifier(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Consumer<BiomeModificationContext> modifier) {
		super(shouldModify, modifier);
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for adding a {@link ConfiguredFeature} for a {@link GenerationStage}.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stage        The {@link GenerationStage} to add the {@link ConfiguredFeature} to.
	 * @param feature      A {@link ConfiguredFeature} to add.
	 * @return A {@link BiomeFeatureModifier} for adding a {@link ConfiguredFeature} for a {@link GenerationStage}.
	 */
	public static BiomeFeatureModifier createFeatureAdder(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, GenerationStage.Decoration stage, Supplier<ConfiguredFeature<?, ?>> feature) {
		return new BiomeFeatureModifier(shouldModify, context -> context.event.getGeneration().getFeatures(stage).add(feature));
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for adding multiple {@link ConfiguredFeature}s for a {@link GenerationStage}.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stage        The {@link GenerationStage} to add the {@link ConfiguredFeature} to.
	 * @param features     A set of {@link ConfiguredFeature}s to add.
	 * @return A {@link BiomeFeatureModifier} for adding multiple {@link ConfiguredFeature}s for a {@link GenerationStage}.
	 */
	public static BiomeFeatureModifier createMultiFeatureAdder(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, GenerationStage.Decoration stage, Set<Supplier<ConfiguredFeature<?, ?>>> features) {
		return new BiomeFeatureModifier(shouldModify, context -> context.event.getGeneration().getFeatures(stage).addAll(features));
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for adding multiple {@link ConfiguredFeature}s for multiple {@link GenerationStage}s.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stages       A set of {@link GenerationStage} to add the {@link ConfiguredFeature} to.
	 * @param features     A set of {@link ConfiguredFeature}s to add.
	 * @return A {@link BiomeFeatureModifier} for adding multiple {@link ConfiguredFeature}s for multiple {@link GenerationStage}s.
	 */
	public static BiomeFeatureModifier createMultiFeatureAdder(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Set<GenerationStage.Decoration> stages, Set<Supplier<ConfiguredFeature<?, ?>>> features) {
		return new BiomeFeatureModifier(shouldModify, context -> {
			BiomeGenerationSettingsBuilder builder = context.event.getGeneration();
			for (GenerationStage.Decoration stage : stages) {
				builder.getFeatures(stage).addAll(features);
			}
		});
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for removing a {@link Feature} for a set of {@link GenerationStage}s.
	 *
	 * @param shouldModify    A {@link BiPredicate} for what biomes it should modify.
	 * @param stages          A set of {@link GenerationStage} to remove the {@link Feature} for.
	 * @param featureSupplier The {@link Feature} to remove.
	 * @return A {@link BiomeFeatureModifier} for removing a {@link Feature} for a set of {@link GenerationStage}s.
	 */
	public static BiomeFeatureModifier createFeatureRemover(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Set<GenerationStage.Decoration> stages, Supplier<Feature<?>> featureSupplier) {
		return new BiomeFeatureModifier(shouldModify, context -> {
			BiomeGenerationSettingsBuilder settingsBuilder = context.event.getGeneration();
			for (GenerationStage.Decoration stage : stages) {
				Feature<?> toRemove = featureSupplier.get();
				settingsBuilder.getFeatures(stage).removeIf(configuredFeatureSupplier -> {
					IFeatureConfig config = configuredFeatureSupplier.get().config;
					return config instanceof DecoratedFeatureConfig && toRemove == ((DecoratedFeatureConfig) config).feature.get().feature;
				});
			}
		});
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for removing a set of {@link Feature}s for a set of {@link GenerationStage}s.
	 *
	 * @param shouldModify     A {@link BiPredicate} for what biomes it should modify.
	 * @param stages           A set of {@link GenerationStage} to remove the {@link Feature} for.
	 * @param featureSuppliers A set of {@link Feature}s to remove.
	 * @return A {@link BiomeFeatureModifier} for removing a set of {@link Feature}s for a set of {@link GenerationStage}s.
	 */
	public static BiomeFeatureModifier createMultiFeatureRemover(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Set<GenerationStage.Decoration> stages, Set<Supplier<Feature<?>>> featureSuppliers) {
		return new BiomeFeatureModifier(shouldModify, context -> {
			Set<Feature<?>> features = Sets.newHashSet();
			featureSuppliers.forEach(featureSupplier -> features.add(featureSupplier.get()));
			BiomeGenerationSettingsBuilder settingsBuilder = context.event.getGeneration();
			for (GenerationStage.Decoration stage : stages) {
				settingsBuilder.getFeatures(stage).removeIf(configuredFeatureSupplier -> {
					IFeatureConfig config = configuredFeatureSupplier.get().config;
					return config instanceof DecoratedFeatureConfig && features.contains(((DecoratedFeatureConfig) config).feature.get().feature);
				});
			}
		});
	}

	/**
	 * Creates a {@link BiomeFeatureModifier} for replacing {@link ConfiguredFeature}s for a {@link Feature} with another {@link ConfiguredFeature} for a set of {@link GenerationStage}s.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stages       A set of {@link GenerationStage} to remove the {@link Feature} for.
	 * @param toReplace    The {@link Feature} to replace.
	 * @param replacer     A {@link ConfiguredFeature} to replace the other {@link ConfiguredFeature} with.
	 * @return A {@link BiomeFeatureModifier} for replacing {@link ConfiguredFeature}s for a {@link Feature} with another {@link ConfiguredFeature} for a set of {@link GenerationStage}s.
	 */
	public static BiomeFeatureModifier createFeatureReplacer(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Set<GenerationStage.Decoration> stages, Supplier<Feature<?>> toReplace, Supplier<ConfiguredFeature<?, ?>> replacer) {
		return new BiomeFeatureModifier(shouldModify, context -> {
			BiomeGenerationSettingsBuilder settingsBuilder = context.event.getGeneration();
			Feature<?> replace = toReplace.get();
			for (GenerationStage.Decoration stage : stages) {
				Set<Supplier<ConfiguredFeature<?, ?>>> toRemove = Sets.newHashSet();
				List<Supplier<ConfiguredFeature<?, ?>>> features = settingsBuilder.getFeatures(stage);
				for (Supplier<ConfiguredFeature<?, ?>> configuredFeatureSupplier : features) {
					IFeatureConfig config = configuredFeatureSupplier.get().config;
					if (config instanceof DecoratedFeatureConfig && ((DecoratedFeatureConfig) config).feature.get().feature == replace) {
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
	 * Creates a {@link BiomeFeatureModifier} for replacing {@link ConfiguredFeature}s for multiple {@link Feature}s with another {@link ConfiguredFeature} for a set of {@link GenerationStage}s.
	 *
	 * @param shouldModify A {@link BiPredicate} for what biomes it should modify.
	 * @param stages       A set of {@link GenerationStage} to remove the {@link Feature}s for.
	 * @param toReplace    A set of {@link Feature}s to replace.
	 * @param replacer     A {@link ConfiguredFeature} to replace the other {@link ConfiguredFeature}s with.
	 * @return A {@link BiomeFeatureModifier} for replacing {@link ConfiguredFeature}s for multiple {@link Feature}s with another {@link ConfiguredFeature} for a set of {@link GenerationStage}s.
	 */
	public static BiomeFeatureModifier createMultiFeatureReplacer(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Set<GenerationStage.Decoration> stages, Set<Supplier<Feature<?>>> toReplace, Supplier<ConfiguredFeature<?, ?>> replacer) {
		return new BiomeFeatureModifier(shouldModify, context -> {
			Set<Feature<?>> features = Sets.newHashSet();
			toReplace.forEach(featureSupplier -> features.add(featureSupplier.get()));
			BiomeGenerationSettingsBuilder settingsBuilder = context.event.getGeneration();
			for (GenerationStage.Decoration stage : stages) {
				Set<Supplier<ConfiguredFeature<?, ?>>> toRemove = Sets.newHashSet();
				List<Supplier<ConfiguredFeature<?, ?>>> configuredFeatures = settingsBuilder.getFeatures(stage);
				for (Supplier<ConfiguredFeature<?, ?>> configuredFeatureSupplier : configuredFeatures) {
					IFeatureConfig config = configuredFeatureSupplier.get().config;
					if (config instanceof DecoratedFeatureConfig && features.contains(((DecoratedFeatureConfig) config).feature.get().feature)) {
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
