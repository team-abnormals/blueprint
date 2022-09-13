package com.teamabnormals.blueprint.core.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.annotations.ConfigKey;
import com.teamabnormals.blueprint.core.api.conditions.ConfigValueCondition;
import com.teamabnormals.blueprint.core.api.conditions.config.IConfigPredicate;
import com.teamabnormals.blueprint.core.api.conditions.config.IConfigPredicateSerializer;
import com.teamabnormals.blueprint.core.api.conditions.loot.ConfigLootCondition;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.BlockSource;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A utility class containing some useful stuff related to Minecraft data modification.
 *
 * @author bageldotjpg
 * @author SmellyModder (Luke Tonon)
 * @author abigailfails
 */
public final class DataUtil {
	public static final Field TAG_MANAGER = ObfuscationReflectionHelper.findField(ReloadableServerResources.class, "f_206849_");
	public static final Field REGISTRY_ACCESS = ObfuscationReflectionHelper.findField(TagManager.class, "f_144569_");
	private static final Method ADD_MIX_METHOD = ObfuscationReflectionHelper.findMethod(PotionBrewing.class, "m_43513_", Potion.class, Item.class, Potion.class);
	private static final Vector<AlternativeDispenseBehavior> ALTERNATIVE_DISPENSE_BEHAVIORS = new Vector<>();
	private static final Vector<CustomNoteBlockInstrument> CUSTOM_NOTE_BLOCK_INSTRUMENTS = new Vector<>();

	/**
	 * Registers a given {@link Block} to be flammable.
	 *
	 * @param block         A {@link Block} to be flammable.
	 * @param encouragement The encouragement for the block.
	 * @param flammability  The flammability for the block.
	 */
	public static void registerFlammable(Block block, int encouragement, int flammability) {
		FireBlock fire = (FireBlock) Blocks.FIRE;
		fire.setFlammable(block, encouragement, flammability);
	}

	/**
	 * Registers a given {@link ItemLike} to be compostable.
	 *
	 * @param item   An {@link ItemLike} to be compostable.
	 * @param chance The compost chance for the item.
	 */
	public static void registerCompostable(ItemLike item, float chance) {
		ComposterBlock.COMPOSTABLES.put(item.asItem(), chance);
	}

	/**
	 * Adds a potion mix.
	 *
	 * @param input    An input {@link Potion}.
	 * @param reactant A reactant {@link Item}.
	 * @param result   A resulting {@link Potion}.
	 */
	public static void addMix(Potion input, Item reactant, Potion result) {
		try {
			ADD_MIX_METHOD.invoke(null, input, reactant, result);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Failed to add mix for " + ForgeRegistries.POTIONS.getKey(result) + " from " + ForgeRegistries.ITEMS.getKey(reactant), e);
		}
	}

