package com.minecraftabnormals.abnormals_core.core.util;

import com.minecraftabnormals.abnormals_core.core.api.conditions.BooleanConfigCondition;
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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	 * <h2>Function</h2>
	 * This method allows you to make crafting recipes, advancement modifiers, etc. check whether a specific config
	 * field is true before loading without having to hardcode new condition classes for specific cases. It's essentially
	 * a wrapper for {@link CraftingHelper#register(IConditionSerializer)} and should be called during common setup accordingly.
	 *
	 * <p>Specifically, it registers a {link BooleanConfigCondition.Serializer} under the name {@code "[modId]:config"}
	 * that accepts the names of the {@link ForgeConfigSpec.ConfigValue ForgeConfigSpec.ConfigValue&lt;Boolean&gt;}
	 * fields in {@code configObjects} as arguments  (formatted into snake case if {@code convertToSnakeCase} is true).</p><br>
	 *
	 * <h2>Implementation</h2>
	 * <p>All the objects in {@code configObjects} are mapped to the simple names of their class.
	 * This means the lowest-down name in the package tree, for example:<br>
	 * {@code com.minecraftabnormals.abnormals_core.core.config.ACConfig$Common} becomes {@code Common}.</p>
	 *
	 * <p>Similarly, all the {@link ForgeConfigSpec.ConfigValue ForgeConfigSpec.ConfigValue&lt;Boolean&gt;} fields in
	 * the classes in {@code configObjects} are mapped to their names.</p>
	 *
	 * <p>If {@code convertToSnakeCase} is true, these names are converted into snake case (e.g. {@code slabfishSettings})
	 * would become {@code slabfish_settings} for consistency with the rest of the JSON format.</p>
	 *
	 * <p>The stored names are used to target config fields from JSON files. When defining a condition with<br>
	 * {@code "type:" "[modId]:config"}<br>
	 * you use the {@code "config"} argument to specify the config <i>class</i> to target, and the {@code "name"}
	 * argument to specify the config <i>field</i> to target.</p>
	 *
	 * <p>For example, in a config condition created under the id {@code abnormals_core}, targeting the
	 * {@link com.minecraftabnormals.abnormals_core.core.config.ACConfig.Common} class and the
	 * {@code signEditingRequiresEmptyHand} field, the syntax would be like this:</p>
	 *
	 * <pre>{@code
	 * "conditions": [
	 *   {
	 *     "type": "abnormals_core:config"
	 *     "config": "common"
	 *     "name": "sign_editing_requires_empty_hand"
	 *   }
	 * ]
	 * }</pre>
	 *
	 * <p><i>While only the name of the config class is shown in JSON, it does still map to an actual object.
	 * When a condition is tested, {@link Field#get(Object)} is called passing in the mapped object.</i></p>
	 *
	 * @param modId the mod ID to register the condition under
	 * @param convertToSnakeCase if true, the accepted values for {@code config} and {@code name} in JSON files are
	 *                           converted to snake case first
	 * @param configObjects the list of objects to get config fields from
	 *
	 * @author abigailfails
	 * */
	public static void registerBooleanConfigCondition(String modId, boolean convertToSnakeCase, Object... configObjects) {
		Map<String, Object> newConfigObjects = new HashMap<>();
		Map<String, Field> newConfigFields = new HashMap<>();
		Arrays.asList(configObjects).forEach(cfg -> {
			newConfigObjects.put(snakeCase(cfg.getClass().getSimpleName()), cfg);
			Arrays.stream(cfg.getClass().getDeclaredFields()).filter(f -> ForgeConfigSpec.ConfigValue.class.isAssignableFrom(f.getType()) && ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0] == Boolean.class).forEach(f -> {
				f.setAccessible(true);
				newConfigFields.put(convertToSnakeCase ? snakeCase(f.getName()) : f.getName(), f);
			});
		});
		CraftingHelper.register(new BooleanConfigCondition.Serializer(modId, newConfigFields, newConfigObjects));
	}

	/**
	 * Converts {@code PascalCase} or {@code camelCase} strings to a {@code snake_case} format.
	 * <p>Can insert underscores between a lowercase letter and a capital letter <b>or</b> a number,
	 * or alternatively between a capital letter and a number.</p>
	 * <p>e.g:<br>
	 *        fgaAbc -> fga_abc  <br>
	 *        era09  -> era_09   <br>
	 *        kiAN0  -> ki_an_0  <br>
	 *        sy70p  -> sy_70p   <br>
	 *        sy70P  -> sy_70_p  </p>
	 *
	 * @param string the string to format
	 * @return {@code string}, formatted into snake case
	 *
	 * @author abigailfails
	 * */
	public static String snakeCase(String string) {
		return string.replaceAll("((?<=[a-z])([A-Z]|[0-9]))|((?<=[0-9])[A-Z])|((?<=[A-Z])[0-9])", "_$0").toLowerCase();
	}
}