package com.teamabnormals.blueprint.common.block.entity;

import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

/**
 * A {@link ChiseledBookShelfBlockEntity} extension used for Blueprint's chiseled bookshelves.
 */
public class BlueprintChiseledBookShelfBlockEntity extends ChiseledBookShelfBlockEntity {

	public BlueprintChiseledBookShelfBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Nonnull
	@Override
	public BlockEntityType<?> getType() {
		return BlueprintBlockEntityTypes.CHISELED_BOOKSHELF.get();
	}
}
