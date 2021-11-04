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
			consumer.accept(Mth.lerp(progress, 0.0F, keyframe.preX.get()), Mth.lerp(progress, 0.0F, keyframe.preY.get()), Mth.lerp(progress, 0.0F, keyframe.preZ.get()));
		} else {
			EndimationKeyframe prev = keyframes[index - 1];
			consumer.accept(Mth.lerp(progress, prev.postX.get(), keyframe.preX.get()), Mth.lerp(progress, prev.postY.get(), keyframe.preY.get()), Mth.lerp(progress, prev.postZ.get(), keyframe.preZ.get()));
		}
	}

}
