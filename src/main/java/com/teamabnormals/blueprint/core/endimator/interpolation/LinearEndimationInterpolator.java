package com.teamabnormals.blueprint.core.endimator.interpolation;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.core.endimator.EndimationKeyframe;
import net.minecraft.util.Mth;

/**
 * An {@link EndimationInterpolator} instance that interpolates linearly.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LinearEndimationInterpolator extends EndimationInterpolator<Unit> {

	public LinearEndimationInterpolator() {
		super(Codec.unit(Unit.INSTANCE), Unit.INSTANCE);
	}

	@Override
	public void apply(Unit config, VecConsumer consumer, EndimationKeyframe[] keyframes, EndimationKeyframe keyframe, int index, int keyframeCount, float progress) {
		if (index == 0) {
			consumer.accept(Mth.lerp(progress, 0.0F, keyframe.x), Mth.lerp(progress, 0.0F, keyframe.y), Mth.lerp(progress, 0.0F, keyframe.z));
		} else {
			EndimationKeyframe prev = keyframes[index - 1];
			consumer.accept(Mth.lerp(progress, prev.x, keyframe.x), Mth.lerp(progress, prev.y, keyframe.y), Mth.lerp(progress, prev.z, keyframe.z));
		}
	}

}
