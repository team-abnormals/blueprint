package com.teamabnormals.blueprint.core.util.modification;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * A generic {@link DataProvider} implementation for {@link ObjectModifierGroup} instances.
 *
 * @param <T> The type of object that the {@link ObjectModifierGroup} instances modify.
 * @param <S> The type of additional serialization object that the {@link ObjectModifierGroup} instances use.
 * @param <D> The type of additional deserialization object that the {@link ObjectModifierGroup} instances use.
 * @author SmellyModder (Luke Tonon)
 * @see ObjectModifierGroup
 * @see EntryBuilder
 */
public abstract class ObjectModifierProvider<T, S, D> implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final String modId;
	private final String name;
	private final PackOutput.PathProvider pathProvider;
	private final CompletableFuture<HolderLookup.Provider> lookupProvider;
	private final ObjectModifierSerializerRegistry<T, S, D> serializerRegistry;
	private final BiFunction<RegistryOps<JsonElement>, ObjectModifierGroup<T, S, D>, S> additionalSerializationGetter;
	private final List<EntryBuilder<T, S, D>> entries = new ArrayList<>();

	public ObjectModifierProvider(String modId, boolean data, String subDirectory, ObjectModifierSerializerRegistry<T, S, D> serializerRegistry, BiFunction<RegistryOps<JsonElement>, ObjectModifierGroup<T, S, D>, S> additionalSerializationGetter, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		this.modId = modId;
		this.name = "Object Modifiers (" + subDirectory + "): " + modId;
		this.pathProvider = output.createPathProvider(data ? PackOutput.Target.DATA_PACK : PackOutput.Target.RESOURCE_PACK, ObjectModificationManager.MAIN_PATH + "/" + subDirectory);
		this.lookupProvider = lookupProvider;
		this.serializerRegistry = serializerRegistry;
		this.additionalSerializationGetter = additionalSerializationGetter;
	}

	public ObjectModifierProvider(String modId, boolean data, String subDirectory, ObjectModifierSerializerRegistry<T, S, D> serializerRegistry, S additionalSerializationObject, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		this(modId, data, subDirectory, serializerRegistry, (ops, group) -> additionalSerializationObject, output, lookupProvider);
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cachedOutput) {
		return this.lookupProvider.thenCompose(provider -> {
			var entries = this.entries;
			entries.clear();
			this.registerEntries(provider);
			PackOutput.PathProvider pathProvider = this.pathProvider;
			var additionalSerializationGetter = this.additionalSerializationGetter;
			RegistryOps<JsonElement> registryOps = RegistryOps.create(JsonOps.INSTANCE, provider);
			ObjectModifierSerializerRegistry<T, S, D> serializerRegistry = this.serializerRegistry;
			return CompletableFuture.allOf(entries.stream().map(entry -> {
				Path resolvedPath = pathProvider.json(new ResourceLocation(this.modId, entry.name));
				ObjectModifierGroup<T, S, D> group = new ObjectModifierGroup<>(entry.selector, entry.modifiers, entry.priority);
				try {
					return DataProvider.saveStable(cachedOutput, group.serialize(additionalSerializationGetter.apply(registryOps, group), serializerRegistry, entry.conditions.toArray(new ICondition[0][])), resolvedPath);
				} catch (JsonParseException exception) {
					LOGGER.error("Couldn't save modifier group {}", resolvedPath, exception);
					return CompletableFuture.completedFuture(null);
				}
			}).toArray(CompletableFuture[]::new));
		});
	}

	/**
	 * Override this to register your entries.
	 */
	protected abstract void registerEntries(HolderLookup.Provider provider);

	/**
	 * Creates and registers an {@link EntryBuilder} instance.
	 *
	 * @param name A name for the entry.
	 */
	protected EntryBuilder<T, S, D> entry(String name) {
		EntryBuilder<T, S, D> entryBuilder = new EntryBuilder<>(name);
		this.entries.add(entryBuilder);
		return entryBuilder;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * This builder class eases data setup for serializing {@link ObjectModifierGroup} instances.
	 *
	 * @param <T> The type of object to modify.
	 * @param <S> The type of additional serialization object that the {@link #modifiers} use.
	 * @param <D> The type of additional deserialization object that the {@link #modifiers} uses.
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class EntryBuilder<T, S, D> {
		private final String name;
		private final LinkedList<ObjectModifier<T, S, D, ?>> modifiers = new LinkedList<>();
		private final ArrayList<ICondition[]> conditions = new ArrayList<>();
		private ConditionedResourceSelector selector = ConditionedResourceSelector.EMPTY;
		private EventPriority priority = EventPriority.NORMAL;

		/**
		 * The main constructor.
		 *
		 * @param name The name for the entry.
		 */
		public EntryBuilder(String name) {
			this.name = name;
		}

		/**
		 * Sets the {@link #selector}.
		 *
		 * @param selector A {@link ConditionedResourceSelector} instance.
		 * @return This builder.
		 */
		public EntryBuilder<T, S, D> selector(ConditionedResourceSelector selector) {
			this.selector = selector;
			return this;
		}

		/**
		 * Sets the {@link #selector} to a {@link ConditionedResourceSelector} instance with no conditions
		 *
		 * @param selector A {@link ResourceSelector} instance.
		 * @return This builder.
		 */
		public EntryBuilder<T, S, D> selector(ResourceSelector<?> selector) {
			return this.selector(new ConditionedResourceSelector(selector));
		}

		/**
		 * Sets the {@link #selector} to a {@link NamesResourceSelector} instance.
		 *
		 * @param names An array of {@link ResourceLocation} names.
		 * @return This builder.
		 */
		public EntryBuilder<T, S, D> selects(ResourceLocation... names) {
			return this.selector(new NamesResourceSelector(names));
		}

		/**
		 * Sets the {@link #selector} to a {@link NamesResourceSelector} instance.
		 *
		 * @param names An array of names.
		 * @return This builder.
		 */
		public EntryBuilder<T, S, D> selects(String... names) {
			return this.selector(new NamesResourceSelector(names));
		}

		/**
		 * Adds an {@link ObjectModifier} instance to the entry.
		 *
		 * @param modifier   A {@link ObjectModifier} instance.
		 * @param conditions An array of {@link ICondition} instances to pair with the modifier.
		 * @return This builder.
		 */
		public EntryBuilder<T, S, D> addModifier(ObjectModifier<T, S, D, ?> modifier, ICondition... conditions) {
			this.modifiers.add(modifier);
			this.conditions.add(conditions);
			return this;
		}

		/**
		 * Sets the {@link #priority}.
		 *
		 * @param priority The {@link EventPriority} to use.
		 * @return This builder.
		 */
		public EntryBuilder<T, S, D> priority(EventPriority priority) {
			this.priority = priority;
			return this;
		}
	}
}
