package core.registry;

import com.minecraftabnormals.abnormals_core.core.annotations.Test;
import common.world.TestSplineFeature;
import core.ACTest;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Test
@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestFeatures {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ACTest.MOD_ID);

	public static final RegistryObject<Feature<NoFeatureConfig>> TEST_SPLINE = FEATURES.register("spline", () -> new TestSplineFeature(NoFeatureConfig.CODEC));
}
