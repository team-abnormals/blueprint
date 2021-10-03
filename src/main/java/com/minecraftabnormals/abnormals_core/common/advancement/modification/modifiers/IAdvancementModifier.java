package com.minecraftabnormals.abnormals_core.common.advancement.modification.modifiers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.minecraftabnormals.abnormals_core.core.util.modification.IModifier;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.util.GsonHelper;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An interface extending the {@link IModifier} interface, typed to be used on {@link Advancement.Builder}s.
 *
 * @param <C> The type of config object for this modifier.
 * @author SmellyModder (Luke Tonon)
 * @see IModifier
 */
public interface IAdvancementModifier<C> extends IModifier<Advancement.Builder, C, Void, DeserializationContext> {

	enum Mode {
		MODIFY("modify"),
		REPLACE("replace");

		private static final Map<String, IAdvancementModifier.Mode> VALUES_MAP = Arrays.stream(values()).collect(Collectors.toMap(IAdvancementModifier.Mode::getName, (mode) -> mode));
		private final String name;

		Mode(String name) {
			this.name = name;
		}

		public static IAdvancementModifier.Mode deserialize(JsonObject object) throws JsonParseException {
			String string = GsonHelper.getAsString(object, "mode");
			IAdvancementModifier.Mode mode = VALUES_MAP.get(string);
			if (mode == null) {
				throw new JsonParseException("Unknown mode type: " + string);
			}
			return mode;
		}

		public void serialize(JsonObject object) {
			object.addProperty("mode", this.name);
		}

		public String getName() {
			return this.name;
		}
	}

}
