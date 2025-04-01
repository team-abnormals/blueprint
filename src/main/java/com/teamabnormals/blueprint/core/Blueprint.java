package com.teamabnormals.blueprint.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.teamabnormals.blueprint.client.BlueprintShaders;
import com.teamabnormals.blueprint.client.RewardHandler;
import com.teamabnormals.blueprint.client.renderer.BlueprintBoatRenderer;
import com.teamabnormals.blueprint.client.renderer.block.BlueprintChestBlockEntityRenderer;
import com.teamabnormals.blueprint.client.renderer.texture.atlas.BlueprintSpriteSources;
import com.teamabnormals.blueprint.client.screen.splash.BlueprintSplashManager;
import com.teamabnormals.blueprint.common.block.BlueprintChiseledBookShelfBlock;
import com.teamabnormals.blueprint.common.network.UpdateSlabfishHatPayload;
import com.teamabnormals.blueprint.common.network.entity.UpdateEndimationPayload;
import com.teamabnormals.blueprint.common.network.entity.TeleportEntityPayload;
import com.teamabnormals.blueprint.common.network.entity.UpdateEntityDataPayload;
import com.teamabnormals.blueprint.common.network.particle.SpawnParticlesPayload;
import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSource;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.blueprint.core.api.BlockSetTypeRegistryHelper;
import com.teamabnormals.blueprint.core.api.BlueprintTrims;
import com.teamabnormals.blueprint.core.api.WoodTypeRegistryHelper;
import com.teamabnormals.blueprint.core.api.conditions.config.*;
import com.teamabnormals.blueprint.core.data.server.BlueprintDataMapProvider;
import com.teamabnormals.blueprint.core.data.server.BlueprintDatapackBuiltinEntriesProvider;
import com.teamabnormals.blueprint.core.data.server.BlueprintRecipeProvider;
import com.teamabnormals.blueprint.core.data.server.tags.*;
import com.teamabnormals.blueprint.core.endimator.EndimationLoader;
import com.teamabnormals.blueprint.core.other.BlueprintEvents;
import com.teamabnormals.blueprint.core.registry.*;
import com.teamabnormals.blueprint.core.util.DataUtil;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import com.teamabnormals.blueprint.core.util.item.CreativeModeTabContentsPopulator;
import com.teamabnormals.blueprint.core.util.registry.BlockEntitySubRegistryHelper;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Mod class for the Blueprint mod.
 *
 * @author SmellyModder (Luke Tonon)
 * @author bageldotjpg
 * @author Jackson
 * @author abigailfails
 */
