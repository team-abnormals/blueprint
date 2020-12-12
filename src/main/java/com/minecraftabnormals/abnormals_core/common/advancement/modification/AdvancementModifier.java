package com.minecraftabnormals.abnormals_core.common.advancement.modification;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.Advancement;

/**
 * An abstract class that can be used to modify an {@link Advancement} with a config.
 *
 * @param <C> The type of config object for this modifier.
 * @author SmellyModder (Luke Tonon)
 */
public abstract class AdvancementModifier<C> {
	private final Codec<ConfiguredAdvancementModifier<C, AdvancementModifier<C>>> codec;

	protected AdvancementModifier(Codec<C> codec) {
		this.codec = codec.fieldOf("config").xmap((config) -> {
			return new ConfiguredAdvancementModifier<>(this, config);
		}, ConfiguredAdvancementModifier::getConfig).codec();
	}

	/**
	 * Gets this modifier's {@link Codec} used for deserializing the config for this modifier.
	 *
	 * @return This modifier's {@link Codec} used for deserializing the config for this modifier.
	 */
	public Codec<ConfiguredAdvancementModifier<C, AdvancementModifier<C>>> getCodec() {
		return this.codec;
	}

	/**
	 * Modifies an {@link Advancement.Builder} with a given config.
	 *
	 * @param builder An {@link Advancement.Builder} to modify.
	 * @param config  A config object to be used in modifying the {@link Advancement.Builder}.
	 */
	public abstract void modify(Advancement.Builder builder, C config);
}
