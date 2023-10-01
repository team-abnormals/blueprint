package com.teamabnormals.blueprint.common.block;

import com.teamabnormals.blueprint.common.block.entity.BlueprintChiseledBookShelfBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BlueprintChiseledBookShelfBlock extends ChiseledBookShelfBlock {

	public BlueprintChiseledBookShelfBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BlueprintChiseledBookShelfBlockEntity(pos, state);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof ChiseledBookShelfBlockEntity bookShelf) {
			Optional<Vec2> optional = getRelativeHitCoordinatesForBlockFace(result, state.getValue(HorizontalDirectionalBlock.FACING));
			if (optional.isEmpty()) {
				return InteractionResult.PASS;
			} else {
				int i = this.getHitSlot(optional.get());
				if (state.getValue(SLOT_OCCUPIED_PROPERTIES.get(i))) {
					removeBook(level, pos, player, bookShelf, i);
					return InteractionResult.sidedSuccess(level.isClientSide);
				} else {
					ItemStack stack = player.getItemInHand(hand);
					if (stack.is(ItemTags.BOOKSHELF_BOOKS)) {
						addBook(level, pos, player, bookShelf, stack, i);
						return InteractionResult.sidedSuccess(level.isClientSide);
					} else {
						return InteractionResult.CONSUME;
					}
				}
			}
		} else {
			return InteractionResult.PASS;
		}
	}

	public int getHitSlot(Vec2 vec2) {
		int i = vec2.y >= 0.5F ? 0 : 1;
		int j = getSection(vec2.x);
		return j + i * 3;
	}

	private static int getSection(float x) {
		if (x < 0.375F) {
			return 0;
		} else {
			return x < 0.6875F ? 1 : 2;
		}
	}
}
