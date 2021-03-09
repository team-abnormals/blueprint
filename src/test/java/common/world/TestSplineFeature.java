package common.world;

import com.minecraftabnormals.abnormals_core.core.util.MathUtil;
import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class TestSplineFeature extends Feature<NoFeatureConfig> {
	private static final Vector3d UP = new Vector3d(0.0F, 1.0F, 0.0F);

	public TestSplineFeature(Codec<NoFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		BlockPos end = pos.add(rand.nextInt(33) - rand.nextInt(33), rand.nextInt(33) - rand.nextInt(33), rand.nextInt(33) - rand.nextInt(33));
		List<Vector3d> points = new ArrayList<>();
		Vector3d startVec = Vector3d.copy(pos);
		Vector3d endVec = Vector3d.copy(end);
		Vector3d difference = endVec.subtract(startVec);
		Vector3d normalizedDifference = difference.normalize();
		Vector3d anchorStart = startVec.subtract(normalizedDifference).add(0, -6, 0);
		Vector3d anchorEnd = endVec.add(normalizedDifference).add(0, -6, 0);
		points.add(anchorStart);
		points.add(startVec);
		Vector3d offset = UP.crossProduct(normalizedDifference);
		double offsetX = offset.x;
		double offsetZ = offset.z;
		for (int i = 0; i < 6; i++) {
			points.add(startVec.add(difference.scale(i / 6.0F)).add(offsetX * (rand.nextDouble() - 0.5D) * 6.0D, (rand.nextDouble() - 0.5D) * 8.0D, offsetZ * (rand.nextDouble() - 0.5D) * 6.0D));
		}
		points.add(endVec);
		points.add(anchorEnd);
		MathUtil.CatmullRomSpline catmullRomSpline = new MathUtil.CatmullRomSpline(points.toArray(new Vector3d[0]), MathUtil.CatmullRomSpline.SplineType.CENTRIPETAL);
		int steps = (int) (20 + Math.sqrt(pos.distanceSq(end)) * 4);
		BlockPos prevPos = null;
		for (int i = 0; i < steps; i++) {
			float progress = i / (float) steps;
			BlockPos interpolatedPos = catmullRomSpline.interpolate(progress);
			if (interpolatedPos.equals(prevPos)) {
				continue;
			}
			prevPos = interpolatedPos;
			world.setBlockState(interpolatedPos, Blocks.DIAMOND_BLOCK.getDefaultState(), 2);
		}
		return true;
	}
}
