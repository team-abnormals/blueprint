package com.teamabnormals.blueprint.common.block.entity;

import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

/**
 * A {@link BeehiveBlockEntity} extension used for Blueprint's beehives.
 */
public class AbnormalsBeehiveBlockEntity extends BeehiveBlockEntity {

	public AbnormalsBeehiveBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Nonnull
	@Override
	public BlockEntityType<?> getType() {
		return BlueprintBlockEntityTypes.BEEHIVE.get();
	}

}
