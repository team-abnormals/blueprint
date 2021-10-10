package core.registry;

import com.minecraftabnormals.abnormals_core.core.util.registry.SoundSubRegistryHelper;
import core.ACTest;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;

@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestSounds {
	public static final SoundSubRegistryHelper HELPER = ACTest.REGISTRY_HELPER.getSoundSubHelper();

	public static final RegistryObject<SoundEvent> AMBIENCE_TEST = HELPER.createSoundEvent("ambient.end_city.test");
}
