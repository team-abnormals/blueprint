package com.teamabnormals.blueprint.core.util.modification.selection.selectors;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelectorSerializers;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.function.Predicate;

/**
 * A {@link ResourceSelector} implementation that always returns an empty list of target names.
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum EmptyResourceSelector implements ResourceSelector<EmptyResourceSelector> {
	INSTANCE;

	private static final Either<Set<ResourceLocation>, Predicate<ResourceLocation>> EMPTY = Either.left(ImmutableSet.of());

	@Override
	public Either<Set<ResourceLocation>, Predicate<ResourceLocation>> select() {
		return EMPTY;
	}

	@Override
	public Serializer getSerializer() {
		return ResourceSelectorSerializers.EMPTY;
	}

	/**
	 * The serializer class for the {@link EmptyResourceSelector}.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class Serializer implements ResourceSelector.Serializer<EmptyResourceSelector> {
		@Override
		public JsonElement serialize(EmptyResourceSelector selector) {
			return new JsonObject();
		}

		@Override
		public EmptyResourceSelector deserialize(JsonElement element) {
			return INSTANCE;
		}
	}
}
