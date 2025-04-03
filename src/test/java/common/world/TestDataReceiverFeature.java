package common.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import core.registry.TestFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

import java.util.Map;

public class TestDataReceiverFeature extends Feature<NoneFeatureConfiguration> {
	public TestDataReceiverFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		BlockPos origin = context.origin();
		BlockPos bastionPos = getClosestBastionPosition(level, origin);

		double d0 = bastionPos.getX() - origin.getX();
		double d1 = bastionPos.getZ() - origin.getZ();
		double d2 = Math.sqrt(d0 * d0 + d1 * d1);

		if (d2 > 128.0D)
			return false;

		Block block = d2 < 32.0D ? Blocks.RED_CONCRETE : d2 < 64.0D ? Blocks.ORANGE_CONCRETE : d2 < 96.0D ? Blocks.YELLOW_CONCRETE : Blocks.GREEN_CONCRETE;
		level.setBlock(origin, block.defaultBlockState(), 2);

		return true;
	}

	private static BlockPos getClosestBastionPosition(WorldGenLevel level, BlockPos pos) {
		ServerLevel serverLevel = level.getLevel();
		Map<Pair<Integer, Integer>, BlockPos> positions = TestFeatures.BASTION_POSITIONS.get(serverLevel);
		int chunkX = pos.getX() >> 4;
		int chunkZ = pos.getZ() >> 4;

		Pair<Integer, Integer> spacingPos = Pair.of(Math.floorDiv(chunkX, 27), Math.floorDiv(chunkZ, 27));
		if (!positions.containsKey(spacingPos)) {
			positions.put(spacingPos, getPotentialStructureChunk(serverLevel.getSeed(), spacingPos.getFirst(), spacingPos.getSecond()).getWorldPosition());
		}

		return positions.get(spacingPos);
	}

	private static ChunkPos getPotentialStructureChunk(long seed, int spacingX, int spacingZ) {
		WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
		worldgenrandom.setLargeFeatureWithSalt(seed, spacingX, spacingZ, 30084232);
		int k = 27 - 4;
		int l = RandomSpreadType.LINEAR.evaluate(worldgenrandom, k);
		int i1 = RandomSpreadType.LINEAR.evaluate(worldgenrandom, k);
		return new ChunkPos(spacingX * 27 + l, spacingZ * 27 + i1);
	}
}