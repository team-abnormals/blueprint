package core.registry;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.world.storage.receiver.LevelConcurrentHashMapReceiver;
import com.teamabnormals.blueprint.common.world.storage.receiver.LevelNoiseReceiver;
import common.world.TestDataReceiverFeature;
import common.world.TestSplineFeature;
import core.BlueprintTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.WorldgenRandom.Algorithm;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestFeatures {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, BlueprintTest.MOD_ID);

	public static final RegistryObject<Feature<NoneFeatureConfiguration>> TEST_SPLINE = FEATURES.register("spline", () -> new TestSplineFeature(NoneFeatureConfiguration.CODEC));
	public static final RegistryObject<Feature<NoneFeatureConfiguration>> TEST_DATA_RECEIVER = FEATURES.register("data_receiver", () -> new TestDataReceiverFeature(NoneFeatureConfiguration.CODEC));

	public static final LevelNoiseReceiver HOE_DIAMONDS = new LevelNoiseReceiver(Algorithm.LEGACY, TestNoises.HOE_DIAMONDS);
	public static final LevelConcurrentHashMapReceiver<Pair<Integer, Integer>, BlockPos> BASTION_POSITIONS = new LevelConcurrentHashMapReceiver<>();

	public static final class TestConfiguredFeatures {
		public static final ResourceKey<ConfiguredFeature<?, ?>> TEST_DATA_RECEIVER = createKey("test_data_receiver");

		public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
			register(context, TEST_DATA_RECEIVER, TestFeatures.TEST_DATA_RECEIVER.get(), NoneFeatureConfiguration.INSTANCE);
		}

		public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
			return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(BlueprintTest.MOD_ID, name));
		}

		public static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
			context.register(key, new ConfiguredFeature<>(feature, config));
		}
	}

	public static final class TestPlacedFeatures {
		public static final ResourceKey<PlacedFeature> TEST_DATA_RECEIVER = createKey("test_data_receiver");

		public static void bootstrap(BootstapContext<PlacedFeature> context) {
			register(context, TEST_DATA_RECEIVER, TestConfiguredFeatures.TEST_DATA_RECEIVER, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE);
		}

		public static ResourceKey<PlacedFeature> createKey(String name) {
			return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(BlueprintTest.MOD_ID, name));
		}

		public static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?, ?>> feature, List<PlacementModifier> modifiers) {
			context.register(key, new PlacedFeature(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(feature), modifiers));
		}

		public static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?, ?>> feature, PlacementModifier... modifiers) {
			register(context, key, feature, List.of(modifiers));
		}
	}
}
