package com.teamabnormals.blueprint.common.levelgen.feature;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

import java.util.HashMap;
import java.util.Set;
import java.util.function.BiConsumer;

public abstract class BlueprintTreeFeature extends Feature<TreeConfiguration> {
	public boolean placeDirt;

	public BlueprintTreeFeature(Codec<TreeConfiguration> config) {
		this(true, config);
	}

	public BlueprintTreeFeature(boolean placeDirt, Codec<TreeConfiguration> config) {
		super(config);
		this.placeDirt = placeDirt;
	}

	@Override
	public boolean place(FeaturePlaceContext<TreeConfiguration> context) {
		TreeConfiguration config = context.config();
		WorldGenLevel level = context.level();
		RandomSource random = context.random();
		BlockPos origin = context.origin();

		TreeInfo info = new TreeInfo(context);

		if (this.canSurvive(level, origin)) {
			this.doPlace(context, info);

			for (BlockPos logPos : info.logMap().keySet()) {
				if (!TreeFeature.validTreePos(level, logPos) || logPos.getY() > level.getMaxBuildHeight())
					return false;
			}

			for (BlockPos foliagePos : info.foliageMap().keySet()) {
				if (!TreeFeature.validTreePos(level, foliagePos) || foliagePos.getY() > level.getMaxBuildHeight())
					return false;
			}

			this.doMidPlace(context, info);

			info.logMap().forEach((logPos, logState) -> {
				level.setBlock(logPos, logState, 19);
				if (logPos.getY() == origin.getY() && this.placeDirt) {
					setDirtAt(level, random, logPos.below(), config);
				}
			});
			info.foliageMap().forEach((foliagePos, foliageState) -> {
				if (TreeFeature.validTreePos(level, foliagePos)) {
					if (foliageState.hasProperty(BlockStateProperties.WATERLOGGED)) {
						foliageState = foliageState.setValue(BlockStateProperties.WATERLOGGED, level.isFluidAtPosition(foliagePos, (fluidState) -> fluidState.isSourceOfType(Fluids.WATER)));
					}

					if (!foliageState.isAir()) {
						level.setBlock(foliagePos, foliageState, 19);
					}
				}
			});

			Set<BlockPos> decorationPositions = Sets.newHashSet();
			BiConsumer<BlockPos, BlockState> decorationSetter = (decorationPos, state) -> {
				decorationPositions.add(decorationPos.immutable());
				level.setBlock(decorationPos, state, 19);
			};

			if (!config.decorators.isEmpty()) {
				TreeDecorator.Context decoratorContext = new TreeDecorator.Context(level, decorationSetter, random, info.logMap().keySet(), info.foliageMap().keySet(), Sets.newHashSet());
				config.decorators.forEach((decorator) -> decorator.place(decoratorContext));
			}

			this.doPostPlace(context, info);

			return BoundingBox.encapsulatingPositions(Iterables.concat(info.logMap().keySet(), info.foliageMap().keySet(), decorationPositions)).map((boundingBox) -> {
				DiscreteVoxelShape shape = TreeFeature.updateLeaves(level, boundingBox, info.logMap().keySet(), decorationPositions, Set.of());
				StructureTemplate.updateShapeAtEdge(level, 3, shape, boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
				return true;
			}).orElse(false);
		} else {
			return false;
		}
	}

	public abstract BlockState getSapling();

	public boolean canSurvive(WorldGenLevel level, BlockPos pos) {
		return this.getSapling().canSurvive(level, pos);
	}

	public abstract void doPlace(FeaturePlaceContext<TreeConfiguration> context, TreeInfo info);

	public void doMidPlace(FeaturePlaceContext<TreeConfiguration> context, TreeInfo info) {
	}

	public void doPostPlace(FeaturePlaceContext<TreeConfiguration> context, TreeInfo info) {
	}

	public static void setDirtAt(WorldGenLevel level, RandomSource random, BlockPos pos, TreeConfiguration config) {
		if (config.forceDirt || !isDirt(level, pos)) {
			level.setBlock(pos, config.dirtProvider.getState(random, pos), 19);
		}
	}

	public static boolean isDirt(LevelSimulatedReader level, BlockPos pos) {
		return level.isStateAtPosition(pos, state -> Feature.isDirt(state) && !state.is(Blocks.GRASS_BLOCK) && !state.is(Blocks.MYCELIUM));
	}

	public record TreeInfo(FeaturePlaceContext<TreeConfiguration> context, HashMap<BlockPos, BlockState> logMap, HashMap<BlockPos, BlockState> foliageMap) {

		public TreeInfo(FeaturePlaceContext<TreeConfiguration> context) {
			this(context, Maps.newHashMap(), Maps.newHashMap());
		}

		public void addLog(BlockPos pos, BlockState state) {
			logMap.put(pos.immutable(), state);
		}

		public void addLog(BlockPos pos) {
			addLog(pos, context.config().trunkProvider.getState(context.random(), pos));
		}

		public void addAxisLog(BlockPos pos, Direction.Axis axis) {
			BlockState state = context.config().trunkProvider.getState(context.random(), pos);
			if (state.hasProperty(BlockStateProperties.AXIS)) {
				addLog(pos, state.setValue(BlockStateProperties.AXIS, axis));
			} else {
				addLog(pos);
			}
		}

		public void addAxisLog(BlockPos pos, Direction direction) {
			addAxisLog(pos, direction.getAxis());
		}

		public void addFoliage(BlockPos pos, BlockState state) {
			foliageMap.put(pos.immutable(), state);
		}

		public void addFoliage(BlockPos pos) {
			addFoliage(pos, context.config().foliageProvider.getState(context.random(), pos));
		}
	}
}