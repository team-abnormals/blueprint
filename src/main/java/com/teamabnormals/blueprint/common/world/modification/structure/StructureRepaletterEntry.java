package com.teamabnormals.blueprint.common.world.modification.structure;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DynamicOps;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.eventbus.api.EventPriority;

import javax.annotation.Nullable;
import java.util.Locale;

import static com.teamabnormals.blueprint.core.util.modification.ObjectModifierGroup.deserializePriority;

/**
 * The record class for storing the data for an "unassigned" {@link StructureRepaletter} instance.
 * <p>A {@link ConditionedResourceSelector} instance is stored for selecting structures.</p>
 * <p>An {@link EventPriority} instance is stored for prioritizing the {@link #repaletter}.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepalleterManager
 * @see StructureRepaletterProvider
 */
public record StructureRepaletterEntry(ConditionedResourceSelector selector, EventPriority priority, StructureRepaletter repaletter) {
	/**
	 * Deserializes a new {@link StructureRepaletter} instance from a {@link JsonElement} instance.
	 *
	 * @param name    The name associated with the {@link JsonElement} instance getting deserialized.
	 * @param element A {@link JsonElement} instance to deserialize from.
	 * @param ops     A {@link DynamicOps} instance to use for deserializing the {@link StructureRepaletter} instance.
	 * @return A new {@link StructureRepaletter} instance from a {@link JsonElement} instance.
	 * @throws JsonParseException If a deserialization problem occurs.
	 */
	@Nullable
	public static StructureRepaletterEntry deserialize(ResourceLocation name, JsonElement element, DynamicOps<JsonElement> ops) throws JsonParseException {
		JsonObject object = GsonHelper.convertToJsonObject(element, element.toString());
		ConditionedResourceSelector selector = ConditionedResourceSelector.deserialize("selector", object.get("selector"));
		if (selector == ConditionedResourceSelector.EMPTY) {
			Blueprint.LOGGER.info("Skipped structure repaletter named '" + name + "' as its conditions were not met");
			return null;
		}
		EventPriority priority = deserializePriority(object);
		var result = StructureRepaletter.CODEC.decode(ops, object);
		var error = result.error();
		if (error.isPresent()) throw new JsonParseException(error.get().message());
		return new StructureRepaletterEntry(selector, priority, result.result().get().getFirst());
	}

	/**
	 * Serializes this instance into a new {@link JsonElement} instance.
	 *
	 * @param ops A {@link DynamicOps} instance to use for serializing the {@link #repaletter}.
	 * @return A new {@link JsonElement} instance representing this instance.
	 * @throws JsonParseException If a serialization problem occurs.
	 */
	public JsonElement serialize(DynamicOps<JsonElement> ops) throws JsonParseException {
		JsonObject object = new JsonObject();
		object.add("selector", this.selector.serialize());
		object.addProperty("priority", this.priority.toString().toLowerCase(Locale.ROOT));
		var result = StructureRepaletter.CODEC.encode(this.repaletter, ops, object);
		var error = result.error();
		if (error.isPresent()) throw new JsonParseException(error.get().message());
		return result.get().left().get();
	}
}
