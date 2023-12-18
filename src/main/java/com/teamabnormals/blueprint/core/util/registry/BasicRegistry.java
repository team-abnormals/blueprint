package com.teamabnormals.blueprint.core.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.Util;
import net.minecraft.core.IdMap;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A simplified version of the {@link net.minecraft.core.Registry} class.
 * <p>This class is not an instance of {@link net.minecraft.core.Registry}.</p>
 * <p>Values can be added anytime, independent of other registry systems.</p>
 *
 * @param <T> The type of object for the registry.
 * @author SmellyModder (Luke Tonon)
 * @author ebo2022
 */
public final class BasicRegistry<T> implements Codec<T>, IdMap<T> {
	private final Lifecycle lifecycle;
	private final BiMap<String, T> map = HashBiMap.create();
	private final ObjectList<T> byId = new ObjectArrayList<>();
	private final Object2IntMap<T> toId = new Object2IntOpenCustomHashMap<>(Util.identityStrategy());
	private int nextId;

	public BasicRegistry(Lifecycle lifecycle) {
		this.lifecycle = lifecycle;
		this.toId.defaultReturnValue(IdMap.DEFAULT);
	}

	public BasicRegistry() {
		this(Lifecycle.stable());
	}

	/**
	 * Registers a value for a given {@link String}.
	 *
	 * @param name  A {@link String} to register the value with.
	 * @param value A value to register.
	 */
	public void register(String name, T value) {
		int id = this.nextId++;
		this.map.put(name, value);
		this.byId.size(id + 1);
		this.byId.set(id, value);
		this.toId.put(value, id);
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
	public Lifecycle registryLifecycle() {
		return this.lifecycle;
	}

	/**
	 * Gets a value for a given {@link String} name.
	 *
	 * @param name A {@link String} name to lookup the value with.
	 * @return A value for a given {@link String} name, or null if there's no value for the given {@link String}.
	 */
	@Nullable
	public T get(String name) {
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
	public Set<T> values() {
		return this.map.values();
	}

	/**
	 * Gets all the entries in this registry.
	 *
	 * @return A set of all the entries in this registry.
	 */
	@Nonnull
	public Set<Map.Entry<String, T>> entrySet() {
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

	/**
	 * Gets the id for a given value.
	 *
	 * @return The id for a given value, or -1 if the value isn't registered.
	 */
	@Override
	public int getId(T value) {
		return this.toId.getInt(value);
	}

	/**
	 * Gets a registry value with the given id.
	 *
	 * @param id An id to check for a registry value with.
	 * @return A registry value with the given id, or null if the id is out of range or no value is registered under the id.
	 */
	@Override
	@Nullable
	public T byId(int id) {
		return id >= 0 && id < this.byId.size() ? this.byId.get(id) : null;
	}

	/**
	 * Gets the number of values added to this registry.
	 *
	 * @return The number of values added to this registry.
	 */
	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public Iterator<T> iterator() {
		return this.map.values().iterator();
	}

	@Override
	public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> ops, U input) {
		return Codec.STRING.decode(ops, input).flatMap((encodedRegistryPair) -> {
			String name = encodedRegistryPair.getFirst();
			T value = this.get(name);
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
