package com.teamabnormals.abnormals_core.core.utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.util.IItemProvider;

public class DataUtils {
	public static void registerFlammable(Block block, int encouragement, int flammability) {
		FireBlock fire = (FireBlock) Blocks.FIRE;
		fire.setFireInfo(block, encouragement, flammability);
	}

	public static void registerCompostable(float chance, IItemProvider item) {
		ComposterBlock.CHANCES.put(item.asItem(), chance);
	}

//	public static void registerStrippable(Block log, Block stripped) {
//		AxeItem.BLOCK_STRIPPING_MAP = Maps.newHashMap(AxeItem.BLOCK_STRIPPING_MAP);
//		AxeItem.BLOCK_STRIPPING_MAP.put(log, stripped);
//	}
}
