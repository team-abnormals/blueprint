package core.registry;

import com.teamabnormals.blueprint.core.util.registry.SoundSubRegistryHelper;
import core.BlueprintTest;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestSounds {
	public static final SoundSubRegistryHelper HELPER = BlueprintTest.REGISTRY_HELPER.getSoundSubHelper();

	public static final RegistryObject<SoundEvent> AMBIENCE_TEST = HELPER.createSoundEvent("ambient.end_city.test");
}
