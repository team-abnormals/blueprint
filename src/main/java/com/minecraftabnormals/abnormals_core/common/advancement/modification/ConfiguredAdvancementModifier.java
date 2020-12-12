package com.minecraftabnormals.abnormals_core.common.advancement.modification;

import com.minecraftabnormals.abnormals_core.common.advancement.modification.modifiers.AdvancementModifiers;
import com.mojang.serialization.Codec;
import net.minecraft.advancements.Advancement;

/**
 * A wrapper around an {@link AdvancementModifier} that uses its {@link AdvancementModifier#modify(Advancement.Builder, Object)} method with a constant config.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class ConfiguredAdvancementModifier<C, M extends AdvancementModifier<C>> {
	public static final Codec<ConfiguredAdvancementModifier<?, ?>> CODEC = AdvancementModifiers.REGISTRY.dispatch(modifier -> {
		return modifier.modifier;
	}, AdvancementModifier::getCodec);
	private final M modifier;
	private final C config;

	public ConfiguredAdvancementModifier(M modifier, C config) {
		this.modifier = modifier;
		this.config = config;
	}

	/**
	 * Modifies an {@link Advancement.Builder}.
	 *
	 * @param builder A {@link Advancement.Builder} to modify.
	 */
	public void modify(Advancement.Builder builder) {
		this.modifier.modify(builder, this.config);
	}

	/**
	 * Gets the {@link AdvancementModifier} this configured wrapper contains.
	 *
	 * @return The {@link AdvancementModifier} this configured wrapper contains.
	 */
	public M getModifier() {
		return this.modifier;
	}

	/**
	 * Gets the config object used for modifying an {@link Advancement.Builder} with this wrapper's {@link #modifier}.
	 *
	 * @return The config object used for modifying an {@link Advancement.Builder} with this wrapper's {@link #modifier}.
	 */
	public C getConfig() {
		return this.config;
	}
}
