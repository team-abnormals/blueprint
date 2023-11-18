package com.teamabnormals.blueprint.core.util.modification.selection.selectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.teamabnormals.blueprint.core.util.modification.selection.*;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A {@link ResourceSelector} implementation that acts as multiple {@link ConditionedResourceSelector} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record MultiResourceSelector(List<ConditionedResourceSelector> selectors) implements ResourceSelector<MultiResourceSelector> {

	public MultiResourceSelector(ConditionedResourceSelector... selectors) {
		this(List.of(selectors));
	}

	public MultiResourceSelector(ResourceSelector<?>... selectors) {
		this(Stream.of(selectors).map(ConditionedResourceSelector::new).toList());
	}

	@Override
	public Either<Set<ResourceLocation>, Predicate<ResourceLocation>> select() {
		var selectors = this.selectors;
		var either = selectors.get(0).select();
		var locations = either.left();
		Predicate<ResourceLocation> combinedPredicate = locations.isPresent() ? locations.get()::contains : either.right().get();
		for (int i = 1; i < selectors.size(); i++) {
			either = selectors.get(i).select();
			locations = either.left();
			combinedPredicate = combinedPredicate.or(locations.isPresent() ? locations.get()::contains : either.right().get());
		}
		return Either.right(combinedPredicate);
	}

	@Override
	public Serializer getSerializer() {
		return ResourceSelectorSerializers.MULTI;
	}

	/**
	 * The serializer class for the {@link MultiResourceSelector}.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class Serializer implements ResourceSelector.Serializer<MultiResourceSelector> {
		@Override
		public JsonElement serialize(MultiResourceSelector selector) {
			JsonArray jsonArray = new JsonArray();
			selector.selectors.forEach(conditionedModifierTargetSelector -> jsonArray.add(conditionedModifierTargetSelector.serialize()));
			return jsonArray;
		}

		@Override
		public MultiResourceSelector deserialize(JsonElement element) {
			JsonArray jsonArray = element.getAsJsonArray();
			List<ConditionedResourceSelector> targetSelectors = new ArrayList<>(jsonArray.size());
			jsonArray.forEach(entry -> targetSelectors.add(ConditionedResourceSelector.deserialize(entry.toString(), entry)));
			return new MultiResourceSelector(targetSelectors);
		}
	}
}
