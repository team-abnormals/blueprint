package com.teamabnormals.abnormals_core.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class ItemFuel extends BlockItem {
    private int burnTime;

    public ItemFuel(Block block, Properties properties, int burnTimeIn) {
        super(block, properties);
        burnTime = burnTimeIn;
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return burnTime;
    }
}
