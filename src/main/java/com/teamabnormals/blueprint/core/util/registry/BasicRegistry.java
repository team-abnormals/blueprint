package com.teamabnormals.blueprint.core.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * A simplified version of the {@link net.minecraft.core.Registry} class.
 * <p>This class is not an instance of {@link net.minecraft.core.Registry}</p>
 *
 * @param <T> The type of object for the registry.
 * @author SmellyModder (Luke Tonon)
 */
public final class BasicRegistry<T> implements Codec<T> {
	private final Lifecycle lifecycle;
	private final BiMap<String, T> map = HashBiMap.create();

	public BasicRegistry(Lifecycle lifecycle) {
		this.lifecycle = lifecycle;
	}

	public BasicRegistry() {
		this.lifecycle = Lifecycle.stable();
	}

	/**
	 * Registers a value for a given {@link String}.
	 *
	 * @param name  A {@link String} to register the value with.
	 * @param value A value to register.
	 */
	public void register(String name, T value) {
		this.map.put(name, value);
	}

	/**
	 * Registers a value for a given {@link ResourceLocation}.
	 *
	 * @param name  A {@link ResourceLocation} to register the value with.
	 * @param value A value to register.
	 */
	public void register(ResourceLocation name, T value) {
		this.register(name.toString(), value);
	}

	/**
	 * Gets this registry's {@link #lifecycle}.
	 *
	 * @return This registry's {@link #lifecycle}.
	 */
	@Nonnull
	public Lifecycle getLifecycle() {
		return this.lifecycle;
	}

	/**
	 * Gets a value for a given {@link String} name.
	 *
	 * @param name A {@link String} name to lookup the value with.
	 * @return A value for a given {@link String} name, or null if there's no value for the given {@link String}.
	 */
	@Nullable
	public T getValue(String name) {
		return this.map.get(name);
	}

	/**
	 * Gets a {@link String} for a given value.
	 *
	 * @param value A value to get a {@link String} for.
	 * @return A {@link String} for a given value.
	 */
	@Nullable
	public String getKey(T value) {
		return this.map.inverse().get(value);
	}

	/**
	 * Gets all the {@link String} keys in this registry.
	 *
	 * @return A set of all the {@link String} keys in this registry.
	 */
	@Nonnull
	public Set<String> keySet() {
		return this.map.keySet();
	}

	/**
	 * Gets all the registered values in this registry.
	 *
	 * @return A set of all the registered values in this registry.
	 */
	@Nonnull
	public Set<T> getValues() {
		return this.map.values();
	}

	/**
	 * Gets all the entries in this registry.
	 *
	 * @return A set of all the entries in this registry.
	 */
	@Nonnull
	public Set<Map.Entry<String, T>> getEntries() {
		return this.map.entrySet();
	}

	/**
	 * Checks if this registry has an entry with a given {@link String} name.
	 *
	 * @param name A {@link String} name to check.
	 * @return If this registry has an entry with a given {@link String} name.
	 */
	public boolean containsKey(String name) {
		return this.map.containsKey(name);
	}

	@Override
	public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> ops, U input) {
		return Codec.STRING.decode(ops, input).flatMap((encodedRegistryPair) -> {
			String name = encodedRegistryPair.getFirst();
			T value = this.getValue(name);
			return value == null ? DataResult.error(() -> "Unknown registry key: " + name) : DataResult.success(Pair.of(value, encodedRegistryPair.getSecond()), this.lifecycle);
		});
	}

	@Override
	public <U> DataResult<U> encode(T input, DynamicOps<U> ops, U prefix) {
		String name = this.getKey(input);
		if (name == null) {
			return DataResult.error(() -> "Unknown registry element: " + prefix);
		}
		return ops.mergeToPrimitive(prefix, ops.createString(name)).setLifecycle(this.lifecycle);
	}
}
