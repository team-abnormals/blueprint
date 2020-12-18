package com.minecraftabnormals.abnormals_core.common.advancement.modification;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A class representing an {@link net.minecraft.advancements.Advancement} to be modified with a list of {@link ConfiguredAdvancementModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class TargetedAdvancementModifier {
	private final ResourceLocation target;
	private final List<ConfiguredAdvancementModifier<?, ?>> configuredModifiers;

	public TargetedAdvancementModifier(@Nonnull ResourceLocation target, @Nonnull List<ConfiguredAdvancementModifier<?, ?>> configuredModifiers) {
		this.configuredModifiers = configuredModifiers;
		this.target = target;
	}

	/**
	 * Gets the modifiers to be used on an {@link net.minecraft.advancements.Advancement} with the same resource location as {@link #target}.
	 *
	 * @return The modifiers to be used on an {@link net.minecraft.advancements.Advancement} with the same resource location as {@link #target}.
	 */
	@Nonnull
	public List<ConfiguredAdvancementModifier<?, ?>> getConfiguredModifiers() {
		return this.configuredModifiers;
	}

	/**
	 * Gets the {@link ResourceLocation} of the {@link net.minecraft.advancements.Advancement} this targeted modifier targets.
	 *
	 * @return The {@link ResourceLocation} of the {@link net.minecraft.advancements.Advancement} this targeted modifier targets.
	 */
	@Nonnull
	public ResourceLocation getTarget() {
		return this.target;
	}
}
