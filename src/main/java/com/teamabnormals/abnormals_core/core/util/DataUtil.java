package com.teamabnormals.abnormals_core.core.util;

import java.lang.reflect.Array;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fml.RegistryObject;

public final class DataUtil {
	
	public static void registerFlammable(Block block, int encouragement, int flammability) {
		FireBlock fire = (FireBlock) Blocks.FIRE;
		fire.setFireInfo(block, encouragement, flammability);
	}
	
	public static void registerCompostable(IItemProvider item, float chance) {
		ComposterBlock.CHANCES.put(item.asItem(), chance);
	}
	
	public static void registerBlockColor(BlockColors blockColors, IBlockColor color, List<RegistryObject<Block>> blocksIn) {
		List<RegistryObject<Block>> registryObjects = blocksIn;
		registryObjects.removeIf(block -> !block.isPresent());
		if (registryObjects.size() > 0) {
			Block[] blocks = new Block[registryObjects.size()];
			for (int i = 0; i < registryObjects.size(); i++) {
				blocks[i] = registryObjects.get(i).get();
			}
			blockColors.register(color, blocks);
		}
	}
	
	public static void registerBlockItemColor(ItemColors blockColors, IItemColor color, List<RegistryObject<Block>> blocksIn) {
		List<RegistryObject<Block>> registryObjects = blocksIn;
		registryObjects.removeIf(block -> !block.isPresent());
		if (registryObjects.size() > 0) {
			Block[] blocks = new Block[registryObjects.size()];
			for (int i = 0; i < registryObjects.size(); i++) {
				blocks[i] = registryObjects.get(i).get();
			}
			blockColors.register(color, blocks);
		}
	}
	
	/**
	 * Adds an EnchantmentType to an EnchantmentType array
	 */
    public static EnchantmentType[] add(EnchantmentType[] array, EnchantmentType element) {
        EnchantmentType[] newArray = array;
        int arrayLength = Array.getLength(newArray);
        Object newArrayObject = Array.newInstance(newArray.getClass().getComponentType(), arrayLength + 1);
        System.arraycopy(array, 0, newArrayObject, 0, arrayLength);
        newArray[newArray.length - 1] = element;
        return newArray;
    }
	
}