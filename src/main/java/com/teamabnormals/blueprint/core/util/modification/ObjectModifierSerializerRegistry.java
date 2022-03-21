package com.teamabnormals.blueprint.core.util.modification;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.annotation.Nullable;

/**
 * The class used for {@link ObjectModifier.Serializer} registries.
 * <p>Create a new instance to make a registry for {@link ObjectModifier.Serializer} instances that serialize and deserialize modifiers of specific type parameters.</p>
 *
 * @param <T> The type of object the {@link ObjectModifier.Serializer} instances in this registry will modify.
 * @param <S> The type of the additional serialization object the {@link ObjectModifier.Serializer} instances in this registry will use.
 * @param <D> The type of the additional deserialization object the {@link ObjectModifier.Serializer} instances in this registry will use.
 * @author SmellyModder (Luke Tonon)
 * @see ObjectModifier.Serializer
 */
public class ObjectModifierSerializerRegistry<T, S, D> {
	private final BiMap<String, ObjectModifier.Serializer<? extends ObjectModifier<T, S, D, ?>, S, D>> serializers = HashBiMap.create();

	/**
	 * Registers an {@link ObjectModifier.Serializer} instance for a given name. This method is safe to call during parallel mod-loading.
	 * <p>The name should be prefixed with your mod's ID.</p>
	 *
	 * @param name       The name to register the {@link ObjectModifier.Serializer} by.
	 * @param serializer The {@link ObjectModifier.Serializer} instance to register.
	 * @param <M>        The type of the {@link ObjectModifier.Serializer}.
	 * @return The given {@link ObjectModifier.Serializer} instance.
	 * @throws IllegalArgumentException If an {@link ObjectModifier.Serializer} instance is already registered with the given name.
	 */
	public synchronized <M extends ObjectModifier<T, S, D, M>, MS extends ObjectModifier.Serializer<M, S, D>> MS register(String name, MS serializer) {
		if (this.serializers.containsKey(name)) {
			throw new IllegalArgumentException("A modifier serializer with name '" + name + "' is already registered!");
		}
		this.serializers.put(name, serializer);
		return serializer;
	}

	/**
	 * Gets a {@link ObjectModifier.Serializer} instance by its name in the registry.
	 *
	 * @param name The name of the {@link ObjectModifier.Serializer} to lookup.
	 * @return A {@link ObjectModifier.Serializer} instance looked up by its name, or null if no such {@link ObjectModifier.Serializer} with the specified name could be found.
	 */
	@Nullable
	public ObjectModifier.Serializer<? extends ObjectModifier<T, S, D, ?>, S, D> getSerializer(String name) {
		return this.serializers.get(name);
	}

	/**
	 * Gets a name for a given {@link ObjectModifier.Serializer} instance.
	 *
	 * @param serializer A {@link ObjectModifier.Serializer} instance to lookup.
	 * @return The name for the given {@link ObjectModifier.Serializer} instance, or null if the serializer isn't in the registry.
	 */
	@Nullable
	public String getName(ObjectModifier.Serializer<? extends ObjectModifier<T, S, D, ?>, S, D> serializer) {
		return this.serializers.inverse().get(serializer);
	}
}
