package com.teamabnormals.blueprint.common.advancement.modification.modifiers;

import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.core.util.modification.ObjectModifier;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.RegistryOps;

/**
 * An interface extending the {@link ObjectModifier} interface, typed to be used on {@link Advancement.Builder} instances.
 *
 * @author SmellyModder (Luke Tonon)
 * @see ObjectModifier
 */
// TODO: Migrate to Remolder
public interface AdvancementModifier<M extends AdvancementModifier<M>> extends ObjectModifier<Advancement.Builder, RegistryOps<JsonElement>, RegistryOps<JsonElement>, M> {
	/**
	 * A {@link ObjectModifier.Serializer} extension, typed to be used for {@link AdvancementModifier} types.
	 *
	 * @param <M> The type of {@link AdvancementModifier} instances to serialize and deserialize.
	 * @author SmellyModder (Luke Tonon)
	 */
	interface Serializer<M extends AdvancementModifier<M>> extends ObjectModifier.Serializer<M, RegistryOps<JsonElement>, RegistryOps<JsonElement>> {}
}
