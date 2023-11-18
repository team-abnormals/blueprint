package com.teamabnormals.blueprint.core.util.modification;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.eventbus.api.EventPriority;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A {@link SimpleJsonResourceReloadListener} subclass that handles the loading of {@link ObjectModifierGroup} instances.
 * <p>Use this for simple prioritized management of {@link ObjectModifierGroup} instances.</p>
 *
 * @param <T> The type of object to modify.
 * @param <S> The type of additional serialization object.
 * @param <D> The type of additional deserialization object.
 * @author SmellyModder (Luke Tonon)
 * @see ObjectModifierGroup
 * @see ObjectModifier
 */
public class ObjectModificationManager<T, S, D> extends SimpleJsonResourceReloadListener {
	public static final String MAIN_PATH = "modifiers";
	private static final HashMultimap<String, Initializer<?>> INITIALIZER_MAP = HashMultimap.create();
	protected final EnumMap<EventPriority, Pair<Map<ResourceLocation, List<ObjectModifier<T, S, D, ?>>>, List<Pair<Predicate<ResourceLocation>, List<ObjectModifier<T, S, D, ?>>>>>> prioritizedAssignedModifiers = new EnumMap<>(EventPriority.class);
	private final String type;
	private final ObjectModifierSerializerRegistry<T, S, D> serializerRegistry;
	private final Function<ResourceLocation, D> additionalDeserializationGetter;
	private final boolean logSkipping;
	private final boolean allowPriority;

	public ObjectModificationManager(Gson gson, String directory, String type, ObjectModifierSerializerRegistry<T, S, D> serializerRegistry, Function<ResourceLocation, D> additionalDeserializationGetter, boolean logSkipping, boolean allowPriority) {
		super(gson, MAIN_PATH + "/" + directory);
		this.type = type;
		this.serializerRegistry = serializerRegistry;
		this.additionalDeserializationGetter = additionalDeserializationGetter;
		this.logSkipping = logSkipping;
		this.allowPriority = allowPriority;
	}

	public ObjectModificationManager(Gson gson, String directory, String type, ObjectModifierSerializerRegistry<T, S, D> serializerRegistry, D additionalDeserializationObject, boolean logSkipping, boolean allowPriority) {
		this(gson, directory, type, serializerRegistry, (location) -> additionalDeserializationObject, logSkipping, allowPriority);
	}

	/**
	 * Registers a {@link Initializer} instance to handle the initialization of a {@link ObjectModificationManager} instance.
	 *
	 * @param targetListener The name of the {@link PreparableReloadListener} to inject before. Leave null to inject at the end.
	 * @param initializer    A {@link Initializer} instance for handling the initialization of a {@link ObjectModificationManager} instance.
	 * @param <OM>           The type of {@link ObjectModificationManager} to initialize.
	 */
	public static synchronized <OM extends ObjectModificationManager<?, ?, ?>> void registerInitializer(@Nullable String targetListener, Initializer<OM> initializer) {
		INITIALIZER_MAP.put(targetListener, initializer);
	}

	/**
	 * Processes the {@link #INITIALIZER_MAP}.
	 *
	 * <p><b>For internal use only!</b></p>
	 */
	public static void processInitializers(RegistryAccess.Frozen registryAccess, Commands.CommandSelection commandSelection, ReloadableServerResources reloadableServerResources, List<PreparableReloadListener> listeners) {
		List<Pair<Integer, ObjectModificationManager<?, ?, ?>>> initialized = new ArrayList<>();
		for (int i = 0; i < listeners.size(); i++) {
			Set<Initializer<?>> initializers = INITIALIZER_MAP.get(listeners.get(i).getName());
			for (Initializer<?> initializer : initializers) {
				initialized.add(Pair.of(i, initializer.init(registryAccess, commandSelection, reloadableServerResources)));
			}
		}
		initialized.forEach(pair -> listeners.add(pair.getFirst(), pair.getSecond()));
		INITIALIZER_MAP.get(null).forEach(initializer -> listeners.add(initializer.init(registryAccess, commandSelection, reloadableServerResources)));
	}

