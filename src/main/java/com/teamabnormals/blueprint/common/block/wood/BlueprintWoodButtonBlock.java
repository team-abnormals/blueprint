package com.teamabnormals.blueprint.common.block.wood;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/**
 * A {@link ButtonBlock} extension that simplifies construction for wooden buttons.
 */
public class BlueprintWoodButtonBlock extends ButtonBlock {

	public BlueprintWoodButtonBlock(Properties properties, BlockSetType setType) {
		super(properties, setType, 30, true);
	}

}
