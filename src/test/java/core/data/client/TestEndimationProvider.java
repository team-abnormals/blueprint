package core.data.client;

import com.teamabnormals.blueprint.core.endimator.util.EndimationProvider;
import core.BlueprintTest;
import net.minecraft.data.DataGenerator;

import static com.teamabnormals.blueprint.core.endimator.Endimation.Builder.Keyframes.*;
import static com.teamabnormals.blueprint.core.endimator.Endimation.PartKeyframes.Builder.*;

public final class TestEndimationProvider extends EndimationProvider {

	public TestEndimationProvider(DataGenerator dataGenerator) {
		super(dataGenerator, BlueprintTest.MOD_ID);
	}

	@Override
	protected void addEndimations() {
		this.endimation("sink")
				.keyframes(
						keyframes()
								.part("cube", partKeyframes().pos(linear(0.0F), catmullRom(0.5F, 0.0F, -16.0F, 0.0F), catmullRom(1.0F)))

				);
	}

}
