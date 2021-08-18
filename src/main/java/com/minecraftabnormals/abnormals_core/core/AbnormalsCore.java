package com.minecraftabnormals.abnormals_core.core;

import com.google.common.collect.Sets;
import com.minecraftabnormals.abnormals_core.client.RewardHandler;
import com.minecraftabnormals.abnormals_core.client.renderer.AbnormalsBoatRenderer;
import com.minecraftabnormals.abnormals_core.client.tile.AbnormalsChestTileEntityRenderer;
import com.minecraftabnormals.abnormals_core.common.blocks.AbnormalsBeehiveBlock;
import com.minecraftabnormals.abnormals_core.common.capability.chunkloading.ChunkLoaderCapability;
import com.minecraftabnormals.abnormals_core.common.capability.chunkloading.ChunkLoaderEvents;
import com.minecraftabnormals.abnormals_core.common.network.MessageC2SUpdateSlabfishHat;
import com.minecraftabnormals.abnormals_core.common.network.MessageS2CServerRedirect;
import com.minecraftabnormals.abnormals_core.common.network.entity.MessageS2CEndimation;
import com.minecraftabnormals.abnormals_core.common.network.entity.MessageS2CTeleportEntity;
import com.minecraftabnormals.abnormals_core.common.network.entity.MessageS2CUpdateEntityData;
import com.minecraftabnormals.abnormals_core.common.network.particle.MessageS2CSpawnParticle;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.DataProcessors;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedData;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedDataManager;
import com.minecraftabnormals.abnormals_core.core.api.SignManager;
import com.minecraftabnormals.abnormals_core.core.api.conditions.ACAndRecipeCondition;
import com.minecraftabnormals.abnormals_core.core.api.conditions.QuarkFlagRecipeCondition;
import com.minecraftabnormals.abnormals_core.core.api.conditions.config.*;
import com.minecraftabnormals.abnormals_core.core.api.model.FullbrightModel;
import com.minecraftabnormals.abnormals_core.core.config.ACConfig;
import com.minecraftabnormals.abnormals_core.core.endimator.EndimationDataManager;
import com.minecraftabnormals.abnormals_core.core.events.listener.ACEvents;
import com.minecraftabnormals.abnormals_core.core.registry.ACEntities;
import com.minecraftabnormals.abnormals_core.core.registry.ACLootConditions;
import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;
import com.minecraftabnormals.abnormals_core.core.util.DataUtil;
import com.minecraftabnormals.abnormals_core.core.util.NetworkUtil;
import com.minecraftabnormals.abnormals_core.core.util.registry.RegistryHelper;
import com.minecraftabnormals.abnormals_core.core.util.registry.TileEntitySubRegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(AbnormalsCore.MODID)
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class AbnormalsCore {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "abnormals_core";
	public static final String NETWORK_PROTOCOL = "AC1";
	public static final EndimationDataManager ENDIMATION_DATA_MANAGER = new EndimationDataManager();
	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MODID);
	public static final TrackedData<Byte> SLABFISH_SETTINGS = TrackedData.Builder.create(DataProcessors.BYTE, () -> (byte) 8).enablePersistence().build();

	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "net"))
			.networkProtocolVersion(() -> NETWORK_PROTOCOL)
			.clientAcceptedVersions(NETWORK_PROTOCOL::equals)
			.serverAcceptedVersions(NETWORK_PROTOCOL::equals)
			.simpleChannel();

	public AbnormalsCore() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext context = ModLoadingContext.get();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ChunkLoaderEvents());

		this.setupMessages();

		CraftingHelper.register(new QuarkFlagRecipeCondition.Serializer());
		CraftingHelper.register(new ACAndRecipeCondition.Serializer());
		DataUtil.registerConfigCondition(AbnormalsCore.MODID, ACConfig.COMMON, ACConfig.CLIENT, ACConfig.CLIENT.slabfishSettings);
		DataUtil.registerConfigPredicate(new EqualsPredicate.Serializer());
		DataUtil.registerConfigPredicate(new GreaterThanOrEqualPredicate.Serializer());
		DataUtil.registerConfigPredicate(new GreaterThanPredicate.Serializer());
		DataUtil.registerConfigPredicate(new LessThanOrEqualPredicate.Serializer());
		DataUtil.registerConfigPredicate(new LessThanPredicate.Serializer());
		DataUtil.registerConfigPredicate(new ContainsPredicate.Serializer());
		DataUtil.registerConfigPredicate(new MatchesPredicate.Serializer());

		REGISTRY_HELPER.getEntitySubHelper().register(modEventBus);
		REGISTRY_HELPER.getTileEntitySubHelper().register(modEventBus);

		modEventBus.addListener((ModConfig.ModConfigEvent event) -> {
			final ModConfig config = event.getConfig();
			if (config.getSpec() == ACConfig.COMMON_SPEC) {
				ACConfig.ValuesHolder.updateCommonValuesFromConfig(config);
			} else if (config.getSpec() == ACConfig.CLIENT_SPEC) {
				ACConfig.ValuesHolder.updateClientValuesFromConfig(config);
			}
		});

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			modEventBus.addListener(EventPriority.NORMAL, false, ColorHandlerEvent.Block.class, event -> {
				IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
				if (resourceManager instanceof IReloadableResourceManager) {
					((IReloadableResourceManager) resourceManager).registerReloadListener(ENDIMATION_DATA_MANAGER);
				}
			});
			modEventBus.addListener(EventPriority.NORMAL, false, ModConfig.Reloading.class, event -> {
				if (event.getConfig().getModId().equals(AbnormalsCore.MODID)) NetworkUtil.updateSlabfish(RewardHandler.SlabfishSetting.getConfig());
			});
			modEventBus.addListener(this::clientSetup);
			modEventBus.addListener(this::modelSetup);
			modEventBus.addListener(RewardHandler::clientSetup);
		});

		modEventBus.addListener(EventPriority.LOWEST, this::commonSetup);
		modEventBus.addListener(EventPriority.LOWEST, this::postLoadingSetup);
		context.registerConfig(ModConfig.Type.COMMON, ACConfig.COMMON_SPEC);
		context.registerConfig(ModConfig.Type.CLIENT, ACConfig.CLIENT_SPEC);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			this.replaceBeehivePOI();
			ACLootConditions.registerLootConditions();
		});
		ChunkLoaderCapability.register();
		TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MODID, "slabfish_head"), SLABFISH_SETTINGS);
	}

	private void clientSetup(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(ACEntities.BOAT.get(), AbnormalsBoatRenderer::new);

		ClientRegistry.bindTileEntityRenderer(ACTileEntities.CHEST.get(), AbnormalsChestTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ACTileEntities.TRAPPED_CHEST.get(), AbnormalsChestTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ACTileEntities.SIGN.get(), SignTileEntityRenderer::new);

		event.enqueueWork(SignManager::setupAtlas);
	}

	private void postLoadingSetup(FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			DataUtil.getSortedAlternativeDispenseBehaviors().forEach(DataUtil.AlternativeDispenseBehavior::register);
			ACEvents.SORTED_CUSTOM_NOTE_BLOCK_INSTRUMENTS.addAll(DataUtil.getSortedCustomNoteBlockInstruments());
		});
	}

	private void modelSetup(ModelRegistryEvent event) {
		ModelLoaderRegistry.registerLoader(new ResourceLocation(MODID, "fullbright"), FullbrightModel.Loader.INSTANCE);
	}

	private void setupMessages() {
		int id = -1;

		CHANNEL.registerMessage(id++, MessageS2CEndimation.class, MessageS2CEndimation::serialize, MessageS2CEndimation::deserialize, MessageS2CEndimation::handle);
		CHANNEL.registerMessage(id++, MessageS2CTeleportEntity.class, MessageS2CTeleportEntity::serialize, MessageS2CTeleportEntity::deserialize, MessageS2CTeleportEntity::handle);
		CHANNEL.registerMessage(id++, MessageS2CSpawnParticle.class, MessageS2CSpawnParticle::serialize, MessageS2CSpawnParticle::deserialize, MessageS2CSpawnParticle::handle);
		CHANNEL.registerMessage(id++, MessageS2CServerRedirect.class, MessageS2CServerRedirect::serialize, MessageS2CServerRedirect::deserialize, MessageS2CServerRedirect::handle);
		CHANNEL.registerMessage(id++, MessageS2CUpdateEntityData.class, MessageS2CUpdateEntityData::serialize, MessageS2CUpdateEntityData::deserialize, MessageS2CUpdateEntityData::handle);
		CHANNEL.registerMessage(id, MessageC2SUpdateSlabfishHat.class, MessageC2SUpdateSlabfishHat::serialize, MessageC2SUpdateSlabfishHat::deserialize, MessageC2SUpdateSlabfishHat::handle);
	}

	private void replaceBeehivePOI() {
		PointOfInterestType.BEEHIVE.matchingStates = Sets.newHashSet(PointOfInterestType.BEEHIVE.matchingStates);
		Map<BlockState, PointOfInterestType> statePointOfInterestMap = ObfuscationReflectionHelper.getPrivateValue(PointOfInterestType.class, null, "field_221073_u");
		if (statePointOfInterestMap != null) {
			for (Block block : TileEntitySubRegistryHelper.collectBlocks(AbnormalsBeehiveBlock.class)) {
				block.getStateDefinition().getPossibleStates().forEach(state -> {
					statePointOfInterestMap.put(state, PointOfInterestType.BEEHIVE);
					PointOfInterestType.BEEHIVE.matchingStates.add(state);
				});
			}
		}
	}
}