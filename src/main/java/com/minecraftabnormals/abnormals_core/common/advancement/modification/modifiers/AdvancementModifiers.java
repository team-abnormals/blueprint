package com.minecraftabnormals.abnormals_core.common.advancement.modification.modifiers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.minecraftabnormals.abnormals_core.common.advancement.modification.AdvancementModifier;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;

/**
 * The registry class for {@link AdvancementModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class AdvancementModifiers {
	public static final Registry REGISTRY = new Registry();

	public static final ParentModifier PARENT_MODIFIER = register("parent", new ParentModifier());
	public static final RewardsModifier REWARDS_MODIFIER = register("rewards", new RewardsModifier());
	public static final DisplayInfoModifier DISPLAY_INFO_MODIFIER = register("display", new DisplayInfoModifier());

	/**
	 * Registers an {@link AdvancementModifier} for a given name. This method is safe to call during parallel mod-loading.
	 * <p>The name should be prefixed with your mod id.</p>
	 *
	 * @param name     The name to register the {@link AdvancementModifier} by.
	 * @param modifier A {@link AdvancementModifier} to register.
	 * @param <C>      The type of the config object for the {@link AdvancementModifier}.
	 * @param <M>      The type of the {@link AdvancementModifier} to register.
	 * @return The given {@link AdvancementModifier}.
	 * @throws IllegalArgumentException If a {@link AdvancementModifier} is already registered with the given name.
	 */
	public static synchronized <C, M extends AdvancementModifier<C>> M register(String name, M modifier) {
		REGISTRY.register(name, modifier);
		return modifier;
	}

	public static class Registry implements Codec<AdvancementModifier<?>> {
		private final Lifecycle lifecycle = Lifecycle.stable();
		private final BiMap<String, AdvancementModifier<?>> map = HashBiMap.create();

		private <M extends AdvancementModifier<?>> void register(String name, M modifier) {
			if (this.map.containsKey(name)) {
				throw new IllegalArgumentException("A modifier with name '" + name + "' is already registered!");
			}
			this.map.put(name, modifier);
		}

		@Override
		public <T> DataResult<Pair<AdvancementModifier<?>, T>> decode(DynamicOps<T> ops, T input) {
			return Codec.STRING.decode(ops, input).addLifecycle(this.lifecycle).flatMap(pair -> {
				String name = pair.getFirst();
				if (!this.map.containsKey(name)) {
					return DataResult.error("Unknown Advancement Modifier: " + name);
				}
				return DataResult.success(pair.mapFirst(this.map::get), this.lifecycle);
			});
		}

		@Override
		public <T> DataResult<T> encode(AdvancementModifier<?> input, DynamicOps<T> ops, T prefix) {
			return ops.mergeToPrimitive(prefix, ops.createString(this.map.inverse().get(input))).setLifecycle(this.lifecycle);
		}
	}
}
