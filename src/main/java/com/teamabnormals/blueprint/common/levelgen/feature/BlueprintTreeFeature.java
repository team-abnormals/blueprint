package com.teamabnormals.blueprint.common.levelgen.feature;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

import java.util.HashMap;
import java.util.Set;
import java.util.function.BiConsumer;

public abstract class BlueprintTreeFeature extends Feature<TreeConfiguration> {
	public Set<BlockPos> logPositions;
	public Set<BlockPos> foliagePositions;

	public HashMap<BlockPos, BlockState> specialLogPositions;
	public HashMap<BlockPos, BlockState> specialFoliagePositions;

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

		this.logPositions = Sets.newHashSet();
		this.foliagePositions = Sets.newHashSet();
		this.specialLogPositions = Maps.newHashMap();
		this.specialFoliagePositions = Maps.newHashMap();

		if (this.canSurvive(level, origin)) {
			this.doPlace(context);

			for (BlockPos logPos : this.logPositions) {
				if (!TreeFeature.validTreePos(level, logPos) || logPos.getY() > level.getMaxBuildHeight())
					return false;
			}

			for (BlockPos foliagePos : this.foliagePositions) {
				if (!TreeFeature.validTreePos(level, foliagePos) || foliagePos.getY() > level.getMaxBuildHeight())
					return false;
			}

			this.doMidPlace(context);

			this.logPositions.forEach(logPos -> {
				level.setBlock(logPos, this.specialLogPositions.getOrDefault(logPos, config.trunkProvider.getState(random, logPos)), 19);
				if (logPos.getY() == origin.getY() && this.placeDirt) {
					setDirtAt(level, random, logPos.below(), config);
				}
			});
			this.foliagePositions.forEach(foliagePos -> {
				if (TreeFeature.validTreePos(level, foliagePos)) {
					BlockState state = this.specialFoliagePositions.getOrDefault(foliagePos, config.foliageProvider.getState(random, foliagePos));

					if (TreeFeature.isBlockWater(level, foliagePos) && state.hasProperty(BlockStateProperties.WATERLOGGED)) {
						state = state.setValue(BlockStateProperties.WATERLOGGED, true);
					}

					if (!state.isAir()) {
						level.setBlock(foliagePos, state, 19);
					}
				}
			});

			Set<BlockPos> decorationPositions = Sets.newHashSet();
			BiConsumer<BlockPos, BlockState> decorationSetter = (decorationPos, state) -> {
				decorationPositions.add(decorationPos.immutable());
				level.setBlock(decorationPos, state, 19);
			};

			if (!config.decorators.isEmpty()) {
				TreeDecorator.Context decoratorContext = new TreeDecorator.Context(level, decorationSetter, random, this.logPositions, this.foliagePositions, Sets.newHashSet());
				config.decorators.forEach((decorator) -> decorator.place(decoratorContext));
			}

			this.doPostPlace(context);

			return BoundingBox.encapsulatingPositions(Iterables.concat(this.logPositions, this.foliagePositions, decorationPositions)).map((boundingBox) -> {
				DiscreteVoxelShape shape = TreeFeature.updateLeaves(level, boundingBox, this.logPositions, decorationPositions, Set.of());
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

	public abstract void doPlace(FeaturePlaceContext<TreeConfiguration> context);

	public void doMidPlace(FeaturePlaceContext<TreeConfiguration> context) {
	}

	public void doPostPlace(FeaturePlaceContext<TreeConfiguration> context) {
	}

	public void addLog(BlockPos pos) {
		this.logPositions.add(pos.immutable());
	}

	public void addSpecialLog(BlockPos pos, BlockState state) {
		this.addLog(pos);
		this.specialLogPositions.put(pos.immutable(), state);
	}

	public void addFoliage(BlockPos pos) {
		this.foliagePositions.add(pos.immutable());
	}

	public void addSpecialFoliage(BlockPos pos, BlockState state) {
		this.addFoliage(pos);
		this.specialFoliagePositions.put(pos.immutable(), state);
	}

	public static void setDirtAt(WorldGenLevel level, RandomSource random, BlockPos pos, TreeConfiguration config) {
		if (config.forceDirt || level.isStateAtPosition(pos, state -> state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.MYCELIUM))) {
			level.setBlock(pos, config.dirtProvider.getState(random, pos), 19);
		}
	}
}