	/**
	 * Registers a {@link BlockColor} for a list of blocks.
	 *
	 * @param blockColors The {@link BlockColors} to register to.
	 * @param color       A {@link BlockColor} to use.
	 * @param blocksIn    A list of blocks to register.
	 */
	public static void registerBlockColor(BlockColors blockColors, BlockColor color, List<RegistryObject<Block>> blocksIn) {
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
	 * Registers an {@link ItemColor} for a list of block items.
	 *
	 * @param itemColors The {@link ItemColors} to register to.
	 * @param color      An {@link ItemColor} to use.
	 * @param blocksIn   A list of blocks to register.
	 */
	public static void registerBlockItemColor(ItemColors itemColors, ItemColor color, List<RegistryObject<Block>> blocksIn) {
		blocksIn.removeIf(block -> !block.isPresent());
		if (blocksIn.size() > 0) {
			Block[] blocks = new Block[blocksIn.size()];
			for (int i = 0; i < blocksIn.size(); i++) {
				blocks[i] = blocksIn.get(i).get();
			}
			itemColors.register(color, blocks);
		}
	}

	/**
	 * Adds a gift loot table to a {@link VillagerProfession}
	 *
	 * @param profession The profession that will give a gift
	 */
	public static void registerVillagerGift(VillagerProfession profession) {
		ResourceLocation name = ForgeRegistries.VILLAGER_PROFESSIONS.getKey(profession);
		if (name != null) {
			GiveGiftToHero.GIFTS.put(profession, new ResourceLocation(name.getNamespace(), "gameplay/hero_of_the_village/" + name.getPath() + "_gift"));
		}
	}

	/**
	 * Adds a {@link SoundEvent} for when a Parrot imitates a specific {@link EntityType}
	 *
	 * @param entityType The entity type to imitate
	 * @param soundEvent The sound event to play when imitating
	 */
	public static void registerParrotImitation(EntityType<?> entityType, SoundEvent soundEvent) {
		Parrot.MOB_SOUND_MAP.put(entityType, soundEvent);
	}

	/**
	 * Makes a concatenation of two arrays of the same type.
	 * <p>Useful for adding onto hardcoded arrays.</p>
	 *
	 * @param array A base array to add onto.
	 * @param toAdd An array to add onto the base array.
	 * @param <T>   The type of elements in the arrays.
	 * @return A concatenation of two arrays of the same type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] concatArrays(T[] array, @Nonnull T... toAdd) {
		int arrayLength = array.length;
		int toAddLength = toAdd.length;
		T[] newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), arrayLength + toAddLength);
		System.arraycopy(array, 0, newArray, 0, arrayLength);
		System.arraycopy(toAdd, 0, newArray, arrayLength, toAddLength);
		return newArray;
	}

	/**
	 * Concatenates an array from a given {@link Field} with a given array.
	 * <p>Useful for adding onto inaccessible hardcoded arrays.</p>
	 *
	 * @param arrayField A field to get the base array to add onto.
	 * @param object     An object to use when getting the base array from {@code arrayField}.
	 * @param toAdd      An array to add onto the base array.
	 * @param <T>        The type of elements in the arrays.
	 */
	@SuppressWarnings("unchecked")
	public static <T> void concatArrays(Field arrayField, @Nullable Object object, @Nonnull T... toAdd) {
		try {
			arrayField.set(object, concatArrays((T[]) arrayField.get(object), toAdd));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Changes the localization key of a block
	 *
	 * @param block The {@link Block} being re-localized
	 * @param modid The modid of the mod changing the localization
	 * @param name  The new name of the block
	 */
	public static void changeBlockLocalization(Block block, String modid, String name) {
		block.descriptionId = Util.makeDescriptionId("block", new ResourceLocation(modid, name));
	}

	/**
	 * Changes the localization key of a block
	 * Takes a {@link ResourceLocation}
	 *
	 * @param inputMod  The modid of the block being re-localized
	 * @param input     The name of the block being re-localized
	 * @param outputMod The modid of the mod changing the localization
	 * @param output    The new name of the block
	 */
	public static void changeBlockLocalization(String inputMod, String input, String outputMod, String output) {
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(inputMod, input));
		if (block != null)
			block.descriptionId = Util.makeDescriptionId("block", new ResourceLocation(outputMod, output));
	}

	/**
	 * Changes the localization key of an item
	 *
	 * @param item  The {@link Item} being re-localized
	 * @param modid The modid of the mod changing the localization
	 * @param name  The new name of the item
	 */
	public static void changeItemLocalization(Item item, String modid, String name) {
		item.descriptionId = Util.makeDescriptionId("item", new ResourceLocation(modid, name));
	}

	/**
	 * Changes the localization key of an item
	 * Takes a {@link ResourceLocation}
	 *
	 * @param inputMod  The modid of the item being re-localized
	 * @param input     The name of the item being re-localized
	 * @param outputMod The modid of the mod changing the localization
	 * @param output    The new name of the item
	 */
	public static void changeItemLocalization(String inputMod, String input, String outputMod, String output) {
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(inputMod, input));
		if (item != null)
			item.descriptionId = Util.makeDescriptionId("item", new ResourceLocation(outputMod, output));
	}

	/**
	 * Checks if a given {@link ResourceLocation} matches at least one location of a {@link ResourceKey} in set of {@link ResourceKey}s.
	 *
	 * @return If a given {@link ResourceLocation} matches at least one location of a {@link ResourceKey} in set of {@link ResourceKey}s.
	 */
	public static boolean matchesKeys(ResourceLocation loc, ResourceKey<?>... keys) {
		for (ResourceKey<?> key : keys)
			if (key.location().equals(loc))
				return true;
		return false;
	}

	/**
	 * <p>Slates a {@link AlternativeDispenseBehavior} instance for later processing, where it will be used to register
	 * an {@link DispenseItemBehavior} that performs the new behavior if its condition is met and the behavior that was
	 * already registered if not. See {@link AlternativeDispenseBehavior} for details.
	 *
	 * <p>Since Blueprint handles registering the condition at the right time, mods should call this method as
	 * early as possible, ideally in an {@link net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent#enqueueWork enqueueWork}
	 * call within an {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent FMLCommonSetupEvent}.</p>
	 *
	 * @param behavior The {@link AlternativeDispenseBehavior} to be registered.
	 * @author abigailfails
	 * @see AlternativeDispenseBehavior
	 */
	public static void registerAlternativeDispenseBehavior(AlternativeDispenseBehavior behavior) {
		ALTERNATIVE_DISPENSE_BEHAVIORS.add(behavior);
	}

	/**
	 * Registers a {@link CustomNoteBlockInstrument} that will get used to play a custom note block sound if a
	 * {@link BlockSource} predicate (representing the position under the note block) passes.
	 * See {@link CustomNoteBlockInstrument} for details.
	 *
	 * <p>Since Blueprint adds instruments to an internal list at the end of mod loading, mods should call
	 * this method as early as possible, ideally in an
	 * {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent FMLCommonSetupEvent} listener.</p>
	 *
	 * @param instrument The {@link CustomNoteBlockInstrument} to get registered.
	 * @author abigailfails
	 * @see CustomNoteBlockInstrument
	 */
	public static void registerNoteBlockInstrument(CustomNoteBlockInstrument instrument) {
		CUSTOM_NOTE_BLOCK_INSTRUMENTS.add(instrument);
	}

	/**
	 * Adds a new {@link StructurePoolElement} to a pre-existing {@link StructurePoolElement}.
	 *
	 * @param toAdd    The {@link ResourceLocation} of the pattern to insert the new piece into.
	 * @param newPiece The {@link StructurePoolElement} to insert into {@code toAdd}.
	 * @param weight   The probability weight of {@code newPiece}.
	 * @author abigailfails
	 */
	public static void addToJigsawPattern(ResourceLocation toAdd, StructurePoolElement newPiece, int weight) {
		StructureTemplatePool oldPool = BuiltinRegistries.TEMPLATE_POOL.get(toAdd);
		if (oldPool != null) {
			oldPool.rawTemplates.add(Pair.of(newPiece, weight));
			List<StructurePoolElement> jigsawPieces = oldPool.templates;
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
	 * <p>This method allows you to make crafting recipes, modifiers, loot tables, etc. check whether a specific config
	 * field is true/whether it meets specific predicates before loading without having to hardcode new condition classes
	 * for certain config values. It's essentially a wrapper for the condition and loot condition registry methods and
	 * should be called during common setup accordingly.</p><br><br>
	 *
	 * <h2>Implementation</h2>
	 * <p>All the {@link net.minecraftforge.common.ForgeConfigSpec.ConfigValue}s in the objects in
	 * {@code configObjects} with a {@link ConfigKey} annotation are mapped to the string values
	 * of their field's annotation.
	 *
	 * <p>The stored names are used to target config fields from JSON files. When defining a condition with<br>
	 * {@code "type": "[modId]:config"}<br>
	 * you use the {@code "value"} argument to specify the config value to target.
	 *
	 * <p>For example, in a config condition created under the id {@code blueprint}
	 * that checks whether {@code "sign_editing_requires_empty_hand"} (the annotated value for the
	 * {@code signEditingRequiresEmptyHand} field) is true, the syntax would be like this:</p>
	 *
	 * <pre>{@code
	 * "conditions": [
	 *   {
	 *     "type": "blueprint:config"
	 *     "value": "sign_editing_requires_empty_hand"
	 *   }
	 * ]
	 * }</pre>
	 *
	 * <p>Config conditions also accept a {@code predicates} array, which defines
	 * {@link IConfigPredicate IConfigPredicate}s that the
	 * config value must match before the condition returns true, and a boolean {@code inverted} argument which makes
	 * the condition pass if it evaluates to false instead of true. If the config value is non-boolean,
	 * {@code predicates} are required. Each individual predicate also accepts an {@code inverted} argument (as
	 * {@code !(A.B) != !A.!B}).</p>
	 *
	 * <p>For example, you could check whether a the float config value {@code "potato_poison_chance"} is less than
	 * 0.1 by using the {@code "blueprint:greater_than_or_equal_to"} predicate and inverting it. (Of course,
	 * in this situation it's easier to just use the {@code "blueprint:less_than"} predicate, but this is just
	 * an example used to show the syntax of inverting).</p>
	 *
	 * <pre>{@code
	 * "conditions": [
	 *   {
	 *     "type": "blueprint:config",
	 *     "value": "potato_poison_chance",
	 *     "predicates": [
	 *       {
	 *         "type": "blueprint:greater_than_or_equal_to",
	 *         "value": 0.1,
	 *         "inverted": true
	 *       }
	 *     ]
	 *   }
	 * ],
	 * }</pre>
	 *
	 * <p>Blueprint has pre-made predicates for numeric and string comparison as well as checking for equality,
	 * but you can create custom predicates and register them with
	 * {@link DataUtil#registerConfigPredicate(IConfigPredicateSerializer)}.</p>
	 *
	 * @param modId         The mod ID to register the config condition under. The reason this is required and that you can't just
	 *                      register your values under {@code "blueprint:config"} is because there could be duplicate keys
	 *                      between mods.
	 * @param configObjects The list of objects to get config keys from. The {@link ConfigKey} values must be unique.
	 * @return The created {@link LootItemConditionType} to register
	 * @author abigailfails
	 */
	public static LootItemConditionType registerConfigCondition(String modId, Object... configObjects) {
		HashMap<String, ForgeConfigSpec.ConfigValue<?>> configValues = new HashMap<>();
		for (Object object : configObjects) {
			for (Field field : object.getClass().getDeclaredFields()) {
				if (field.getAnnotation(ConfigKey.class) != null && ForgeConfigSpec.ConfigValue.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					try {
						configValues.put(field.getAnnotation(ConfigKey.class).value(), (ForgeConfigSpec.ConfigValue<?>) field.get(object));
					} catch (IllegalAccessException ignored) {
					}
				}
			}
		}
		CraftingHelper.register(new ConfigValueCondition.Serializer(modId, configValues));
		return new LootItemConditionType(new ConfigLootCondition.ConfigSerializer(modId, configValues));
	}

	/**
	 * Registers an {@link IConfigPredicateSerializer} for an
	 * {@link IConfigPredicate IConfigPredicate}.
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

	/**
	 * Returns the list of registered {@link AlternativeDispenseBehavior}s, sorted by their comparators. Intended for
	 * internal use in order to register the behaviors to the dispenser registry.
	 *
	 * @author abigailfails
	 */
	public static List<AlternativeDispenseBehavior> getSortedAlternativeDispenseBehaviors() {
		List<AlternativeDispenseBehavior> behaviors = new ArrayList<>(ALTERNATIVE_DISPENSE_BEHAVIORS);
		Collections.sort(behaviors);
		return behaviors;
	}

	/**
	 * Returns the list of registered {@link CustomNoteBlockInstrument}s, sorted by their comparators.
	 * <b>Intended for internal use.</b>
	 *
	 * @author abigailfails
	 */
	public static List<CustomNoteBlockInstrument> getSortedCustomNoteBlockInstruments() {
		List<CustomNoteBlockInstrument> instruments = new ArrayList<>(CUSTOM_NOTE_BLOCK_INSTRUMENTS);
		Collections.sort(instruments);
		return instruments;
	}

	public static RegistryOps<JsonElement> createRegistryOps(ReloadableServerResources serverResources) throws IllegalAccessException {
		return RegistryOps.create(JsonOps.INSTANCE, (RegistryAccess) REGISTRY_ACCESS.get(TAG_MANAGER.get(serverResources)));
	}

	/**
	 * When an instance of this class is registered using {@link DataUtil#registerAlternativeDispenseBehavior(AlternativeDispenseBehavior)},
	 * an {@link DispenseItemBehavior} will get registered that will perform a new {@link DispenseItemBehavior} if
	 * a condition is met and the behavior that was already in the registry if not. See constructor for details.
	 *
	 * <p>This works even if multiple mods
	 * add new behavior to the same item, though the conditions may overlap, which is what
	 * {@code modIdComparator} is intended to solve.</p>
	 *
	 * @author abigailfails
	 */
	public static class AlternativeDispenseBehavior implements Comparable<AlternativeDispenseBehavior> {
		protected final String modId;
		protected final Item item;
		protected final BiPredicate<BlockSource, ItemStack> condition;
		protected final DispenseItemBehavior behavior;
		protected final Comparator<String> modIdComparator;

		/**
		 * Initialises a new {@link AlternativeDispenseBehavior} where {@code condition} decides whether {@code behavior}
		 * should get used instead of the behavior previously stored in the dispenser registry for {@code item}.
		 *
		 * <p>Ideally, the condition should be implemented such that the predicate only passes if the new behavior will
		 * be 'successful', avoiding problems with failure sounds not playing.</p>
		 *
		 * @param modId     The ID of the mod registering the condition.
		 * @param item      The {@link Item} to register the {@code behavior} for.
		 * @param condition A {@link BiPredicate} that takes in {@link BlockSource} and {@link ItemStack} arguments,
		 *                  returning true if {@code behavior} should be performed.
		 * @param behavior  The {@link DispenseItemBehavior} that will be used if the {@code condition} is met.
		 */
		public AlternativeDispenseBehavior(String modId, Item item, BiPredicate<BlockSource, ItemStack> condition, DispenseItemBehavior behavior) {
			this(modId, item, condition, behavior, (id1, id2) -> 0);
		}

		/**
		 * Initialises a new {@link AlternativeDispenseBehavior}, where {@code condition} decides whether {@code behavior}
		 * should get used instead of the behavior previously stored in the dispenser registry for {@code item}.
		 *
		 * <p>Ideally, the condition should be implemented such that the predicate only passes if the new behavior will
		 * be 'successful', avoiding problems with failure sounds not playing.</p>
		 *
		 * <p>If multiple mods add a behavior to the same item and the conditions overlap such that the order that they
		 * are registered in matters, {@code modIdComparator} (where the first parameter is {@code modId} and the second
		 * parameter is the mod ID of another {@link AlternativeDispenseBehavior} instance)
		 * can be used to ensure this order regardless of which mod is loaded first.</p>
		 *
		 * <p>For example, if a mod with the ID {@code a} has a behavior where its condition passes if any block is in front
		 * of the dispenser, but a mod with the ID {@code b} has a behavior for the same item that passes only if a specific
		 * block is in front of the dispenser, authors may want to make sure that {@code b}'s condition is registered after
		 * {@code a}'s. In this case, {@code a}'s {@code modIdComparator} should be something like
		 * {@code (id1, id2) -> id2.equals("b") ? -1 : 0}, and {@code b}'s should be {@code (id1, id2) -> id2.equals("a") ? 1 : 0}.</p>
		 *
		 * @param modId           The ID of the mod registering the condition.
		 * @param item            The {@link Item} to register the {@code behavior} for.
		 * @param condition       A {@link BiPredicate} that takes in {@link BlockSource} and {@link ItemStack} arguments,
		 *                        returning true if {@code behavior} should be performed.
		 * @param behavior        The {@link DispenseItemBehavior} that will be used if the {@code condition} is met.
		 * @param modIdComparator A {@link Comparator} that compares two strings. The first is {@code modId}, and the
		 *                        second is the mod id for another behavior registered to the same item.
		 *                        It should return 1 if {@code behavior} is to be registered after the other behavior, -1 if
		 *                        it should go before, and 0 in any other case.
		 */
		public AlternativeDispenseBehavior(String modId, Item item, BiPredicate<BlockSource, ItemStack> condition, DispenseItemBehavior behavior, Comparator<String> modIdComparator) {
			this.modId = modId;
			this.item = item;
			this.condition = condition;
			this.behavior = behavior;
			this.modIdComparator = modIdComparator;
		}

		@Override
		public int compareTo(AlternativeDispenseBehavior behavior) {
			return this.item == behavior.item ? this.modIdComparator.compare(this.modId, behavior.modId) : 0;
		}

		/**
		 * Registers an {@link DispenseItemBehavior} for {@code item} which performs {@code behavior} if
		 * {@code condition} passes.
		 */
		public void register() {
			DispenseItemBehavior oldBehavior = DispenserBlock.DISPENSER_REGISTRY.get(item);
			DispenserBlock.registerBehavior(item, (source, stack) -> condition.test(source, stack) ? behavior.dispense(source, stack) : oldBehavior.dispense(source, stack));
		}
	}

	/**
	 * When an instance of this class is registered using
	 * {@link DataUtil#registerNoteBlockInstrument(CustomNoteBlockInstrument)}, note blocks will play a custom sound
	 * if an {@link BlockSource} predicate for the position under the note block passes. See constructor for details.
	 *
	 * <p>If multiple mods add new instruments the predicates may overlap, which is what
	 * {@code modIdComparator} is intended to solve.</p>
	 *
	 * @author abigailfails
	 */
	public static class CustomNoteBlockInstrument implements Comparable<CustomNoteBlockInstrument> {
		protected final String modId;
		protected final Comparator<String> modIdComparator;
		protected final Predicate<BlockSource> condition;
		private final SoundEvent sound;

		/**
		 * Initialises a new {@link CustomNoteBlockInstrument} where {@code condition} decides whether {@code sound}
		 * should get played instead of vanilla's when a note block is triggered.
		 *
		 * @param modId     The ID of the mod registering the condition.
		 * @param condition A {@link Predicate} that takes in a {@link BlockSource} instance that represents the
		 *                  position under the note block, returning true if {@code sound} should be played.
		 * @param sound     The {@link SoundEvent} that will be played if {@code condition} is met.
		 */
		public CustomNoteBlockInstrument(String modId, Predicate<BlockSource> condition, SoundEvent sound) {
			this(modId, condition, sound, (id1, id2) -> 0);
		}

		/**
		 * Initialises a new {@link CustomNoteBlockInstrument} where {@code condition} decides whether {@code sound}
		 * should get played instead of vanilla's when a note block is triggered.
		 *
		 * <p>If multiple mods add new instruments and the {@link BlockSource} predicates overlap such that the order
		 * that they are registered in matters, {@code modIdComparator} (where the first parameter is {@code modId} and
		 * the second parameter is the mod ID of another {@link CustomNoteBlockInstrument} instance) can be used to
		 * ensure this order regardless of which mod is loaded first.</p>
		 *
		 * <p>For example, if a mod with the ID {@code a} has an instrument that plays if the block under the note
		 * block's material is {@code HEAVY_METAL}, but a mod with the ID {@code b} has an instrument that plays if the
		 * block is a lodestone, authors may want to make sure that {@code b}'s condition is tested before {@code a}'s.
		 * In this case, {@code a}'s {@code modIdComparator} should be something like
		 * {@code (id1, id2) -> id2.equals("b") ? 1 : 0}, and {@code b}'s should be
		 * {@code (id1, id2) -> id2.equals("a") ? -1 : 0}.</p>
		 *
		 * @param modId           The ID of the mod registering the condition.
		 * @param condition       A {@link Predicate} that takes in a {@link BlockSource} instance that represents the
		 *                        position under the note block, returning true if {@code sound} should be played.
		 * @param sound           The {@link SoundEvent} that will be played if {@code condition} is met.
		 * @param modIdComparator A {@link Comparator} that compares two strings. The first is {@code modId}, and the
		 *                        second is the mod id for another note block instrument.
		 *                        It should return 1 if {@code condition} should be tested after the other instrument's,
		 *                        -1 if it should go before, and 0 in any other case.
		 */
		public CustomNoteBlockInstrument(String modId, Predicate<BlockSource> condition, SoundEvent sound, Comparator<String> modIdComparator) {
			this.modId = modId;
			this.condition = condition;
			this.sound = sound;
			this.modIdComparator = modIdComparator;
		}

		@Override
		public int compareTo(CustomNoteBlockInstrument instrument) {
			return this.modIdComparator.compare(this.modId, instrument.modId);
		}

		public boolean test(BlockSource source) {
			return this.condition.test(source);
		}

		public SoundEvent getSound() {
			return this.sound;
		}
	}
}