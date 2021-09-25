package com.minecraftabnormals.abnormals_core.common.advancement.modification;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.util.GsonHelper;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An abstract class that can be used to modify an {@link Advancement} with a config.
 *
 * @param <C> The type of config object for this modifier.
 * @author SmellyModder (Luke Tonon)
 */
//TODO: Switch to new modifier system in 1.17
public abstract class AdvancementModifier<C> {
	private final Deserializer<C> deserializer;

	protected AdvancementModifier(Deserializer<C> deserializer) {
		this.deserializer = deserializer;
	}

	/**
	 * Gets this modifier's {@link Deserializer} used for deserializing the config for this modifier.
	 *
	 * @return This modifier's {@link Deserializer} used for deserializing the config for this modifier.
	 */
	public Deserializer<C> getDeserializer() {
		return this.deserializer;
	}

	public final ConfiguredAdvancementModifier<C, AdvancementModifier<C>> deserialize(JsonElement config, DeserializationContext conditionArrayParser) throws JsonParseException {
		return new ConfiguredAdvancementModifier<>(this, this.deserializer.deserialize(config, conditionArrayParser));
	}

	/**
	 * Modifies an {@link Advancement.Builder} with a given config.
	 *
	 * @param builder An {@link Advancement.Builder} to modify.
	 * @param config  A config object to be used in modifying the {@link Advancement.Builder}.
	 */
	public abstract void modify(Advancement.Builder builder, C config);

	public enum Mode {
		MODIFY("modify"),
		REPLACE("replace");

		private static final Map<String, Mode> VALUES_MAP = Arrays.stream(values()).collect(Collectors.toMap(Mode::getName, (mode) -> mode));
		private final String name;

		Mode(String name) {
			this.name = name;
		}

		public static Mode deserialize(JsonObject object) throws JsonParseException {
			String string = GsonHelper.getAsString(object, "mode");
			Mode mode = VALUES_MAP.get(string);
			if (mode == null) {
				throw new JsonParseException("Unknown mode type: " + string);
			}
			return mode;
		}

		public String getName() {
			return this.name;
		}
	}

	@FunctionalInterface
	protected interface Deserializer<C> {
		C deserialize(JsonElement element, DeserializationContext conditionArrayParser) throws JsonParseException;
	}
}
