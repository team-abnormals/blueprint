package com.minecraftabnormals.abnormals_core.core.api.conditions.config;

import com.minecraftabnormals.abnormals_core.core.api.conditions.ConfigValueCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * A predicate for a {@link ConfigValueCondition}, takes in a {@link net.minecraftforge.common.ForgeConfigSpec.ConfigValue} and returns a boolean for whether it matches the condition.
 *
 * @author abigailfails
 */
public interface IConfigPredicate {
	/**
	 * Gets the ID of the predicate that will be checked for when deserializing from JSON (e.g. {@code "abnormals_core:equals"}).
	 *
	 * @return A {@link ResourceLocation} representing the predicate's unique identifier.
	 */
	ResourceLocation getID();

	/**
	 * Takes in a {@link ForgeConfigSpec.ConfigValue} and returns true if it matches the predicate's condition.
	 *
	 * <p>As {@code value} can be of any type, if the predicate only works on a specific type it should throw an exception.
	 * if {@code value} is the wrong type. However, if the serializer is written correctly it should detect this error first.</p>
	 *
	 * @param value The config value to check against.
	 * @return Whether {@code value} meets the predicate.
	 * @throws IllegalArgumentException If {@code value} is of an invalid type.
	 */
	boolean test(ForgeConfigSpec.ConfigValue<?> value) throws IllegalArgumentException;
}
