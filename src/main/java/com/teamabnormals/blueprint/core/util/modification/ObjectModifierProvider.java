package com.teamabnormals.blueprint.core.util.modification;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
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
import java.util.function.Function;

/**
 * A generic {@link DataProvider} implementation for {@link ObjectModifierGroup} instances.
 *
 * @param <T> The type of object that the {@link ObjectModifierGroup} instances modify.
 * @param <S> The type of additional serialization object that the {@link ObjectModifierGroup} instances use.
 * @param <D> The type of additional deserialization object that the {@link ObjectModifierGroup} instances use.
 * @author SmellyModder (Luke Tonon)
 * @see ObjectModifierGroup
 * @see ProviderEntry
 */
public abstract class ObjectModifierProvider<T, S, D> implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private final DataGenerator dataGenerator;
	private final String modId;
	private final String name;
	private final String directory;
	private final Gson gson;
	private final ObjectModifierSerializerRegistry<T, S, D> serializerRegistry;
	private final Function<ObjectModifierGroup<T, S, D>, S> additionalSerializationGetter;
	private final List<ProviderEntry<T, S, D>> entries = new ArrayList<>();

	public ObjectModifierProvider(DataGenerator dataGenerator, String modId, boolean data, String subDirectory, Gson gson, ObjectModifierSerializerRegistry<T, S, D> serializerRegistry, Function<ObjectModifierGroup<T, S, D>, S> additionalSerializationGetter) {
		this.dataGenerator = dataGenerator;
		this.modId = modId;
		this.name = "Object Modifier Groups (" + subDirectory + "): " + modId;
		this.directory = (data ? "data/" : "assets/") + modId + "/" + ObjectModificationManager.MAIN_PATH + "/" + subDirectory + "/";
		this.gson = gson;
		this.serializerRegistry = serializerRegistry;
		this.additionalSerializationGetter = additionalSerializationGetter;
	}

	@Override
	public void run(HashCache directoryCache) {
		Set<String> entryNames = Sets.newHashSet();
		Path outputFolder = this.dataGenerator.getOutputFolder();
		Gson gson = this.gson;
		Function<ObjectModifierGroup<T, S, D>, S> additionalSerializationGetter = this.additionalSerializationGetter;
		ObjectModifierSerializerRegistry<T, S, D> serializerRegistry = this.serializerRegistry;
		var entries = this.entries;
		entries.clear();
		this.registerEntries();
		entries.forEach(entry -> {
			if (!entryNames.add(entry.name)) {
				throw new IllegalStateException("Duplicate modifier group: " + entry.name);
			} else {
				Path resolvedPath = outputFolder.resolve(this.directory + entry.name + ".json");
				try {
					ObjectModifierGroup<T, S, D> group = entry.group;
					DataProvider.save(gson, directoryCache, group.serialize(additionalSerializationGetter.apply(group), serializerRegistry, entry.conditions), resolvedPath);
				} catch (IOException e) {
					LOGGER.error("Couldn't save modifier group {}", resolvedPath, e);
				}
			}
		});
	}

	/**
	 * Override this to register your entries.
	 */
	protected abstract void registerEntries();

	/**
	 * Registers a {@link ProviderEntry} instance.
	 *
	 * @param entry A {@link ProviderEntry} instance to generate.
	 */
	protected void registerEntry(ProviderEntry<T, S, D> entry) {
		this.entries.add(entry);
	}

	/**
	 * Creates and registers a {@link ProviderEntry} instance.
	 *
	 * @param name       The name of the group.
	 * @param group      A {@link ObjectModifierGroup} instance to generate.
	 * @param conditions A two-dimension array of conditions for the {@link ObjectModifierGroup} instance.
	 */
	protected void registerEntry(String name, ObjectModifierGroup<T, S, D> group, ICondition[][] conditions) {
		this.registerEntry(new ProviderEntry<>(name, group, conditions));
	}

	/**
	 * Creates and registers a {@link ProviderEntry} instance with no conditions.
	 *
	 * @param name  The name of the group.
	 * @param group A {@link ObjectModifierGroup} instance to generate.
	 */
	protected void registerEntry(String name, ObjectModifierGroup<T, S, D> group) {
		this.registerEntry(name, group, ProviderEntry.NO_CONDITIONS);
	}

	/**
	 * Creates and registers a {@link ProviderEntry} instance that only has one {@link ObjectModifier} instance that selects an array of resources.
	 *
	 * @param name     The name of the group.
	 * @param modifier A {@link ObjectModifier} instance to generate.
	 * @param names    An array of {@link ResourceLocation} names to use for a {@link NamesResourceSelector} instance.
	 */
	protected void registerEntry(String name, ObjectModifier<T, S, D, ?> modifier, ResourceLocation... names) {
		this.registerEntry(name, new ObjectModifierGroup<>(new NamesResourceSelector(names), List.of(modifier)), ProviderEntry.NO_CONDITIONS);
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getModId() {
		return this.modId;
	}

	/**
	 * Stores the necessary data for serializing a {@link ObjectModifierGroup} instance with conditions.
	 *
	 * @param <T> The type of object that the {@link #group} modifies.
	 * @param <S> The type of additional serialization object that the {@link #group} uses.
	 * @param <D> The type of additional deserialization object that the {@link #group} uses.
	 * @author SmellyModder (Luke Tonon)
	 */
	public static record ProviderEntry<T, S, D>(String name, ObjectModifierGroup<T, S, D> group, ICondition[][] conditions) {
		private static final ICondition[][] NO_CONDITIONS = new ICondition[][]{};
	}
}
