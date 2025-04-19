package core;

import client.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.client.screen.splash.SplashSerializers;
import com.teamabnormals.blueprint.common.world.storage.GlobalStorage;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.events.AnimateTickEvents;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import com.teamabnormals.blueprint.core.util.DataUtil;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import common.world.TestGlobalStorage;
import core.data.client.TestAssetsRemolderProvider;
import core.data.client.TestEndimationProvider;
import core.data.client.TestSplashProvider;
import core.data.client.TestSpriteSourceProvider;
import core.data.server.*;
import core.registry.*;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.placement.CavePlacements;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(BlueprintTest.MOD_ID)
public final class BlueprintTest {
	public static final String MOD_ID = "blueprint_test";
	public static final RegistryHelper REGISTRY_HELPER = RegistryHelper.create(MOD_ID, helper -> {
		helper.putSubHelper(Registries.ITEM, new TestItems.Helper(helper));
	});
	public static final TestGlobalStorage TEST_GLOBAL_STORAGE = GlobalStorage.createStorage(ResourceLocation.fromNamespaceAndPath(MOD_ID, "test_storage"), new TestGlobalStorage());
	public static final TrackedData<Boolean> TEST_TRACKED_DATA = TrackedData.Builder.create(ByteBufCodecs.BOOL, () -> false).enableSaving(Codec.BOOL.fieldOf("Boolean")).enablePersistence().build();

	public BlueprintTest(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.addListener(EventPriority.LOWEST, this::commonSetup);

		REGISTRY_HELPER.register(modEventBus);
		TestFeatures.FEATURES.register(modEventBus);
		TestItems.DECORATED_POT_PATTERNS.register(modEventBus);
		TestEndimations.register();
		TestRabbitVariants.register();

		if (FMLEnvironment.dist == Dist.CLIENT) {
			SplashSerializers.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "custom"), TestCustomSplash.CODEC);
			TestItems.setupTabEditors();

			modEventBus.addListener(this::clientSetup);
			modEventBus.addListener(this::rendererSetup);
			modEventBus.register(TestClientEvents.HUMANOID_ENDIMATORS);
		}
		modEventBus.addListener(this::dataSetup);
		TrackedDataManager.INSTANCE.registerData(ResourceLocation.fromNamespaceAndPath(MOD_ID, "tracked_data"), TEST_TRACKED_DATA);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			DataUtil.registerDecoratedPotPattern(TestItems.ITEM.get(), TestItems.TEST_POTTERY_SHERD);
		});
		DataUtil.registerNoteBlockInstrument(new DataUtil.CustomNoteBlockInstrument(Blueprint.MOD_ID, source -> source.state().is(BlockTags.IRON_ORES), SoundEvents.BELL_BLOCK));
		DataUtil.registerNoteBlockInstrument(new DataUtil.CustomNoteBlockInstrument(BlueprintTest.MOD_ID, source -> source.state().is(Blocks.LODESTONE), SoundEvents.SHIELD_BREAK, false, (id1, id2) -> id2.equals("blueprint") ? -1 : 0));
		DataUtil.registerNoteBlockInstrument(new DataUtil.CustomNoteBlockInstrument(BlueprintTest.MOD_ID, source -> source.state().is(Blocks.FLOWER_POT), SoundEvents.WOLF_AMBIENT, true));
		DataUtil.addToJigsawPattern(ResourceLocation.withDefaultNamespace("village/plains/decor"), registryAccess -> {
			return StructurePoolElement.feature(registryAccess.registryOrThrow(Registries.PLACED_FEATURE).getHolderOrThrow(CavePlacements.SCULK_PATCH_ANCIENT_CITY)).apply(StructureTemplatePool.Projection.RIGID);
		}, 100);
	}

	@OnlyIn(Dist.CLIENT)
	private void clientSetup(FMLClientSetupEvent event) {
		BiomeUtil.markEndBiomeCustomMusic(Biomes.ICE_SPIKES);
		AnimateTickEvents.BLOCK.registerListener(TestEvents::onAnimateTick);
		AnimateTickEvents.FLUID.registerListener(TestEvents::onFluidAnimateTick);
	}

	private void dataSetup(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper helper = event.getExistingFileHelper();

		boolean includeServer = event.includeServer();
		TestBlockTagsProvider testBlockTagsProvider = new TestBlockTagsProvider(packOutput, lookupProvider, helper);
		generator.addProvider(includeServer, testBlockTagsProvider);
		generator.addProvider(includeServer, new TestItemTagsProvider(packOutput, lookupProvider, testBlockTagsProvider.contentsGetter(), helper));
		generator.addProvider(includeServer, new TestAdvancementModifiersProvider(packOutput, lookupProvider));
		generator.addProvider(includeServer, new TestChunkGeneratorModifiersProvider(packOutput, lookupProvider));
		generator.addProvider(includeServer, new TestDatapackBuiltinEntriesProvider(packOutput, lookupProvider));
		generator.addProvider(includeServer, new TestDataRemolderProvider(packOutput, lookupProvider));

		boolean includeClient = event.includeClient();
		generator.addProvider(includeClient, new TestSpriteSourceProvider(packOutput, lookupProvider, helper));
		generator.addProvider(includeClient, new TestEndimationProvider(packOutput));
		generator.addProvider(includeClient, new TestSplashProvider(packOutput));
		generator.addProvider(includeClient, new TestAssetsRemolderProvider(packOutput, lookupProvider));
	}

	@OnlyIn(Dist.CLIENT)
	private void rendererSetup(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(TestEntities.COW.get(), CowRenderer::new);
		event.registerEntityRenderer(TestEntities.ENDIMATED_TEST.get(), TestEndimatedEntityRenderer::new);
		event.registerEntityRenderer(TestEntities.ENDIMATED_WALKING.get(), EndimatedWalkingEntityRenderer::new);
		event.registerBlockEntityRenderer(TestBlockEntities.TEST_ENDIMATED.get(), TestEndimatedBlockEntityRenderer::new);
	}

	public static ResourceLocation location(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
