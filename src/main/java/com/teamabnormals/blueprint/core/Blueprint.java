package com.teamabnormals.blueprint.core;

import com.google.common.collect.Sets;
import com.teamabnormals.blueprint.client.BlueprintShaders;
import com.teamabnormals.blueprint.client.RewardHandler;
import com.teamabnormals.blueprint.client.renderer.BlueprintBoatRenderer;
import com.teamabnormals.blueprint.client.renderer.block.BlueprintChestBlockEntityRenderer;
import com.teamabnormals.blueprint.common.block.BlueprintBeehiveBlock;
import com.teamabnormals.blueprint.common.capability.chunkloading.ChunkLoaderCapability;
import com.teamabnormals.blueprint.common.capability.chunkloading.ChunkLoaderEvents;
import com.teamabnormals.blueprint.common.network.MessageC2SUpdateSlabfishHat;
import com.teamabnormals.blueprint.common.network.entity.MessageS2CEndimation;
import com.teamabnormals.blueprint.common.network.entity.MessageS2CTeleportEntity;
import com.teamabnormals.blueprint.common.network.entity.MessageS2CUpdateEntityData;
import com.teamabnormals.blueprint.common.network.particle.MessageS2CSpawnParticle;
import com.teamabnormals.blueprint.common.world.storage.tracking.DataProcessors;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.blueprint.core.api.SignManager;
import com.teamabnormals.blueprint.core.api.conditions.BlueprintAndCondition;
import com.teamabnormals.blueprint.core.api.conditions.QuarkFlagRecipeCondition.Serializer;
import com.teamabnormals.blueprint.core.api.conditions.config.*;
import com.teamabnormals.blueprint.core.api.model.FullbrightModel;
import com.teamabnormals.blueprint.core.data.server.tags.BlueprintBlockTagsProvider;
import com.teamabnormals.blueprint.core.data.server.tags.BlueprintEntityTypeTagsProvider;
import com.teamabnormals.blueprint.core.data.server.tags.BlueprintItemTagsProvider;
import com.teamabnormals.blueprint.core.endimator.EndimationLoader;
import com.teamabnormals.blueprint.core.other.BlueprintEvents;
import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;
import com.teamabnormals.blueprint.core.registry.BlueprintEntityTypes;
import com.teamabnormals.blueprint.core.registry.BlueprintLootConditions;
import com.teamabnormals.blueprint.core.util.DataUtil;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import com.teamabnormals.blueprint.core.util.registry.BlockEntitySubRegistryHelper;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Mod class for the Blueprint mod.
 *
 * @author SmellyModder (Luke Tonon)
 * @author bageldotjpg
 * @author Jackson
 * @author abigailfails
 */
