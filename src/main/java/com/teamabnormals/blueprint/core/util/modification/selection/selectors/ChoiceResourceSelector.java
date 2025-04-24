package com.teamabnormals.blueprint.core.util.modification.selection.selectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelectorSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.common.conditions.FalseCondition;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.Set;
import java.util.function.Predicate;

/**
 * A {@link ResourceSelector} implementation that picks a {@link ConditionedResourceSelector} if a condition is met or picks another {@link ConditionedResourceSelector} if the condition is not met.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record ChoiceResourceSelector(ConditionedResourceSelector first, ConditionedResourceSelector second, ICondition condition) implements ResourceSelector<ChoiceResourceSelector> {

	public ChoiceResourceSelector(ResourceSelector<?> first, ResourceSelector<?> second, ICondition condition) {
		this(new ConditionedResourceSelector(first), new ConditionedResourceSelector(second), condition);
	}

	@Override
	public Either<Set<ResourceLocation>, Predicate<ResourceLocation>> select() {
		return this.condition.test(ICondition.IContext.EMPTY) ? this.first.select() : this.second.select();
	}

	@Override
	public Serializer getSerializer() {
		return ResourceSelectorSerializers.CHOICE;
	}

	/**
	 * The serializer class for the {@link ChoiceResourceSelector}.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class Serializer implements ResourceSelector.Serializer<ChoiceResourceSelector> {
		@Override
		public JsonElement serialize(ChoiceResourceSelector selector) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("first", selector.first.serialize());
			jsonObject.add("second", selector.second.serialize());
			jsonObject.add("condition", ICondition.CODEC.encodeStart(JsonOps.INSTANCE, selector.condition).mapOrElse(element -> element, error -> JsonNull.INSTANCE));
			return jsonObject;
		}

		@Override
		public ChoiceResourceSelector deserialize(JsonElement element) {
			JsonObject jsonObject = element.getAsJsonObject();
			JsonObject conditionObject = GsonHelper.convertToJsonObject(jsonObject.get("condition"), "condition");
			var result = ICondition.CODEC.decode(JsonOps.INSTANCE, conditionObject);
			if (result.isError()) return new ChoiceResourceSelector(ConditionedResourceSelector.EMPTY, ConditionedResourceSelector.deserialize("second", GsonHelper.convertToJsonObject(jsonObject.get("second"), "second")), FalseCondition.INSTANCE);
			return new ChoiceResourceSelector(ConditionedResourceSelector.deserialize("first", jsonObject.get("first")), ConditionedResourceSelector.deserialize("second", jsonObject.get("second")), result.result().get().getFirst());
		}
	}
}
