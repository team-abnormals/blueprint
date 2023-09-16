package com.teamabnormals.blueprint.common.codec;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.DataUtil;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.function.Function;

/**
 * Similar to Mojang's {@link ExtraCodecs} class, but for even more extra codecs!
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintExtraCodecs {
	/**
	 * A codec for Forge's {@link ICondition} system.
	 * <p>Either left -> Array of stable conditions</p>
	 * <p>Either right -> JsonArray of unstable conditions.</p>
	 * <p>Use {@link Either#right(Object)} when serializing unavailable conditions.</p>
	 * <p>If {@link Either#right()} is present after deserialization, that means false.</p>
	 */
	public static final Codec<Function<ICondition.IContext, Either<ICondition[], JsonArray>>> CONDITIONS = ExtraCodecs.JSON.flatXmap(element -> {
		if (!(element instanceof JsonArray array)) return DataResult.error(() -> "Conditions must be an array");
		int size = array.size();
		for (int i = 0; i < size; i++) {
			if (!(array.get(i) instanceof JsonObject))
				return DataResult.error(() -> "Conditions must be an array of JsonObjects");
		}
		return DataResult.success(DataUtil.memoize(context -> {
			ICondition[] iConditions = new ICondition[size];
			try {
				for (int i = 0; i < size; i++) {
					ICondition condition = CraftingHelper.getCondition((JsonObject) array.get(i));
					if (!condition.test(context)) return Either.right(array);
					iConditions[i] = condition;
				}
			} catch (JsonParseException exception) {
				Blueprint.LOGGER.error("Failed to lazy deserialize conditions: {}", array, exception);
				return Either.right(new JsonArray());
			}
			return Either.left(iConditions);
		}));
	}, function -> {
		var conditions = function.apply(ICondition.IContext.EMPTY);
		var unstableConditions = conditions.right();
		if (unstableConditions.isPresent()) return DataResult.success(unstableConditions.get());
		try {
			return DataResult.success(CraftingHelper.serialize(conditions.left().get()));
		} catch (JsonParseException exception) {
			return DataResult.error(exception::getMessage);
		}
	});
}
