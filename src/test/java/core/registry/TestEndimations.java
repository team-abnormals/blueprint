package core.registry;

import com.teamabnormals.blueprint.core.endimator.PlayableEndimation;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimationManager;
import core.BlueprintTest;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestEndimations {
	public static final PlayableEndimation BLOCK_ENTITY_WAVE = register("be/wave", 15, PlayableEndimation.LoopType.LOOP);
	public static final PlayableEndimation BLOCK_ENTITY_CLAPPING = register("be/clapping", 10, PlayableEndimation.LoopType.LOOP);
	public static final PlayableEndimation ROTATE = register("rotate", 40, PlayableEndimation.LoopType.NONE);
	public static final PlayableEndimation SINK = register("sink", 20, PlayableEndimation.LoopType.LOOP);
	public static final PlayableEndimation HOVER = register("hover", 40, PlayableEndimation.LoopType.HOLD);
	public static final PlayableEndimation COMPLEX = register("complex", 30, PlayableEndimation.LoopType.LOOP);

	private static PlayableEndimation register(String name, int duration, PlayableEndimation.LoopType loopType) {
		return PlayableEndimationManager.INSTANCE.registerPlayableEndimation(new PlayableEndimation(new ResourceLocation(BlueprintTest.MOD_ID, name), duration, loopType));
	}
}
