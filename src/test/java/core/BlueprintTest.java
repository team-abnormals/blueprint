package core;

import client.EndimatedWalkingEntityRenderer;
import client.TestClientEvents;
import client.TestCustomSplash;
import client.TestEndimatedBlockEntityRenderer;
import client.TestEndimatedEntityRenderer;
import com.teamabnormals.blueprint.client.screen.splash.SplashSerializers;
import com.teamabnormals.blueprint.common.world.storage.GlobalStorage;
import com.teamabnormals.blueprint.common.world.storage.tracking.DataProcessors;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.events.AnimateTickEvents;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import com.teamabnormals.blueprint.core.util.DataUtil;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import common.world.TestGlobalStorage;
import core.data.client.TestEndimationProvider;
import core.data.client.TestSplashProvider;
import core.data.server.*;
import core.registry.*;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(BlueprintTest.MOD_ID)
@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BlueprintTest {
	public static final String MOD_ID = "blueprint_test";
	public static final RegistryHelper REGISTRY_HELPER = RegistryHelper.create(MOD_ID, helper -> {
		helper.putSubHelper(ForgeRegistries.ITEMS, new TestItems.Helper(helper));
	});
	public static final TestGlobalStorage TEST_GLOBAL_STORAGE = GlobalStorage.createStorage(new ResourceLocation(MOD_ID, "test_storage"), new TestGlobalStorage());
	public static final TrackedData<Boolean> TEST_TRACKED_DATA = TrackedData.Builder.create(DataProcessors.BOOLEAN, () -> false).enableSaving().enablePersistence().build();

	public BlueprintTest() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(EventPriority.LOWEST, this::commonSetup);

		REGISTRY_HELPER.register(modEventBus);
		TestFeatures.FEATURES.register(modEventBus);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			SplashSerializers.register(new ResourceLocation(MOD_ID, "custom"), TestCustomSplash.CODEC);

			modEventBus.addListener(this::clientSetup);
			modEventBus.addListener(this::rendererSetup);
			modEventBus.register(TestClientEvents.HUMANOID_ENDIMATORS);
		});
		modEventBus.addListener(this::dataSetup);
		TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "tracked_data"), TEST_TRACKED_DATA);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			SpawnPlacements.register(TestEntities.COW.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Cow::checkAnimalSpawnRules);
			DataUtil.concatArrays(ObfuscationReflectionHelper.findField(CreativeModeTab.class, "f_40769_"), CreativeModeTab.TAB_TOOLS, EnchantmentCategory.BOW);
		});
		DataUtil.registerNoteBlockInstrument(new DataUtil.CustomNoteBlockInstrument(Blueprint.MOD_ID, source -> source.getBlockState().getMaterial() == Material.HEAVY_METAL, SoundEvents.BELL_BLOCK));
		DataUtil.registerNoteBlockInstrument(new DataUtil.CustomNoteBlockInstrument(BlueprintTest.MOD_ID, source -> source.getBlockState().is(Blocks.LODESTONE), SoundEvents.SHIELD_BREAK, (id1, id2) -> id2.equals("blueprint") ? -1 : 0));
	}

	@OnlyIn(Dist.CLIENT)
	private void clientSetup(FMLClientSetupEvent event) {
		BiomeUtil.markEndBiomeCustomMusic(Biomes.ICE_SPIKES);
		AnimateTickEvents.BLOCK.registerListener(TestEvents::onAnimateTick);
		AnimateTickEvents.FLUID.registerListener(TestEvents::onFluidAnimateTick);
	}

	private void dataSetup(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();

		boolean includeServer = event.includeServer();
		generator.addProvider(includeServer, new TestAdvancementModifiersProvider(generator));
		generator.addProvider(includeServer, new TestLootModifiersProvider(generator));
		generator.addProvider(includeServer, new TestChunkGeneratorModifiersProvider(generator));
		generator.addProvider(includeServer, new TestModdedBiomeSlicesProvider(generator));
		generator.addProvider(includeServer, new TestStructureRepaletterProvider(generator));

		boolean includeClient = event.includeClient();
		generator.addProvider(includeClient, new TestEndimationProvider(generator));
		generator.addProvider(includeClient, new TestSplashProvider(generator));
	}

	@OnlyIn(Dist.CLIENT)
	private void rendererSetup(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(TestEntities.COW.get(), CowRenderer::new);
		event.registerEntityRenderer(TestEntities.ENDIMATED_TEST.get(), TestEndimatedEntityRenderer::new);
		event.registerEntityRenderer(TestEntities.ENDIMATED_WALKING.get(), EndimatedWalkingEntityRenderer::new);
		event.registerBlockEntityRenderer(TestBlockEntities.TEST_ENDIMATED.get(), TestEndimatedBlockEntityRenderer::new);
	}
}
