package com.teamabnormals.blueprint.common.block;

import com.teamabnormals.blueprint.common.block.entity.BlueprintChiseledBookShelfBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;

public class BlueprintChiseledBookShelfBlock extends ChiseledBookShelfBlock {

	public BlueprintChiseledBookShelfBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BlueprintChiseledBookShelfBlockEntity(pos, state);
	}

	@Override
	public OptionalInt getHitSlot(BlockHitResult result, BlockState state) {
		return getRelativeHitCoordinatesForBlockFace(result, state.getValue(HorizontalDirectionalBlock.FACING)).map(this::getHitSlot).orElseGet(OptionalInt::empty);
	}

	public static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult hitResult, Direction face) {
		return ChiseledBookShelfBlock.getRelativeHitCoordinatesForBlockFace(hitResult, face);
	}

	public OptionalInt getHitSlot(Vec2 vec2) {
		int i = vec2.y >= 0.5F ? 0 : 1;
		int j = getSection(vec2.x);
		return OptionalInt.of(j + i * 3);
	}
}
