package common.block;

import com.mojang.serialization.MapCodec;
import common.block.entity.TestEndimatedBlockEntity;
import core.registry.TestBlockEntities;
import core.registry.TestEndimations;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public final class TestEndimatedBlock extends BaseEntityBlock {
	public static final MapCodec<TestEndimatedBlock> CODEC = simpleCodec(TestEndimatedBlock::new);

	public TestEndimatedBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult) {
		boolean isClientSide = level.isClientSide;
		if (!isClientSide) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof TestEndimatedBlockEntity test) {
				test.setPlayingEndimation(test.isEndimationPlaying(TestEndimations.BLOCK_ENTITY_CLAPPING) ? TestEndimations.BLOCK_ENTITY_WAVE : TestEndimations.BLOCK_ENTITY_CLAPPING);
				level.sendBlockUpdated(pos, state, state, 3);
			}
		}
		return InteractionResult.sidedSuccess(isClientSide);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, TestBlockEntities.TEST_ENDIMATED.get(), (testLevel, pos, testState, test) -> {
			test.endimateTick();
		});
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return TestBlockEntities.TEST_ENDIMATED.get().create(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState p_49232_) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
}
