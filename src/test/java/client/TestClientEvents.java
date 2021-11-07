package client;

import com.teamabnormals.blueprint.core.endimator.*;
import com.teamabnormals.blueprint.core.endimator.util.ModelEndimatorCache;
import core.BlueprintTest;
import core.registry.TestEndimations;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID, value = Dist.CLIENT)
public final class TestClientEvents {
	@OnlyIn(Dist.CLIENT)
	public static final ModelEndimatorCache<HumanoidModel<?>> HUMANOID_ENDIMATORS = ModelEndimatorCache.forType(humanoidModel -> {
		Endimator endimator = new Endimator();
		endimator.put("head", (EndimatablePart) humanoidModel.head);
		return endimator;
	});

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		Player player = event.player;
		if (player.isInWater()) {
			Endimatable endimatable = (Endimatable) player;
			if (endimatable.isNoEndimationPlaying()) {
				endimatable.setPlayingEndimation(TestEndimations.HUMANOID_NOD);
			}
		}
	}
}
