package core;

import client.TestEndimatedEntityRenderer;
import com.google.common.collect.Sets;
import com.minecraftabnormals.abnormals_core.common.world.modification.BiomeFeatureModifier;
import com.minecraftabnormals.abnormals_core.common.world.modification.BiomeModificationManager;
import com.minecraftabnormals.abnormals_core.common.world.modification.BiomeModificationPredicates;
import com.minecraftabnormals.abnormals_core.common.world.modification.BiomeSpawnsModifier;
import com.minecraftabnormals.abnormals_core.common.world.storage.GlobalStorage;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.DataProcessors;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedData;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedDataManager;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.annotations.Test;
import com.minecraftabnormals.abnormals_core.core.registry.LootInjectionRegistry;
import com.minecraftabnormals.abnormals_core.core.util.BiomeUtil;
import com.minecraftabnormals.abnormals_core.core.util.DataUtil;
import com.minecraftabnormals.abnormals_core.core.util.registry.RegistryHelper;
import com.mojang.datafixers.util.Pair;
import common.world.TestGlobalStorage;
import core.registry.TestBiomes;
import core.registry.TestEntities;
import core.registry.TestFeatures;
import core.registry.TestItems;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.data.worldgen.Features;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Test
@Mod(ACTest.MOD_ID)
@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ACTest {
	public static final String MOD_ID = "ac_test";
	public static final RegistryHelper REGISTRY_HELPER = RegistryHelper.create(MOD_ID, helper -> {
		helper.putSubHelper(ForgeRegistries.ITEMS, new TestItems.Helper(helper));
	});
	public static final TestGlobalStorage TEST_GLOBAL_STORAGE = GlobalStorage.createStorage(new ResourceLocation(MOD_ID, "test_storage"), new TestGlobalStorage());
	public static final TrackedData<Boolean> TEST_TRACKED_DATA = TrackedData.Builder.create(DataProcessors.BOOLEAN, () -> false).enableSaving().enablePersistence().build();

	public ACTest() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(EventPriority.LOWEST, this::commonSetup);

		REGISTRY_HELPER.register(modEventBus);
		TestFeatures.FEATURES.register(modEventBus);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			modEventBus.addListener(this::clientSetup);
			modEventBus.addListener(this::rendererSetup);
		});
		TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "tracked_data"), TEST_TRACKED_DATA);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			BiomeUtil.addEdgeBiome(Biomes.PLAINS, (noise, north, west, south, east) -> {
				if ((!Biomes.PLAINS.equals(north) && !BiomeUtil.isOceanBiome(north)) || (!Biomes.PLAINS.equals(west) && !BiomeUtil.isOceanBiome(west)) || (!Biomes.PLAINS.equals(south) && !BiomeUtil.isOceanBiome(south)) || (!Biomes.PLAINS.equals(east) && !BiomeUtil.isOceanBiome(east))) {
					return Biomes.SOUL_SAND_VALLEY;
				}
				return null;
			}, BiomeUtil.Priority.NORMAL);
			BiomeUtil.addEdgeBiome(Biomes.PLAINS, (noise, north, west, south, east) -> {
				if (!Biomes.PLAINS.equals(north) || !Biomes.PLAINS.equals(west) || !Biomes.PLAINS.equals(south) || !Biomes.PLAINS.equals(east)) {
					return Biomes.ICE_SPIKES;
				}
				return null;
			}, BiomeUtil.Priority.LOW);
			BiomeUtil.addOceanBiome(BiomeUtil.OceanType.WARM, TestBiomes.TEST_OCEAN.getKey(), Biomes.DEEP_WARM_OCEAN, 20);
			BiomeUtil.addHillBiome(Biomes.PLAINS, Pair.of(Biomes.WARPED_FOREST, 1), Pair.of(Biomes.CRIMSON_FOREST, 3));
			BiomeUtil.addNetherBiome(new Biome.ClimateParameters(0.0F, 0.1F, 0.0F, 0.0F, 0.25F), TestBiomes.TEST_NETHER.getKey());
			SpawnPlacements.register(TestEntities.COW.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Cow::checkAnimalSpawnRules);
		});

		DataUtil.registerNoteBlockInstrument(new DataUtil.CustomNoteBlockInstrument(AbnormalsCore.MODID, source -> source.getBlockState().getMaterial() == Material.HEAVY_METAL, SoundEvents.BELL_BLOCK));
		DataUtil.registerNoteBlockInstrument(new DataUtil.CustomNoteBlockInstrument(ACTest.MOD_ID, source -> source.getBlockState().is(Blocks.LODESTONE), SoundEvents.SHIELD_BREAK, (id1, id2) -> id2.equals("abnormals_core") ? -1 : 0));

		BiomeModificationManager instance = BiomeModificationManager.INSTANCE;
		instance.addModifier(BiomeFeatureModifier.createFeatureAdder(BiomeModificationPredicates.forBiomeKey(Biomes.PLAINS), GenerationStep.Decoration.VEGETAL_DECORATION, () -> Features.BIRCH.decorated(FeatureDecorator.DARK_OAK_TREE.configured(DecoratorConfiguration.NONE))));
		instance.addModifier(BiomeSpawnsModifier.createSpawnAdder(BiomeModificationPredicates.forBiomeKey(Biomes.SAVANNA), MobCategory.CREATURE, TestEntities.COW::get, 12, 10, 20));
		instance.addModifier(BiomeFeatureModifier.createFeatureReplacer(BiomeModificationPredicates.forBiomeKey(Biomes.END_HIGHLANDS), Sets.newHashSet(GenerationStep.Decoration.SURFACE_STRUCTURES), () -> Feature.END_GATEWAY, () -> Features.END_ISLAND));
		instance.addModifier(BiomeFeatureModifier.createFeatureRemover(BiomeModificationPredicates.forBiomeKey(Biomes.SMALL_END_ISLANDS), Sets.newHashSet(GenerationStep.Decoration.RAW_GENERATION), () -> Feature.END_ISLAND));
		instance.addModifier(BiomeFeatureModifier.createFeatureAdder(BiomeModificationPredicates.forBiomeKey(Biomes.ICE_SPIKES), GenerationStep.Decoration.UNDERGROUND_DECORATION, () -> TestFeatures.TEST_SPLINE.get().configured(FeatureConfiguration.NONE).decorated(Features.Decorators.HEIGHTMAP_SQUARE).rarity(3)));

		BiomeUtil.addEndBiome(Biomes.ICE_SPIKES, 7);
		this.registerLootInjectors();
	}

	@OnlyIn(Dist.CLIENT)
	private void clientSetup(FMLClientSetupEvent event) {
		BiomeUtil.markEndBiomeCustomMusic(new ResourceLocation("ice_spikes"));
	}

	@OnlyIn(Dist.CLIENT)
	private void rendererSetup(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(TestEntities.COW.get(), CowRenderer::new);
		event.registerEntityRenderer(TestEntities.ENDIMATED_TEST.get(), TestEndimatedEntityRenderer::new);
	}

	private void registerLootInjectors() {
		LootInjectionRegistry.LootInjector injector = new LootInjectionRegistry.LootInjector(MOD_ID);
		injector.addLootInjection(injector.buildLootPool("test", 1, 0), BuiltInLootTables.NETHER_BRIDGE, BuiltInLootTables.JUNGLE_TEMPLE);
	}
}
