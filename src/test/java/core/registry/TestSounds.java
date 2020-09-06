package core.registry;

import com.teamabnormals.abnormals_core.core.annotations.Test;
import com.teamabnormals.abnormals_core.core.util.RegistryHelper;
import core.ACTest;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Test
@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestSounds {
	public static final RegistryHelper HELPER = ACTest.REGISTRY_HELPER;

	public static final RegistryObject<SoundEvent> AMBIENCE_TEST = HELPER.createSoundEvent("ambient.end_city.test");
}
