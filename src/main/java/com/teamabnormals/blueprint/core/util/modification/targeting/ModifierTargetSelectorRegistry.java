package com.teamabnormals.blueprint.core.util.modification.targeting;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.teamabnormals.blueprint.core.util.modification.targeting.selectors.*;

import javax.annotation.Nullable;

/**
 * The registry for all {@link ModifierTargetSelector} types.
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum ModifierTargetSelectorRegistry {
	INSTANCE;

	public static final ChoiceModifierTargetSelector CHOICE = INSTANCE.register("choice", new ChoiceModifierTargetSelector());
	public static final EmptyModifierTargetSelector EMPTY = INSTANCE.register("empty", new EmptyModifierTargetSelector());
	public static final MultiModifierTargetSelector MULTI = INSTANCE.register("multi", new MultiModifierTargetSelector());
	public static final NamesModifierTargetSelector NAMES = INSTANCE.register("names", new NamesModifierTargetSelector());
	public static final RegexModifierSelector REGEX = INSTANCE.register("regex", new RegexModifierSelector());
	private final BiMap<String, ModifierTargetSelector<?>> targetSelectors = HashBiMap.create();

	/**
	 * Registers a {@link ModifierTargetSelector} instance for a given ID.
	 *
	 * @param id             An ID to assign to the given {@link ModifierTargetSelector}.
	 * @param targetSelector A {@link ModifierTargetSelector} instance to register.
	 * @param <C>            The type of config for the given {@link ModifierTargetSelector} instance.
	 * @param <M>            The type of {@link ModifierTargetSelector} for the given {@link ModifierTargetSelector} instance.
	 * @return The given {@link ModifierTargetSelector} instance.
	 */
	public synchronized <C, M extends ModifierTargetSelector<C>> M register(String id, M targetSelector) {
		if (this.targetSelectors.containsKey(id)) {
			throw new IllegalArgumentException("A selector with the ID '" + id + "' is already registered!");
		}
		this.targetSelectors.put(id, targetSelector);
		return targetSelector;
	}

	/**
	 * Gets the {@link ModifierTargetSelector} instance associated a given ID.
	 *
	 * @param id An ID to look up.
	 * @return The {@link ModifierTargetSelector} instance associated with a given ID, or null if no {@link ModifierTargetSelector} instance exists for the given ID.
	 */
	@Nullable
	public ModifierTargetSelector<?> getTargetSelector(String id) {
		return this.targetSelectors.get(id);
	}

	/**
	 * Gets the ID associated with a given {@link ModifierTargetSelector} instance.
	 *
	 * @param selector A {@link ModifierTargetSelector} instance to look up.
	 * @return The ID associated with a given {@link ModifierTargetSelector} instance, or null if no ID exists for the given {@link ModifierTargetSelector} instance.
	 */
	@Nullable
	public String getSelectorID(ModifierTargetSelector<?> selector) {
		return this.targetSelectors.inverse().get(selector);
	}
}
