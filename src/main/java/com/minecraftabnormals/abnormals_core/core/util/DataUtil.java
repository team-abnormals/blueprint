package com.minecraftabnormals.abnormals_core.core.util;

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
import net.minecraft.entity.ai.brain.task.GiveHeroGiftsTask;
import net.minecraft.entity.merchant.villager.VillagerProfession;
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
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

	public static void registerVillagerGift(VillagerProfession profession) {
		ResourceLocation name = profession.getRegistryName();
		if (name != null) {
			GiveHeroGiftsTask.GIFTS.put(profession, new ResourceLocation(name.getNamespace(), "gameplay/hero_of_the_village/" + name.getPath() + "_gift"));
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
}
