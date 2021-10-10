package core.registry;

import common.world.TestSplineFeature;
import core.ACTest;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestFeatures {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ACTest.MOD_ID);

	public static final RegistryObject<Feature<NoneFeatureConfiguration>> TEST_SPLINE = FEATURES.register("spline", () -> new TestSplineFeature(NoneFeatureConfiguration.CODEC));
}
