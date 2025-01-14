package core.registry;

import common.world.TestSplineFeature;
import core.BlueprintTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TestFeatures {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, BlueprintTest.MOD_ID);

	public static final DeferredHolder<? super Feature<NoneFeatureConfiguration>, TestSplineFeature> TEST_SPLINE = FEATURES.register("spline", () -> new TestSplineFeature(NoneFeatureConfiguration.CODEC));
}