@Mod(Blueprint.MOD_ID)
public final class Blueprint {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "blueprint";
	public static final EndimationLoader ENDIMATION_LOADER = new EndimationLoader();
	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);
	public static final TrackedData<Byte> SLABFISH_SETTINGS = TrackedData.Builder.create(ByteBufCodecs.BYTE, () -> (byte) 8).enablePersistence().build();

	public Blueprint(IEventBus bus, ModContainer modContainer) {
		bus.addListener(this::registerPayloadHandlers);

		DataUtil.registerConfigPredicate(new EqualsPredicate.Serializer());
		DataUtil.registerConfigPredicate(new GreaterThanOrEqualPredicate.Serializer());
		DataUtil.registerConfigPredicate(new GreaterThanPredicate.Serializer());
		DataUtil.registerConfigPredicate(new LessThanOrEqualPredicate.Serializer());
		DataUtil.registerConfigPredicate(new LessThanPredicate.Serializer());
		DataUtil.registerConfigPredicate(new ContainsPredicate.Serializer());
		DataUtil.registerConfigPredicate(new MatchesPredicate.Serializer());

		REGISTRY_HELPER.getEntitySubHelper().register(bus);
		REGISTRY_HELPER.getBlockEntitySubHelper().register(bus);
		BlueprintPoiTypes.POI_TYPES.register(bus);
		BlueprintSurfaceRules.RULE_SOURCES.register(bus);
		BlueprintConditionCodecs.CONDITION_CODECS.register(bus);
		BlueprintLootConditions.LOOT_CONDITION_TYPES.register(bus);

		bus.addListener((ModConfigEvent event) -> {
			final ModConfig config = event.getConfig();
			if (config.getSpec() == BlueprintConfig.CLIENT_SPEC) {
				BlueprintConfig.CLIENT.load();
			}
		});

		if (FMLEnvironment.dist == Dist.CLIENT) {
			bus.addListener(EventPriority.NORMAL, false, RegisterColorHandlersEvent.Block.class, event -> {
				ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
				if (resourceManager instanceof ReloadableResourceManager) {
					((ReloadableResourceManager) resourceManager).registerReloadListener(ENDIMATION_LOADER);
				}
			});
			bus.addListener(EventPriority.NORMAL, false, ModConfigEvent.Reloading.class, event -> {
				if (event.getConfig().getModId().equals(Blueprint.MOD_ID))
					NetworkUtil.updateSlabfish(RewardHandler.SlabfishSetting.getConfig());
			});
			bus.addListener(this::clientSetup);
			bus.addListener(this::registerLayerDefinitions);
			bus.addListener(this::rendererSetup);
			bus.addListener(BlueprintTrims::onModelsBaked);
			bus.addListener(CreativeModeTabContentsPopulator::onBuildCreativeModeTabContents);
			bus.addListener(BlueprintSplashManager::onRegisterClientReloadListeners);
			bus.addListener(RewardHandler::clientSetup);
			bus.addListener(RewardHandler::addLayers);
			bus.addListener(BlueprintShaders::registerShaders);
			bus.addListener(BlueprintSpriteSources::register);
		}

		bus.addListener(BlueprintDataPackRegistries::registerRegistries);
		bus.addListener(this::registerOnEvent);
		bus.addListener(EventPriority.LOWEST, this::commonSetup);
		bus.addListener(EventPriority.LOWEST, this::postLoadingSetup);
		bus.addListener(this::dataSetup);
		modContainer.registerConfig(ModConfig.Type.CLIENT, BlueprintConfig.CLIENT_SPEC);
		modContainer.registerConfig(ModConfig.Type.COMMON, BlueprintConfig.COMMON_SPEC);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		TrackedDataManager.INSTANCE.registerData(ResourceLocation.fromNamespaceAndPath(MOD_ID, "slabfish_head"), SLABFISH_SETTINGS);

		Set<Block> validBlocks = Sets.newHashSet(BlockEntityType.CHISELED_BOOKSHELF.validBlocks);
		validBlocks.addAll(Sets.newHashSet(BlockEntitySubRegistryHelper.collectBlocks(BlueprintChiseledBookShelfBlock.class)));
		BlockEntityType.CHISELED_BOOKSHELF.validBlocks = ImmutableSet.copyOf(validBlocks);

		event.enqueueWork(() -> {
			WoodTypeRegistryHelper.registerWoodTypes();
			BlockSetTypeRegistryHelper.registerBlockSetTypes();
		});
	}

	private void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			WoodTypeRegistryHelper.setupAtlas();
			BlueprintTrims.init();
		});
	}

	private void dataSetup(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
		ExistingFileHelper helper = event.getExistingFileHelper();

		boolean server = event.includeServer();
		BlueprintBlockTagsProvider blockTags = new BlueprintBlockTagsProvider(MOD_ID, output, provider, helper);
		generator.addProvider(server, blockTags);
		generator.addProvider(server, new BlueprintItemTagsProvider(MOD_ID, output, provider, blockTags.contentsGetter(), helper));
		generator.addProvider(server, new BlueprintEntityTypeTagsProvider(MOD_ID, output, provider, helper));
		generator.addProvider(server, new BlueprintPoiTypeTagsProvider(MOD_ID, output, provider, helper));
		generator.addProvider(server, new BlueprintRecipeProvider(MOD_ID, output, provider));
		generator.addProvider(server, new BlueprintDatapackBuiltinEntriesProvider(output, provider));
		generator.addProvider(server, new BlueprintDataMapProvider(output, provider));
	}

	private void registerOnEvent(RegisterEvent event) {
		event.register(Registries.BIOME_SOURCE, (helper) -> {
			helper.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "modded"), ModdedBiomeSource.CODEC);
		});
	}

	@OnlyIn(Dist.CLIENT)
	private void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		BlueprintBoatTypes.registerLayerDefinitions(event);
	}

	@OnlyIn(Dist.CLIENT)
	private void rendererSetup(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(BlueprintEntityTypes.BOAT.get(), context -> new BlueprintBoatRenderer(context, false));
		event.registerEntityRenderer(BlueprintEntityTypes.CHEST_BOAT.get(), context -> new BlueprintBoatRenderer(context, true));
		event.registerEntityRenderer(BlueprintEntityTypes.FALLING_BLOCK.get(), FallingBlockRenderer::new);

		event.registerBlockEntityRenderer(BlueprintBlockEntityTypes.CHEST.get(), BlueprintChestBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(BlueprintBlockEntityTypes.TRAPPED_CHEST.get(), BlueprintChestBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(BlueprintBlockEntityTypes.SIGN.get(), SignRenderer::new);
		event.registerBlockEntityRenderer(BlueprintBlockEntityTypes.HANGING_SIGN.get(), HangingSignRenderer::new);
	}

	private void postLoadingSetup(FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			DataUtil.getSortedAlternativeDispenseBehaviors().forEach(DataUtil.AlternativeDispenseBehavior::register);
			BlueprintEvents.SORTED_CUSTOM_NOTE_BLOCK_INSTRUMENTS = DataUtil.getSortedCustomNoteBlockInstruments();
		});
	}

	private void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");
		registrar.playToClient(UpdateEndimationPayload.TYPE, UpdateEndimationPayload.STREAM_CODEC, UpdateEndimationPayload::handle);
		registrar.playToClient(TeleportEntityPayload.TYPE, TeleportEntityPayload.STREAM_CODEC, TeleportEntityPayload::handle);
		registrar.playToClient(SpawnParticlesPayload.TYPE, SpawnParticlesPayload.STREAM_CODEC, SpawnParticlesPayload::handle);
		registrar.playToClient(UpdateEntityDataPayload.TYPE, UpdateEntityDataPayload.STREAM_CODEC, UpdateEntityDataPayload::handle);
		registrar.playToServer(UpdateSlabfishHatPayload.TYPE, UpdateSlabfishHatPayload.STREAM_CODEC, UpdateSlabfishHatPayload::handle);
	}
}