package com.teamabnormals.blueprint.core.util.modification.selection;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.*;

import javax.annotation.Nullable;

/**
 * The registry for all {@link ResourceSelector.Serializer} types.
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum ResourceSelectorSerializers {
	INSTANCE;

	public static final ChoiceResourceSelector.Serializer CHOICE = INSTANCE.register("choice", new ChoiceResourceSelector.Serializer());
	public static final EmptyResourceSelector.Serializer EMPTY = INSTANCE.register("empty", new EmptyResourceSelector.Serializer());
	public static final MultiResourceSelector.Serializer MULTI = INSTANCE.register("multi", new MultiResourceSelector.Serializer());
	public static final NamesResourceSelector.Serializer NAMES = INSTANCE.register("names", new NamesResourceSelector.Serializer());
	public static final RegexResourceSelector.Serializer REGEX = INSTANCE.register("regex", new RegexResourceSelector.Serializer());
	private final BiMap<String, ResourceSelector.Serializer<?>> serializers = HashBiMap.create();

	/**
	 * Registers a {@link ResourceSelector.Serializer} instance for a given ID.
	 *
	 * @param id   An ID to assign to the given {@link ResourceSelector.Serializer} instance.
	 * @param type A {@link ResourceSelector.Serializer} instance to register.
	 * @param <S>  The type of {@link ResourceSelector.Serializer} to register.
	 * @return The given {@link ResourceSelector.Serializer} instance.
	 */
	public synchronized <S extends ResourceSelector.Serializer<?>> S register(String id, S type) {
		if (this.serializers.containsKey(id)) {
			throw new IllegalArgumentException("A selector with the ID '" + id + "' is already registered!");
		}
		this.serializers.put(id, type);
		return type;
	}

	/**
	 * Gets the {@link ResourceSelector.Serializer} instance associated with a given ID.
	 *
	 * @param id An ID to look up.
	 * @return The {@link ResourceSelector.Serializer} instance associated with a given ID, or null if no {@link ResourceSelector.Serializer} instance exists for the given ID.
	 */
	@Nullable
	public ResourceSelector.Serializer<?> getSerializer(String id) {
		return this.serializers.get(id);
	}

	/**
	 * Gets the ID associated with a given {@link ResourceSelector.Serializer} instance.
	 *
	 * @param type A {@link ResourceSelector.Serializer} instance to look up.
	 * @return The ID associated with a given {@link ResourceSelector.Serializer} instance, or null if no ID exists for the given {@link ResourceSelector.Serializer} instance.
	 */
	@Nullable
	public String getSerializerID(ResourceSelector.Serializer<?> type) {
		return this.serializers.inverse().get(type);
	}
}
