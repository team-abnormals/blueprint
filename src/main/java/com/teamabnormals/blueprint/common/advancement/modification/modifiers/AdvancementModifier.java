package com.teamabnormals.blueprint.common.advancement.modification.modifiers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.teamabnormals.blueprint.core.util.modification.ObjectModifier;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.util.GsonHelper;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An interface extending the {@link ObjectModifier} interface, typed to be used on {@link Advancement.Builder} instances.
 *
 * @author SmellyModder (Luke Tonon)
 * @see ObjectModifier
 */
public interface AdvancementModifier<M extends AdvancementModifier<M>> extends ObjectModifier<Advancement.Builder, Void, DeserializationContext, M> {
	/**
	 * A serializable enum used by some built-in {@link AdvancementModifier} implementations to signify if an operation is modifying or replacing something.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	//TODO: Possibly get rid of this
	enum Mode {
		MODIFY("modify"),
		REPLACE("replace");

		private static final Map<String, AdvancementModifier.Mode> VALUES_MAP = Arrays.stream(values()).collect(Collectors.toMap(AdvancementModifier.Mode::getName, (mode) -> mode));
		private final String name;

		Mode(String name) {
			this.name = name;
		}

		public static AdvancementModifier.Mode deserialize(JsonObject object) throws JsonParseException {
			String string = GsonHelper.getAsString(object, "mode");
			AdvancementModifier.Mode mode = VALUES_MAP.get(string);
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

	/**
	 * A {@link ObjectModifier.Serializer} extension, typed to be used for {@link AdvancementModifier} types.
	 *
	 * @param <M> The type of {@link AdvancementModifier} instances to serialize and deserialize.
	 * @author SmellyModder (Luke Tonon)
	 */
	interface Serializer<M extends AdvancementModifier<M>> extends ObjectModifier.Serializer<M, Void, DeserializationContext> {}
}