	/**
	 * Applies all the modifiers assigned to a {@link ResourceLocation} instance and an {@link EventPriority} instance.
	 *
	 * @param eventPriority An {@link EventPriority} instance to only apply modifiers of that priority.
	 * @param location      A {@link ResourceLocation} instance to get the modifiers assigned to it.
	 * @param value         An object of type {@code <T>} to modify.
	 */
	public void applyModifiers(EventPriority eventPriority, ResourceLocation location, T value) {
		var assignedModifiers = this.prioritizedAssignedModifiers.get(eventPriority);
		if (assignedModifiers != null) {
			var directModifiers = assignedModifiers.getFirst();
			var modifiers = directModifiers.get(location);
			if (modifiers != null) modifiers.forEach(modifier -> modifier.modify(value));
			var dynamicModifiers = assignedModifiers.getSecond();
			if (!dynamicModifiers.isEmpty()) {
				for (var dynamicModifier : dynamicModifiers) {
					if (dynamicModifier.getFirst().test(location)) dynamicModifier.getSecond().forEach(modifier -> modifier.modify(value));
				}
			}
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		String type = this.type;
		int groupsLoaded = 0;
		var serializerRegistry = this.serializerRegistry;
		var additionalDeserializationGetter = this.additionalDeserializationGetter;
		boolean logSkipping = this.logSkipping;
		boolean allowPriority = this.allowPriority;
		var prioritizedAssignedModifiers = this.prioritizedAssignedModifiers;
		prioritizedAssignedModifiers.clear();
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			ResourceLocation resourcelocation = entry.getKey();
			try {
				var group = ObjectModifierGroup.deserialize(resourcelocation.toString(), entry.getValue().getAsJsonObject(), additionalDeserializationGetter.apply(resourcelocation), serializerRegistry, logSkipping, allowPriority);
				var assignedModifiers = prioritizedAssignedModifiers.computeIfAbsent(group.priority(), __ -> Pair.of(new HashMap<>(), new ArrayList<>()));
				var groupModifiers = group.modifiers();
				var either = group.selector().select();
				var locations = either.left();
				if (locations.isPresent()) {
					var directModifiers = assignedModifiers.getFirst();
					for (ResourceLocation location : locations.get()) {
						directModifiers.computeIfAbsent(location, __ -> new ArrayList<>()).addAll(groupModifiers);
					}
				} else assignedModifiers.getSecond().add(Pair.of(either.right().get(), groupModifiers));
				groupsLoaded++;
			} catch (IllegalArgumentException | JsonParseException jsonparseexception) {
				Blueprint.LOGGER.error("Parsing error loading " + type + " Modifier Group: {}", resourcelocation, jsonparseexception);
			}
		}
		Blueprint.LOGGER.info(type + " Modification Manager has loaded {} modifier groups", groupsLoaded);
	}

	/**
	 * The functional interface used for handling the initialization of {@link ObjectModificationManager} instances.
	 *
	 * @param <OM> The type of {@link ObjectModificationManager} to initialize.
	 * @author SmellyModder (Luke Tonon)
	 */
	@FunctionalInterface
	public interface Initializer<OM extends ObjectModificationManager<?, ?, ?>> {
		/**
		 * Creates a new {@link ObjectModificationManager} instance of type {@code <OM>}.
		 *
		 * @param registryAccess            The {@link RegistryAccess.Frozen} instance.
		 * @param commandSelection          The type of {@link Commands.CommandSelection}.
		 * @param reloadableServerResources The {@link ReloadableServerResources} instance.
		 * @return A new {@link ObjectModificationManager} instance of type {@code <OM>}.
		 */
		OM init(RegistryAccess.Frozen registryAccess, Commands.CommandSelection commandSelection, ReloadableServerResources reloadableServerResources);
	}
}
