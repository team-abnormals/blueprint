package com.minecraftabnormals.abnormals_core.core.utils;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fml.RegistryObject;

public class DataUtils {
	
	public static void registerFlammable(Block block, int encouragement, int flammability) {
		FireBlock fire = (FireBlock) Blocks.FIRE;
		fire.setFireInfo(block, encouragement, flammability);
	}

	public static void registerCompostable(float chance, IItemProvider item) {
		ComposterBlock.CHANCES.put(item.asItem(), chance);
	}
	
	public static void registerBlockColor(BlockColors blockColors, IBlockColor color, List<RegistryObject<Block>> blocksIn) {
		List<RegistryObject<Block>> registryObjects = blocksIn;
		registryObjects.removeIf(block -> !block.isPresent());
		if(registryObjects.size() > 0) {
			Block[] blocks = new Block[registryObjects.size()];
			for(int i = 0; i < registryObjects.size(); i++) {
				blocks[i] = registryObjects.get(i).get();
			}
			blockColors.register(color, blocks);
		}
	}
	
	public static void registerBlockItemColor(ItemColors blockColors, IItemColor color, List<RegistryObject<Block>> blocksIn) {
		List<RegistryObject<Block>> registryObjects = blocksIn;
		registryObjects.removeIf(block -> !block.isPresent());
		if(registryObjects.size() > 0) {
			Block[] blocks = new Block[registryObjects.size()];
			for(int i = 0; i < registryObjects.size(); i++) {
				blocks[i] = registryObjects.get(i).get();
			}
			blockColors.register(color, blocks);
		}
	}
	
}