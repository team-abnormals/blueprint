package com.teamabnormals.blueprint.core.util.modification.selection.selectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelectorSerializers;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@link ResourceSelector} implementation that returns a configurable list of target names.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record NamesResourceSelector(Set<ResourceLocation> names) implements ResourceSelector<NamesResourceSelector> {

	public NamesResourceSelector(ResourceLocation... names) {
		this(Set.of(names));
	}

	public NamesResourceSelector(String... names) {
		this(Stream.of(names).map(ResourceLocation::new).collect(Collectors.toSet()));
	}

	@Override
	public Either<Set<ResourceLocation>, Predicate<ResourceLocation>> select() {
		return Either.left(this.names);
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
			HashSet<ResourceLocation> names = new HashSet<>();
			jsonArray.forEach(nameElement -> names.add(new ResourceLocation(nameElement.getAsString())));
			return new NamesResourceSelector(names);
		}
	}

}
