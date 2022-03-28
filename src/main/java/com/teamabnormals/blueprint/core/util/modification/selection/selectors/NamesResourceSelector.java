package com.teamabnormals.blueprint.core.util.modification.selection.selectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelectorSerializers;
import com.teamabnormals.blueprint.core.util.modification.selection.SelectionSpace;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A {@link ResourceSelector} implementation that returns a configurable list of target names.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record NamesResourceSelector(List<ResourceLocation> names) implements ResourceSelector<NamesResourceSelector> {

	public NamesResourceSelector(ResourceLocation... names) {
		this(List.of(names));
	}

	public NamesResourceSelector(String... names) {
		this(Stream.of(names).map(ResourceLocation::new).toList());
	}

	@Override
	public List<ResourceLocation> select(SelectionSpace space) {
		return this.names;
	}

	@Override
	public Serializer getSerializer() {
		return ResourceSelectorSerializers.NAMES;
	}

	public static final class Serializer implements ResourceSelector.Serializer<NamesResourceSelector> {
		@Override
		public JsonElement serialize(NamesResourceSelector selector) {
			JsonArray jsonArray = new JsonArray();
			selector.names.forEach(location -> jsonArray.add(location.toString()));
			return jsonArray;
		}

		@Override
		public NamesResourceSelector deserialize(JsonElement element) {
			JsonArray jsonArray = element.getAsJsonArray();
			List<ResourceLocation> names = new ArrayList<>();
			jsonArray.forEach(nameElement -> names.add(new ResourceLocation(nameElement.getAsString())));
			return new NamesResourceSelector(names);
		}
	}

}