@Mod(Blueprint.MOD_ID)
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Blueprint {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "blueprint";
	public static final String NETWORK_PROTOCOL = "BP1";
	public static final EndimationLoader ENDIMATION_LOADER = new EndimationLoader();
	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);
	public static final TrackedData<Byte> SLABFISH_SETTINGS = TrackedData.Builder.create(DataProcessors.BYTE, () -> (byte) 8).enablePersistence().build();

	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MOD_ID, "net"))
			.networkProtocolVersion(() -> NETWORK_PROTOCOL)
			.clientAcceptedVersions(NETWORK_PROTOCOL::equals)
			.serverAcceptedVersions(NETWORK_PROTOCOL::equals)
			.simpleChannel();

	public Blueprint() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext context = ModLoadingContext.get();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ChunkLoaderEvents());

		this.registerMessages();

		CraftingHelper.register(new Serializer());
		CraftingHelper.register(new BlueprintAndCondition.Serializer());
		DataUtil.registerConfigCondition(Blueprint.MOD_ID, BlueprintConfig.CLIENT, BlueprintConfig.CLIENT.slabfishSettings);
		DataUtil.registerConfigPredicate(new EqualsPredicate.Serializer());
		DataUtil.registerConfigPredicate(new GreaterThanOrEqualPredicate.Serializer());
		DataUtil.registerConfigPredicate(new GreaterThanPredicate.Serializer());
		DataUtil.registerConfigPredicate(new LessThanOrEqualPredicate.Serializer());
		DataUtil.registerConfigPredicate(new LessThanPredicate.Serializer());
		DataUtil.registerConfigPredicate(new ContainsPredicate.Serializer());
		DataUtil.registerConfigPredicate(new MatchesPredicate.Serializer());

		REGISTRY_HELPER.getEntitySubHelper().register(bus);
		REGISTRY_HELPER.getBlockEntitySubHelper().register(bus);

		bus.addListener((ModConfigEvent event) -> {
			final ModConfig config = event.getConfig();
			if (config.getSpec() == BlueprintConfig.CLIENT_SPEC) {
				BlueprintConfig.CLIENT.load();
			}
		});

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			bus.addListener(EventPriority.NORMAL, false, ColorHandlerEvent.Block.class, event -> {
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
			bus.addListener(this::modelSetup);
			bus.addListener(this::rendererSetup);
			bus.addListener(RewardHandler::clientSetup);
			bus.addListener(RewardHandler::addLayers);
			bus.addListener(BlueprintShaders::registerShaders);
		});

		bus.addListener(EventPriority.LOWEST, this::commonSetup);
		bus.addListener(EventPriority.LOWEST, this::postLoadingSetup);
		bus.addListener(this::dataSetup);
		bus.addListener(this::registerCapabilities);
		context.registerConfig(ModConfig.Type.CLIENT, BlueprintConfig.CLIENT_SPEC);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			this.replaceBeehivePOI();
			BlueprintLootConditions.registerLootConditions();
		});
		TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "slabfish_head"), SLABFISH_SETTINGS);
	}

	private void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(SignManager::setupAtlas);
	}

	private void dataSetup(GatherDataEvent event) {
		DataGenerator dataGenerator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		if (event.includeServer()) {
			BlueprintBlockTagsProvider blockTagGen = new BlueprintBlockTagsProvider(MOD_ID, dataGenerator, existingFileHelper);
			dataGenerator.addProvider(blockTagGen);
			dataGenerator.addProvider(new BlueprintItemTagsProvider(MOD_ID, dataGenerator, blockTagGen, existingFileHelper));
			dataGenerator.addProvider(new BlueprintEntityTypeTagsProvider(MOD_ID, dataGenerator, existingFileHelper));
		}
	}

	private void rendererSetup(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(BlueprintEntityTypes.BOAT.get(), BlueprintBoatRenderer::new);

		event.registerBlockEntityRenderer(BlueprintBlockEntityTypes.CHEST.get(), BlueprintChestBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(BlueprintBlockEntityTypes.TRAPPED_CHEST.get(), BlueprintChestBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(BlueprintBlockEntityTypes.SIGN.get(), SignRenderer::new);
	}

	private void postLoadingSetup(FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			DataUtil.getSortedAlternativeDispenseBehaviors().forEach(DataUtil.AlternativeDispenseBehavior::register);
			BlueprintEvents.SORTED_CUSTOM_NOTE_BLOCK_INSTRUMENTS = DataUtil.getSortedCustomNoteBlockInstruments();
		});
	}

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		ChunkLoaderCapability.register(event);
	}

	private void modelSetup(ModelRegistryEvent event) {
		ModelLoaderRegistry.registerLoader(new ResourceLocation(MOD_ID, "fullbright"), FullbrightModel.Loader.INSTANCE);
	}

	private void registerMessages() {
		int id = -1;
		CHANNEL.registerMessage(++id, MessageS2CEndimation.class, MessageS2CEndimation::serialize, MessageS2CEndimation::deserialize, MessageS2CEndimation::handle);
		CHANNEL.registerMessage(++id, MessageS2CTeleportEntity.class, MessageS2CTeleportEntity::serialize, MessageS2CTeleportEntity::deserialize, MessageS2CTeleportEntity::handle);
		CHANNEL.registerMessage(++id, MessageS2CSpawnParticle.class, MessageS2CSpawnParticle::serialize, MessageS2CSpawnParticle::deserialize, MessageS2CSpawnParticle::handle);
		CHANNEL.registerMessage(++id, MessageS2CUpdateEntityData.class, MessageS2CUpdateEntityData::serialize, MessageS2CUpdateEntityData::deserialize, MessageS2CUpdateEntityData::handle);
		CHANNEL.registerMessage(++id, MessageC2SUpdateSlabfishHat.class, MessageC2SUpdateSlabfishHat::serialize, MessageC2SUpdateSlabfishHat::deserialize, MessageC2SUpdateSlabfishHat::handle);
	}

	private void replaceBeehivePOI() {
		PoiType.BEEHIVE.matchingStates = Sets.newHashSet(PoiType.BEEHIVE.matchingStates);
		Map<BlockState, PoiType> statePointOfInterestMap = ObfuscationReflectionHelper.getPrivateValue(PoiType.class, null, "f_27323_");
		if (statePointOfInterestMap != null) {
			for (Block block : BlockEntitySubRegistryHelper.collectBlocks(BlueprintBeehiveBlock.class)) {
				block.getStateDefinition().getPossibleStates().forEach(state -> {
					statePointOfInterestMap.put(state, PoiType.BEEHIVE);
					PoiType.BEEHIVE.matchingStates.add(state);
				});
			}
		}
	}
}