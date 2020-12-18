package com.minecraftabnormals.abnormals_core.common.advancement.modification.modifiers;

import com.google.common.collect.Maps;
import com.minecraftabnormals.abnormals_core.common.advancement.modification.AdvancementModifier;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * The registry class for {@link AdvancementModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class AdvancementModifiers {
	private static final Map<String, AdvancementModifier<?>> REGISTRY = Maps.newHashMap();

	public static final ParentModifier PARENT_MODIFIER = register("parent", new ParentModifier());
	public static final RewardsModifier REWARDS_MODIFIER = register("rewards", new RewardsModifier());
	public static final DisplayInfoModifier DISPLAY_INFO_MODIFIER = register("display", new DisplayInfoModifier());
	public static final CriteriaModifier CRITERIA_MODIFIER = register("criteria", new CriteriaModifier());

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
		if (REGISTRY.containsKey(name)) {
			throw new IllegalArgumentException("A modifier with name '" + name + "' is already registered!");
		}
		REGISTRY.put(name, modifier);
		return modifier;
	}

	/**
	 * Gets an {@link AdvancementModifier} by its name.
	 *
	 * @param name The name of the {@link AdvancementModifier} to lookup.
	 * @return An {@link AdvancementModifier} by its name.
	 */
	@Nullable
	public static AdvancementModifier<?> getModifier(String name) {
		return REGISTRY.get(name);
	}
}
