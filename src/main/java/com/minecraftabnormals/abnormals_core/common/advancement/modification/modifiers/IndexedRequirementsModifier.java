package com.minecraftabnormals.abnormals_core.common.advancement.modification.modifiers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.minecraftabnormals.abnormals_core.common.advancement.modification.AdvancementModifier;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.util.JSONUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;
import java.util.Optional;

/**
 * An {@link AdvancementModifier} extension that modifies a specific requirement array of an advancement's requirements.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class IndexedRequirementsModifier extends AdvancementModifier<IndexedRequirementsModifier.Config> {

	public IndexedRequirementsModifier() {
		super(((element, conditionArrayParser) -> {
			JsonObject object = element.getAsJsonObject();
			Mode mode = Mode.deserialize(object);
			int index = JSONUtils.getAsInt(object, "index");
			Optional<Map<String, Criterion>> criterionMap = JSONUtils.isValidNode(object, "criteria") ? Optional.of(Criterion.criteriaFromJson(object.getAsJsonObject("criteria"), conditionArrayParser)) : Optional.empty();
			Optional<String[]> requirements = Optional.empty();
			if (criterionMap.isPresent()) {
				Map<String, Criterion> map = criterionMap.get();
				if (map.isEmpty()) {
					throw new JsonParseException("Criteria cannot be empty! Don't include it instead");
				}
				if (JSONUtils.isValidNode(object, "requirements")) {
					JsonArray requirementsArray = JSONUtils.getAsJsonArray(object, "requirements");
					String[] strings = new String[requirementsArray.size()];
					if (strings.length == 0) {
						throw new JsonParseException("Requirements cannot be empty!");
					}
					for (int i = 0; i < strings.length; i++) {
						String string = requirementsArray.get(i).getAsString();
						if (!map.containsKey(string)) {
							throw new JsonParseException("Unknown required criterion '" + string + "'");
						}
						strings[i] = string;
					}
					for (String key : map.keySet()) {
						if (!ArrayUtils.contains(strings, key)) {
							throw new JsonParseException("Criterion '" + key + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
						}
					}
					requirements = Optional.of(strings);
				}
			}
			return new Config(mode, index, criterionMap, requirements);
		}));
	}

	@Override
	public void modify(Advancement.Builder builder, Config config) {
		Map<String, Criterion> criteria = builder.getCriteria();
		try {
			String[][] requirementsArray = (String[][]) CriteriaModifier.REQUIREMENTS_FIELD.get(builder);
			int index = config.index;
			if (config.mode == Mode.MODIFY) {
				config.requirements.ifPresent(strings -> requirementsArray[index] = ArrayUtils.addAll(requirementsArray[index], strings));
			} else {
				criteria.clear();
				requirementsArray[index] = config.requirements.orElse(new String[]{});
			}
			config.criterionMap.ifPresent(criteria::putAll);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	static class Config {
		private final Mode mode;
		private final int index;
		private final Optional<Map<String, Criterion>> criterionMap;
		private final Optional<String[]> requirements;

		Config(Mode mode, int index, Optional<Map<String, Criterion>> criterionMap, Optional<String[]> requirements) {
			this.index = index;
			this.mode = mode;
			this.criterionMap = criterionMap;
			this.requirements = requirements;
		}
	}

}
