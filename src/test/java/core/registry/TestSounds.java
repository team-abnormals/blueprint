package core.registry;

import com.teamabnormals.blueprint.core.events.LoadThisClassEvent;
import com.teamabnormals.blueprint.core.util.registry.SoundSubRegistryHelper;
import core.BlueprintTest;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class TestSounds {
	@SubscribeEvent
	public static void $(LoadThisClassEvent event) {}

	public static final SoundSubRegistryHelper HELPER = BlueprintTest.REGISTRY_HELPER.getSoundSubHelper();

	public static final DeferredHolder<SoundEvent, SoundEvent> AMBIENCE_TEST = HELPER.createSoundEvent("ambient.end_city.test");
}
