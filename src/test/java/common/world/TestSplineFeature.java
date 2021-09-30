package common.world;

import com.minecraftabnormals.abnormals_core.core.util.MathUtil;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class TestSplineFeature extends Feature<NoneFeatureConfiguration> {
	private static final Vec3 UP = new Vec3(0.0F, 1.0F, 0.0F);

	public TestSplineFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		BlockPos pos = context.origin();
		Random rand = context.random();
		BlockPos end = pos.offset(rand.nextInt(16) - rand.nextInt(16), rand.nextInt(33) - rand.nextInt(33), rand.nextInt(16) - rand.nextInt(16));
		List<Vec3> points = new ArrayList<>();
		Vec3 startVec = Vec3.atLowerCornerOf(pos);
		Vec3 endVec = Vec3.atLowerCornerOf(end);
		Vec3 difference = endVec.subtract(startVec);
		Vec3 normalizedDifference = difference.normalize();
		Vec3 anchorStart = startVec.subtract(normalizedDifference).add(0, -6, 0);
		Vec3 anchorEnd = endVec.add(normalizedDifference).add(0, -6, 0);
		points.add(anchorStart);
		points.add(startVec);
		Vec3 offset = UP.cross(normalizedDifference);
		double offsetX = offset.x;
		double offsetZ = offset.z;
		for (int i = 0; i < 6; i++) {
			points.add(startVec.add(difference.scale(i / 6.0F)).add(offsetX * (rand.nextDouble() - 0.5D) * 6.0D, (rand.nextDouble() - 0.5D) * 8.0D, offsetZ * (rand.nextDouble() - 0.5D) * 6.0D));
		}
		points.add(endVec);
		points.add(anchorEnd);
		MathUtil.CatmullRomSpline catmullRomSpline = new MathUtil.CatmullRomSpline(points.toArray(new Vec3[0]), MathUtil.CatmullRomSpline.SplineType.CENTRIPETAL);
		int steps = (int) (20 + Math.sqrt(pos.distSqr(end)) * 4);
		BlockPos prevPos = null;
		WorldGenLevel level = context.level();
		for (int i = 0; i < steps; i++) {
			float progress = i / (float) steps;
			BlockPos interpolatedPos = catmullRomSpline.interpolate(progress);
			if (interpolatedPos.equals(prevPos)) {
				continue;
			}
			prevPos = interpolatedPos;
			level.setBlock(interpolatedPos, Blocks.DIAMOND_BLOCK.defaultBlockState(), 2);
		}
		return true;
	}
}
