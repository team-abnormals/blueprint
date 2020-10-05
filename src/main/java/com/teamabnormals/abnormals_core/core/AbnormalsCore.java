package com.teamabnormals.abnormals_core.core;

import com.teamabnormals.abnormals_core.core.api.banner.BannerManager;
import com.teamabnormals.abnormals_core.core.registry.ACEntities;
import com.teamabnormals.abnormals_core.core.registry.ACTileEntities;
import com.teamabnormals.abnormals_core.core.util.registry.RegistryHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.teamabnormals.abnormals_core.client.renderer.AbnormalsBoatRenderer;
import com.teamabnormals.abnormals_core.client.tile.*;
import com.teamabnormals.abnormals_core.common.blocks.AbnormalsBeehiveBlock;
import com.teamabnormals.abnormals_core.common.capability.chunkloading.*;
import com.teamabnormals.abnormals_core.common.network.*;
import com.teamabnormals.abnormals_core.common.network.entity.*;
import com.teamabnormals.abnormals_core.common.network.particle.*;
import com.teamabnormals.abnormals_core.core.config.ACConfig;
import com.teamabnormals.abnormals_core.core.api.IAddToBiomes;
import com.teamabnormals.abnormals_core.core.api.conditions.*;
import com.teamabnormals.abnormals_core.core.endimator.EndimationDataManager;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

//TODO: update package to com.minecraftabnormals.abnormals_core
@SuppressWarnings("deprecation")
@Mod(AbnormalsCore.MODID)
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class AbnormalsCore {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "abnormals_core";
	public static final String NETWORK_PROTOCOL = "AC1";
	public static final EndimationDataManager ENDIMATION_DATA_MANAGER = new EndimationDataManager();
	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper.Builder(MODID).build();
	
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "net"))
		.networkProtocolVersion(() -> NETWORK_PROTOCOL)
		.clientAcceptedVersions(NETWORK_PROTOCOL::equals)
		.serverAcceptedVersions(NETWORK_PROTOCOL::equals)
		.simpleChannel();

	public AbnormalsCore() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ChunkLoaderEvents());
		
		this.setupMessages();
		
		CraftingHelper.register(new QuarkFlagRecipeCondition.Serializer());
		CraftingHelper.register(new ACAndRecipeCondition.Serializer());
		BannerManager.RECIPE_SERIALIZERS.register(modEventBus);

		REGISTRY_HELPER.getEntitySubHelper().register(modEventBus);
		REGISTRY_HELPER.getTileEntitySubHelper().register(modEventBus);
		
		modEventBus.addListener((ModConfig.ModConfigEvent event) -> {
			final ModConfig config = event.getConfig();
			if (config.getSpec() == ACConfig.COMMON_SPEC) {
				ACConfig.ValuesHolder.updateCommonValuesFromConfig(config);
			}
		});

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(ENDIMATION_DATA_MANAGER);
			modEventBus.addListener(this::clientSetup);
		});
		
		modEventBus.addListener(EventPriority.LOWEST, this::commonSetup);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ACConfig.COMMON_SPEC);
	}
    
	private void commonSetup(final FMLCommonSetupEvent event) {
		DeferredWorkQueue.runLater(() -> {
			ForgeRegistries.FEATURES.getValues().stream().filter(feature -> feature instanceof IAddToBiomes).forEach((feature) -> {
				ForgeRegistries.BIOMES.forEach(((IAddToBiomes) feature).processBiomeAddition());
			});
			this.replaceBeehivePOI();
		});
		ChunkLoaderCapability.register();
	}
    
	@OnlyIn(Dist.CLIENT)
	private void clientSetup(final FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(ACEntities.BOAT.get(), AbnormalsBoatRenderer::new);
		
		ClientRegistry.bindTileEntityRenderer(ACTileEntities.CHEST.get(), AbnormalsChestTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ACTileEntities.TRAPPED_CHEST.get(), AbnormalsChestTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ACTileEntities.SIGN.get(), AbnormalsSignTileEntityRenderer::new);
	}
	
	private void setupMessages() {
		int id = -1;
		
		CHANNEL.messageBuilder(MessageS2CEndimation.class, id++)
		.encoder(MessageS2CEndimation::serialize).decoder(MessageS2CEndimation::deserialize)
		.consumer(MessageS2CEndimation::handle)
		.add();
		
		CHANNEL.messageBuilder(MessageSOpenSignEditor.class, id++)
		.encoder(MessageSOpenSignEditor::serialize).decoder(MessageSOpenSignEditor::deserialize)
		.consumer(MessageSOpenSignEditor::handle)
		.add();
		
		CHANNEL.messageBuilder(MessageC2SEditSign.class, id++)
		.encoder(MessageC2SEditSign::serialize).decoder(MessageC2SEditSign::deserialize)
		.consumer(MessageC2SEditSign::handle)
		.add();
		
		CHANNEL.messageBuilder(MessageS2CUpdateSign.class, id++)
		.encoder(MessageS2CUpdateSign::serialize).decoder(MessageS2CUpdateSign::deserialize)
		.consumer(MessageS2CUpdateSign::handle)
		.add();
		
		CHANNEL.messageBuilder(MessageS2CTeleportEntity.class, id++)
		.encoder(MessageS2CTeleportEntity::serialize).decoder(MessageS2CTeleportEntity::deserialize)
		.consumer(MessageS2CTeleportEntity::handle)
		.add();
		
		CHANNEL.messageBuilder(MessageS2CSpawnParticle.class, id++)
		.encoder(MessageS2CSpawnParticle::serialize).decoder(MessageS2CSpawnParticle::deserialize)
		.consumer(MessageS2CSpawnParticle::handle)
		.add();
		
		CHANNEL.messageBuilder(MessageC2S2CSpawnParticle.class, id++)
		.encoder(MessageC2S2CSpawnParticle::serialize).decoder(MessageC2S2CSpawnParticle::deserialize)
		.consumer(MessageC2S2CSpawnParticle::handle)
		.add();

		CHANNEL.messageBuilder(MessageS2CServerRedirect.class, id++)
		.encoder(MessageS2CServerRedirect::serialize).decoder(MessageS2CServerRedirect::deserialize)
		.consumer(MessageS2CServerRedirect::handle)
		.add();

		CHANNEL.messageBuilder(MessageS2CUpdateEntityData.class, id++)
		.encoder(MessageS2CUpdateEntityData::serialize).decoder(MessageS2CUpdateEntityData::deserialize)
		.consumer(MessageS2CUpdateEntityData::handle)
		.add();
	}

	private void replaceBeehivePOI() {
		ImmutableList<Block> BEEHIVES = ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block instanceof AbnormalsBeehiveBlock).collect(ImmutableList.toImmutableList());
		PointOfInterestType.BEEHIVE.blockStates = Sets.newHashSet(PointOfInterestType.BEEHIVE.blockStates);
		BEEHIVES.stream().forEach((block) -> block.getStateContainer().getValidStates().forEach(state -> {
			PointOfInterestType.POIT_BY_BLOCKSTATE.put(state, PointOfInterestType.BEEHIVE);
			PointOfInterestType.BEEHIVE.blockStates.add(state);
		}));
	}
}