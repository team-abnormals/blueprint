package com.minecraftabnormals.abnormals_core.common.blocks;

import net.minecraft.world.level.block.PressurePlateBlock;

import net.minecraft.world.level.block.PressurePlateBlock.Sensitivity;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class AbnormalsPressurePlateBlock extends PressurePlateBlock {
	public AbnormalsPressurePlateBlock(Sensitivity sensitivityIn, Properties propertiesIn) {
		super(sensitivityIn, propertiesIn);
	}
}
