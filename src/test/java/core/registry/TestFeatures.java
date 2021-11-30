package core.registry;

import common.world.TestSplineFeature;
import core.BlueprintTest;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestFeatures {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, BlueprintTest.MOD_ID);

	public static final RegistryObject<Feature<NoneFeatureConfiguration>> TEST_SPLINE = FEATURES.register("spline", () -> new TestSplineFeature(NoneFeatureConfiguration.CODEC));
}
