package com.teamabnormals.blueprint.core.util.modification.selection.selectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelectorSerializers;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link ResourceSelector} implementation that returns a list of target names picked using a configurable regular expression.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record RegexResourceSelector(Pattern pattern) implements ResourceSelector<RegexResourceSelector> {
	@Override
	public Either<Set<ResourceLocation>, Predicate<ResourceLocation>> select() {
		Matcher matcher = this.pattern.matcher("");
		return Either.right(location -> matcher.reset(location.toString()).matches());
	}

	@Override
	public Serializer getSerializer() {
		return ResourceSelectorSerializers.REGEX;
	}

	/**
	 * The serializer class for the {@link RegexResourceSelector}.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class Serializer implements ResourceSelector.Serializer<RegexResourceSelector> {
		@Override
		public JsonElement serialize(RegexResourceSelector selector) {
			return new JsonPrimitive(selector.pattern().pattern());
		}

		@Override
		public RegexResourceSelector deserialize(JsonElement element) {
			return new RegexResourceSelector(Pattern.compile(element.getAsString()));
		}
	}
}
