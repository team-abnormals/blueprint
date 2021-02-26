package com.minecraftabnormals.abnormals_core.core.util;

import com.google.common.collect.ImmutableMap;
import com.minecraftabnormals.abnormals_core.core.annotations.ConfigKey;
import com.minecraftabnormals.abnormals_core.core.api.conditions.ConfigValueCondition;
import com.minecraftabnormals.abnormals_core.core.api.conditions.config.IConfigPredicateSerializer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.management.ImmutableDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.function.BiPredicate;

public final class DataUtil {
	private static final Method ADD_MIX_METHOD = ObfuscationReflectionHelper.findMethod(PotionBrewing.class, "func_193357_a", Potion.class, Item.class, Potion.class);

	public static void registerFlammable(Block block, int encouragement, int flammability) {
		FireBlock fire = (FireBlock) Blocks.FIRE;
		fire.setFireInfo(block, encouragement, flammability);
	}

	public static void registerCompostable(IItemProvider item, float chance) {
		ComposterBlock.CHANCES.put(item.asItem(), chance);
	}

	public static void addMix(Potion input, Item reactant, Potion result) {
		try {
			ADD_MIX_METHOD.invoke(null, input, reactant, result);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Failed to add mix for " + result.getRegistryName() + " from " + reactant.getRegistryName(), e);
		}
	}

	public static void registerBlockColor(BlockColors blockColors, IBlockColor color, List<RegistryObject<Block>> blocksIn) {
		blocksIn.removeIf(block -> !block.isPresent());
		if (blocksIn.size() > 0) {
			Block[] blocks = new Block[blocksIn.size()];
			for (int i = 0; i < blocksIn.size(); i++) {
				blocks[i] = blocksIn.get(i).get();
			}
			blockColors.register(color, blocks);
		}
	}

	public static void registerBlockItemColor(ItemColors blockColors, IItemColor color, List<RegistryObject<Block>> blocksIn) {
		blocksIn.removeIf(block -> !block.isPresent());
		if (blocksIn.size() > 0) {
			Block[] blocks = new Block[blocksIn.size()];
			for (int i = 0; i < blocksIn.size(); i++) {
				blocks[i] = blocksIn.get(i).get();
			}
			blockColors.register(color, blocks);
		}
	}

	/**
	 * Adds an EnchantmentType to an EnchantmentType array
	 */
	public static EnchantmentType[] add(EnchantmentType[] array, EnchantmentType element) {
		int arrayLength = Array.getLength(array);
		Object newArrayObject = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
		System.arraycopy(array, 0, newArrayObject, 0, arrayLength);
		array[array.length - 1] = element;
		return array;
	}

	/**
	 * Checks if a given {@link ResourceLocation} matches at least one location of a {@link RegistryKey} in set of {@link RegistryKey}s.
	 *
	 * @return If a given {@link ResourceLocation} matches at least one location of a {@link RegistryKey} in set of {@link RegistryKey}s.
	 */
	public static boolean matchesKeys(ResourceLocation loc, RegistryKey<?>... keys) {
		for (RegistryKey<?> key : keys)
			if (key.getLocation().equals(loc))
				return true;
		return false;
	}

	/**
	 * <p>Registers a {@link IDispenseItemBehavior} that will perform the new behavior if the condition is met and the behavior that was already in the registry if not.
	 * This works even if multiple mods add new behavior to the same item.</p>
	 * <p>Ideally, the condition should be implemented such that the predicate only passes if the new behavior will be 'successful', avoiding problems with failure sounds not playing.</p>
	 *
	 * @param item The {@link Item} to register the {@code newBehavior} for.
	 * @param condition A {@link BiPredicate} that takes in {@link IBlockSource} and {@link ItemStack} arguments, returning true if the {@code newBehavior} should be performed.
	 * @param newBehavior The {@link IDispenseItemBehavior} that will be used if the {@code condition} is met.
	 *
	 * @author abigailfails
	 */
	public static void registerAlternativeDispenseBehavior(Item item, BiPredicate<IBlockSource, ItemStack> condition, IDispenseItemBehavior newBehavior) {
		IDispenseItemBehavior oldBehavior = DispenserBlock.DISPENSE_BEHAVIOR_REGISTRY.get(item);
		DispenserBlock.registerDispenseBehavior(item, (source, stack) -> {
			return condition.test(source, stack) ? newBehavior.dispense(source, stack) : oldBehavior.dispense(source, stack);
		});
	}

	/**
	 * Adds a new {@link JigsawPiece} to a pre-existing {@link JigsawPattern}.
	 *
	 * @param toAdd The {@link ResourceLocation} of the pattern to insert the new piece into.
	 * @param newPiece The {@link JigsawPiece} to insert into {@code toAdd}.
	 * @param weight The probability weight of {@code newPiece}.
	 *
	 * @author abigailfails
	 */
	public static void addToJigsawPattern(ResourceLocation toAdd, JigsawPiece newPiece, int weight) {
		JigsawPattern oldPool = WorldGenRegistries.JIGSAW_POOL.getOrDefault(toAdd);
		if (oldPool != null) {
			oldPool.rawTemplates.add(Pair.of(newPiece, weight));
			List<JigsawPiece> jigsawPieces = oldPool.jigsawPieces;
			for (int i = 0; i < weight; i++) {
				jigsawPieces.add(newPiece);
			}
		}
	}

