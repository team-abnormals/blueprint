package core;

import client.TestEndimatedEntityRenderer;
import com.google.common.collect.Sets;
import com.minecraftabnormals.abnormals_core.common.world.modification.*;
import com.minecraftabnormals.abnormals_core.common.world.storage.GlobalStorage;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.DataProcessors;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedData;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedDataManager;
import com.minecraftabnormals.abnormals_core.core.annotations.Test;
import com.minecraftabnormals.abnormals_core.core.api.banner.BannerManager;
import com.minecraftabnormals.abnormals_core.core.registry.LootInjectionRegistry;
import com.minecraftabnormals.abnormals_core.core.util.registry.RegistryHelper;
import common.world.TestGlobalStorage;
import core.registry.TestEntities;
import core.registry.TestItems;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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
	public static final BannerPattern TEST_BANNER_PATTERN = BannerManager.createPattern("mca", "test", "tst");
	public static final TrackedData<Boolean> TEST_TRACKED_DATA = TrackedData.Builder.create(DataProcessors.BOOLEAN, () -> false).enableSaving().enablePersistence().build();

	public ACTest() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(EventPriority.LOWEST, this::commonSetup);

		REGISTRY_HELPER.register(modEventBus);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			modEventBus.addListener(this::clientSetup);
		});
		TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "tracked_data"), TEST_TRACKED_DATA);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			GlobalEntityTypeAttributes.put(TestEntities.ENDIMATED_TEST.get(), CreatureEntity.func_233666_p_().create());
			GlobalEntityTypeAttributes.put(TestEntities.COW.get(), CowEntity.func_234188_eI_().create());
			EntitySpawnPlacementRegistry.register(TestEntities.COW.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, CowEntity::canAnimalSpawn);
		});

		BiomeModificationManager instance = BiomeModificationManager.INSTANCE;
		instance.addModifier(BiomeFeatureModifier.createFeatureAdder(BiomeModificationPredicates.forBiomeKey(Biomes.PLAINS), GenerationStage.Decoration.VEGETAL_DECORATION, () -> Features.BIRCH.withPlacement(Placement.DARK_OAK_TREE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG))));
		instance.addModifier(BiomeSpawnsModifier.createSpawnAdder(BiomeModificationPredicates.forBiomeKey(Biomes.SAVANNA), EntityClassification.CREATURE, TestEntities.COW::get, 12, 10, 20));
		instance.addModifier(BiomeFeatureModifier.createFeatureReplacer(BiomeModificationPredicates.forBiomeKey(Biomes.END_HIGHLANDS), Sets.newHashSet(GenerationStage.Decoration.SURFACE_STRUCTURES), () -> Feature.END_GATEWAY, () -> Features.END_ISLAND));
		instance.addModifier(BiomeFeatureModifier.createFeatureRemover(BiomeModificationPredicates.forBiomeKey(Biomes.SMALL_END_ISLANDS), Sets.newHashSet(GenerationStage.Decoration.RAW_GENERATION), () -> Feature.END_ISLAND));

		BannerManager.addPattern(BannerManager.TEST, Items.CREEPER_SPAWN_EGG);
		this.registerLootInjectors();
	}

	@OnlyIn(Dist.CLIENT)
	private void clientSetup(final FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(TestEntities.COW.get(), CowRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(TestEntities.ENDIMATED_TEST.get(), TestEndimatedEntityRenderer::new);
	}

	private void registerLootInjectors() {
		LootInjectionRegistry.LootInjector injector = new LootInjectionRegistry.LootInjector(MOD_ID);
		injector.addLootInjection(injector.buildLootPool("test", 1, 0), LootTables.CHESTS_NETHER_BRIDGE, LootTables.CHESTS_JUNGLE_TEMPLE);
	}
}
