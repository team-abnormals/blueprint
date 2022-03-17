package com.teamabnormals.blueprint.core.util.modification;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A generic {@link DataProvider} implementation for {@link TargetedModifier}s
 *
 * @param <T> The type of object that the {@link TargetedModifier}s modify.
 * @param <S> The type of additional serialization object that the {@link TargetedModifier}s use.
 * @param <D> The type of additional deserialization object that the {@link TargetedModifier}s use.
 * @author SmellyModder (Luke Tonon)
 * @see TargetedModifier
 * @see IModifier
 * @see ModifierRegistry
 * @see ProviderEntry
 */
public abstract class ModifierDataProvider<T, S, D> implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private final DataGenerator dataGenerator;
	private final String name;
	private final Gson gson;
	private final BiFunction<Path, ProviderEntry<T, S, D>, Path> pathResolver;
	private final String targetKeyName;
	private final ModifierRegistry<T, S, D> modifierRegistry;
	private final Function<TargetedModifier<T, S, D>, S> additionalSerializationGetter;
	private final List<ProviderEntry<T, S, D>> entries = new ArrayList<>();

	/**
	 * Main constructor for a {@link ModifierDataProvider}.
	 *
	 * @param dataGenerator                 A {@link DataGenerator} to get the output folder from.
	 * @param name                          The name for this {@link ModifierDataProvider}.
	 * @param gson                          A {@link Gson} object for serializing.
	 * @param pathResolver                  A {@link BiFunction} interface used to resolve the path for a given {@link ProviderEntry}.
	 * @param targetKeyName                 The name for the target key in the json files for the modifiers when generating them.
	 * @param modifierRegistry              A {@link ModifierRegistry} to lookup the names of {@link IModifier}s from.
	 * @param additionalSerializationGetter A {@link Function} for getting the additional serialization object for a given {@link TargetedModifier}.
	 */
	public ModifierDataProvider(DataGenerator dataGenerator, String name, Gson gson, BiFunction<Path, ProviderEntry<T, S, D>, Path> pathResolver, String targetKeyName, ModifierRegistry<T, S, D> modifierRegistry, Function<TargetedModifier<T, S, D>, S> additionalSerializationGetter) {
		this.dataGenerator = dataGenerator;
		this.name = name;
		this.gson = gson;
		this.pathResolver = pathResolver;
		this.targetKeyName = targetKeyName;
		this.modifierRegistry = modifierRegistry;
		this.additionalSerializationGetter = additionalSerializationGetter;
	}

	/**
	 * Simple/generic constructor for a {@link ModifierDataProvider}.
	 *
	 * @param dataGenerator    A {@link DataGenerator} to get the output folder from.
	 * @param name             The name for this {@link ModifierDataProvider}.
	 * @param gson             A {@link Gson} object for serializing.
	 * @param modId            The Mod ID to use when serializing.
	 * @param pathType         The type of path to write to when serializing. For example, "modifiers/loot_tables" for loot tables.
	 * @param modifierRegistry A {@link ModifierRegistry} to lookup the names of {@link IModifier}s from.
	 * @param serializerObject An additional serialization object to use when serializing a {@link ProviderEntry}.
	 */
	public ModifierDataProvider(DataGenerator dataGenerator, String name, Gson gson, String modId, String pathType, ModifierRegistry<T, S, D> modifierRegistry, S serializerObject) {
		this(dataGenerator, name, gson, (path, tsdProviderEntry) -> path.resolve("data/" + modId + "/" + pathType + "/" + tsdProviderEntry.name.getPath() + ".json"), "target", modifierRegistry, tsdTargetedModifier -> serializerObject);
	}

	@Override
	public void run(HashCache directoryCache) {
		Path outputFolder = this.dataGenerator.getOutputFolder();
		Set<ResourceLocation> entryNames = Sets.newHashSet();
		BiFunction<Path, ProviderEntry<T, S, D>, Path> pathResolver = this.pathResolver;
		Gson gson = this.gson;
		Function<TargetedModifier<T, S, D>, S> additionalSerializationGetter = this.additionalSerializationGetter;
		ModifierRegistry<T, S, D> modifierRegistry = this.modifierRegistry;
		var entries = this.entries;
		entries.clear();
		this.addEntries();
		entries.forEach(entry -> {
			if (!entryNames.add(entry.name)) {
				throw new IllegalStateException("Duplicate modifier: " + entry.name);
			} else {
				Path resolvedPath = pathResolver.apply(outputFolder, entry);
				try {
					TargetedModifier<T, S, D> targetedModifier = entry.targetedModifier;
					DataProvider.save(gson, directoryCache, targetedModifier.serialize(additionalSerializationGetter.apply(targetedModifier), this.targetKeyName, modifierRegistry, entry.conditions), resolvedPath);
				} catch (IOException e) {
					LOGGER.error("Couldn't save modifier {}", resolvedPath, e);
				}
			}
		});
	}

	/**
	 * Adds entries to be generated.
	 */
	protected abstract void addEntries();

	/**
	 * Adds a {@link ProviderEntry} instance to the {@link #entries} list.
	 *
	 * @param entry A {@link ProviderEntry} instance to generate.
	 */
	protected void addEntry(ProviderEntry<T, S, D> entry) {
		this.entries.add(entry);
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * A class representing a modifier to be generated.
	 *
	 * @param <T> The type of object that the {@link TargetedModifier} modifies.
	 * @param <S> The type of additional serialization object that the {@link TargetedModifier} uses.
	 * @param <D> The type of additional deserialization object that the {@link TargetedModifier} uses.
	 * @author SmellyModder (Luke Tonon)
	 */
	public static class ProviderEntry<T, S, D> {
		private static final ICondition[][] NO_CONDITIONS = new ICondition[][]{};
		public final TargetedModifier<T, S, D> targetedModifier;
		public final ICondition[][] conditions;
		public final ResourceLocation name;

		public ProviderEntry(TargetedModifier<T, S, D> targetedModifier, ICondition[][] conditions, ResourceLocation name) {
			this.targetedModifier = targetedModifier;
			this.conditions = conditions;
			this.name = name;
		}

		public ProviderEntry(TargetedModifier<T, S, D> targetedModifier, ResourceLocation name) {
			this(targetedModifier, NO_CONDITIONS, name);
		}
	}
}