	/**
	 * Registers a {@link ConfigValueCondition.Serializer} under the name {@code "[modId]:config"}
	 * that accepts the values of {@link ConfigKey} annotations for {@link net.minecraftforge.common.ForgeConfigSpec.ConfigValue}
	 * fields in the passed-in collection of objects, checking against the annotation's corresponding
	 * {@link net.minecraftforge.common.ForgeConfigSpec.ConfigValue} to determine whether the condition should pass.<br><br>
	 * <h2>Function</h2>
	 * <p>This method allows you to make crafting recipes, advancement modifiers, etc. check whether a specific config
	 * field is true/whether it meets specific predicates before loading without having to hardcode new condition classes
	 * for certain config values. It's essentially a wrapper for {@link CraftingHelper#register(IConditionSerializer)}
	 * and should be called during common setup accordingly.</p><br><br>
	 *
	 * <h2>Implementation</h2>
	 * <p>All the {@link net.minecraftforge.common.ForgeConfigSpec.ConfigValue}s in the objects in
	 * {@code configObjects} with a {@link ConfigKey} annotation are mapped to the string values
	 * of their field's annotation.

	 * <p>The stored names are used to target config fields from JSON files. When defining a condition with<br>
	 * {@code "type": "[modId]:config"}<br>
	 * you use the {@code "value"} argument to specify the config value to target.
	 *
	 * <p>For example, in a config condition created under the id {@code abnormals_core}
	 * that checks whether {@code "sign_editing_requires_empty_hand"} (the annotated value for the
	 * {@code signEditingRequiresEmptyHand} field) is true, the syntax would be like this:</p>
	 *
	 * <pre>{@code
	 * "conditions": [
	 *   {
	 *     "type": "abnormals_core:config"
	 *     "value": "sign_editing_requires_empty_hand"
	 *   }
	 * ]
	 * }</pre>
	 *
	 * <p>Config conditions also accept a {@code predicates} array which defines predicates the config value must match
	 * before the condition returns true, and a boolean {@code inverted} argument which makes the condition pass if it
	 * evaluates to false instead of true. If the config value is non-boolean, {@code predicates} is required.
	 * Each individual predicate also accepts an {@code inverted} argument, as {@code !(A.B) != !A.!B}.</p>
	 *
	 * <p>For example, you could check whether a the float config value {@code "potato_poison_chance"} is less than
	 * 0.1 by using the {@code "abnormals_core:greater_than_or_equal_to"} predicate and inverting it. (Of course,
	 * in this situation it's easier to just use the {@code "abnormals_core:less_than"} predicate, but this is just
	 * an example used to show the syntax of inverting).</p>
	 *
	 * <pre>{@code
	 * "conditions": [
	 *   {
	 *     "type": "abnormals_core:config",
	 *     "value": "potato_poison_chance",
	 *     "predicates": [
	 *       {
	 *         "type": "abnormals_core:greater_than_or_equal_to",
	 *         "value": 0.1,
	 *         "inverted": true
	 *       }
	 *     ]
	 *   }
	 * ],
	 * }</pre>
	 *
	 * <p>Abnormals Core has pre-made predicates for numeric and string comparison as well as checking for equality,
	 * but you can create custom predicates and register them with
	 * {@link DataUtil#registerConfigPredicate(IConfigPredicateSerializer)}.</p>
	 *
	 * @param modId The mod ID to register the config condition under. The reason this is required and that you can't just
	 *              register your values under {@code "abnormals_core:config"} is because there could be duplicate keys
	 *              between mods.
	 * @param configObjects The list of objects to get config keys from. The {@link ConfigKey} values must be unique.
	 *
	 * @author abigailfails
	 */
	public static void registerConfigCondition(String modId, Object... configObjects) {
		HashMap<String, ForgeConfigSpec.ConfigValue<?>> configValues = new HashMap<>();
		for (Object object : configObjects) {
			for (Field field : object.getClass().getDeclaredFields()) {
				if (field.getAnnotation(ConfigKey.class) != null && ForgeConfigSpec.ConfigValue.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					try {
						configValues.put(field.getAnnotation(ConfigKey.class).value(), (ForgeConfigSpec.ConfigValue<?>) field.get(object));
					} catch (IllegalAccessException ignored) {}
				}
			}
		}
		CraftingHelper.register(new ConfigValueCondition.Serializer(modId, ImmutableMap.copyOf(configValues)));
	}

	/**
	 * Registers an {@link IConfigPredicateSerializer} for an
	 * {@link com.minecraftabnormals.abnormals_core.core.api.conditions.config.IConfigPredicate}.
	 *
	 * <p>The predicate takes in a {@link ForgeConfigSpec.ConfigValue} and returns true if it matches specific conditions.</p>
	 *
	 * @param serializer The serializer to register.
	 */
	public static void registerConfigPredicate(IConfigPredicateSerializer<?> serializer) {
		ResourceLocation key = serializer.getID();
		if (ConfigValueCondition.Serializer.CONFIG_PREDICATE_SERIALIZERS.containsKey(key))
			throw new IllegalStateException("Duplicate config predicate serializer: " + key);
		ConfigValueCondition.Serializer.CONFIG_PREDICATE_SERIALIZERS.put(key, serializer);
	}
}
