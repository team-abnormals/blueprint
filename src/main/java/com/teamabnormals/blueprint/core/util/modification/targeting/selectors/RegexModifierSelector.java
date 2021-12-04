package com.teamabnormals.blueprint.core.util.modification.targeting.selectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.teamabnormals.blueprint.core.util.modification.targeting.ModifierTargetSelector;
import com.teamabnormals.blueprint.core.util.modification.targeting.SelectionSpace;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link ModifierTargetSelector} implementation that returns a list of target names picked using a configurable regular expression.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class RegexModifierSelector implements ModifierTargetSelector<Pattern> {

	@Override
	public List<ResourceLocation> getTargetNames(SelectionSpace space, Pattern config) {
		List<ResourceLocation> targetNames = new ArrayList<>();
		Matcher matcher = config.matcher("");
		space.forEach((key, element) -> {
			if (matcher.reset(key.toString()).matches()) {
				targetNames.add(key);
			}
		});
		return targetNames;
	}

	@Override
	public JsonElement serialize(Pattern config) {
		return new JsonPrimitive(config.pattern());
	}

	@Override
	public Pattern deserialize(JsonElement element) {
		return Pattern.compile(element.getAsString());
	}

}
