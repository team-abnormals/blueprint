package com.minecraftabnormals.abnormals_core.common.blocks.sign;

import com.minecraftabnormals.abnormals_core.core.registry.ACBlockEntities;
import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemCategoryFiller;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * A {@link StandingSignBlock} extension used for Abnormals Core's standing signs.
 */
public class AbnormalsStandingSignBlock extends StandingSignBlock implements IAbnormalsSign {
	private static final TargetedItemCategoryFiller FILLER = new TargetedItemCategoryFiller(() -> Items.WARPED_SIGN);

	public AbnormalsStandingSignBlock(Properties properties, WoodType woodType) {
		super(properties, woodType);
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		FILLER.fillItem(this.asItem(), tab, list);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ACBlockEntities.SIGN.get().create(pos, state);
	}
}