package client;

import com.teamabnormals.blueprint.core.endimator.*;
import com.teamabnormals.blueprint.core.endimator.util.ModelEndimatorCache;
import core.BlueprintTest;
import core.registry.TestEndimations;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = BlueprintTest.MOD_ID, value = Dist.CLIENT)
public final class TestClientEvents {
	@OnlyIn(Dist.CLIENT)
	public static final ModelEndimatorCache<HumanoidModel<?>> HUMANOID_ENDIMATORS = ModelEndimatorCache.forType(humanoidModel -> {
		Endimator endimator = new Endimator();
		endimator.put("head", (EndimatablePart) humanoidModel.head);
		return endimator;
	});

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Pre event) {
		Player player = event.getEntity();
		if (player.isInWater()) {
			Endimatable endimatable = (Endimatable) player;
			if (endimatable.isNoEndimationPlaying()) {
				endimatable.setPlayingEndimation(TestEndimations.HUMANOID_NOD);
			}
		}
	}
}
