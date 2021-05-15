package com.minecraftabnormals.abnormals_core.core.annotations;

import com.minecraftabnormals.abnormals_core.core.api.conditions.ConfigValueCondition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation, when applied to {@link net.minecraftforge.common.ForgeConfigSpec.ConfigValue ConfigValue} fields
 * in objects that are passed into the {@code Object...} parameter of
 * {@link com.minecraftabnormals.abnormals_core.core.util.DataUtil#registerConfigCondition(String, Object...) registerConfigCondition},
 * allows the value to be referred to in JSON by a {@link ConfigValueCondition ConfigValueCondition}
 * or {@link com.minecraftabnormals.abnormals_core.core.api.conditions.loot.ConfigLootCondition ConfigLootCondition} matching
 * the mod ID passed into {@link com.minecraftabnormals.abnormals_core.core.util.DataUtil#registerConfigCondition(String, Object...) registerConfigCondition}.
 *
 * <p>The {@code value} of this annotation determines what string needs to be specified in a config condition to get the
 * value associated with its field. For example, if you had a class like this:
 *
 * <pre>{@code
 * public class ExampleConfigKey {
 *     @ConfigKey("test_field")
 *     public final ConfigValue<Boolean> testField; //Initialized somewhere else
 * }
 * }</pre>
 *
 * And an instance of {@code ExampleConfigKey} was registered under a config condition with {@code example_id}, the
 * {@code testField} in that instance could be tested by using its annotation's value as shown here:
 *
 * <pre>{@code
 * "conditions": [
 *   {
 *     "type": "example_id:config"
 *     "value": "test_field"
 *   }
 * ]
 * }</pre>
 * </p>
 *
 * @author abigailfails
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigKey {
    /**
     * The string value to look for in JSON when retrieving this field. For config values under the same mod ID, this
     * has to be unique.
     *
     * @return the string key for this config value
     */
    String value();
}
