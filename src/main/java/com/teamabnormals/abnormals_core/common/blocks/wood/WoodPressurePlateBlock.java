package com.teamabnormals.abnormals_core.common.blocks.wood;

import com.teamabnormals.abnormals_core.common.blocks.AbnormalsPressurePlateBlock;
import com.teamabnormals.abnormals_core.core.util.ItemStackUtil;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;

public class WoodPressurePlateBlock extends AbnormalsPressurePlateBlock {
	public WoodPressurePlateBlock(Sensitivity sensitivityIn, Properties propertiesIn) {
		super(sensitivityIn, propertiesIn);
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		ItemStackUtil.fillAfterItemForGroup(this.asItem(), Items.WARPED_PRESSURE_PLATE, group, items);
	}
}
