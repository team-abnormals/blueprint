package com.teamabnormals.abnormals_core.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.teamabnormals.abnormals_core.client.renderer.AbnormalsBoatRenderer;
import com.teamabnormals.abnormals_core.client.tile.*;
import com.teamabnormals.abnormals_core.common.blocks.AbnormalsBeehiveBlock;
import com.teamabnormals.abnormals_core.common.capability.chunkloading.*;
import com.teamabnormals.abnormals_core.common.network.*;
import com.teamabnormals.abnormals_core.common.network.entity.*;
import com.teamabnormals.abnormals_core.common.network.particle.*;
import com.teamabnormals.abnormals_core.core.config.ACConfig;
import com.teamabnormals.abnormals_core.core.examples.ExampleEntityRegistry;
import com.teamabnormals.abnormals_core.core.examples.ExampleTileEntityRegistry;
import com.teamabnormals.abnormals_core.core.library.Test;
import com.teamabnormals.abnormals_core.core.library.api.IAddToBiomes;
import com.teamabnormals.abnormals_core.core.library.api.conditions.*;
import com.teamabnormals.abnormals_core.core.library.endimator.EndimationDataManager;
import com.teamabnormals.abnormals_core.core.registry.LootInjectionRegistry.LootInjector;
import com.teamabnormals.abnormals_core.core.utils.RegistryHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
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

@SuppressWarnings("deprecation")
@Mod(AbnormalsCore.MODID)
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AbnormalsCore {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "abnormals_core";
	public static final String NETWORK_PROTOCOL = "AC1";
	public static final EndimationDataManager ENDIMATION_DATA_MANAGER = new EndimationDataManager();
	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MODID);
	
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
		LootConditionManager.registerCondition(new ModLoadedLootCondition.Serializer());
		LootConditionManager.registerCondition(new QuarkFlagLootCondition.Serializer());
		
//		REGISTRY_HELPER.getDeferredItemRegister().register(modEventBus);
//		REGISTRY_HELPER.getDeferredBlockRegister().register(modEventBus);
//		REGISTRY_HELPER.getDeferredSoundRegister().register(modEventBus);
		REGISTRY_HELPER.getDeferredEntityRegister().register(modEventBus);
		REGISTRY_HELPER.getDeferredTileEntityRegister().register(modEventBus);
		
		modEventBus.addListener((ModConfig.ModConfigEvent event) -> {
			final ModConfig config = event.getConfig();
			if(config.getSpec() == ACConfig.COMMON_SPEC) {
				ACConfig.ValuesHolder.updateCommonValuesFromConfig(config);
			}
		});
		
        modEventBus.addListener(this::replaceBeehivePOI);
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(ENDIMATION_DATA_MANAGER);
			modEventBus.addListener(this::clientSetup);
			modEventBus.addListener(EventPriority.LOWEST, this::registerItemColors);
		});
		
		modEventBus.addListener(EventPriority.LOWEST, this::commonSetup);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ACConfig.COMMON_SPEC);
	}
    
	private void commonSetup(final FMLCommonSetupEvent event) {
		DeferredWorkQueue.runLater(() -> {
//			REGISTRY_HELPER.processSpawnEggDispenseBehaviors();
			ForgeRegistries.FEATURES.getValues().stream().filter(feature -> feature instanceof IAddToBiomes).forEach((feature) -> {
				ForgeRegistries.BIOMES.forEach(((IAddToBiomes) feature).processBiomeAddition());
			});
		});
		ChunkLoaderCapability.register();
//		ExampleEntitySpawnHandler.processSpawnAdditions();
	}
    
	@OnlyIn(Dist.CLIENT)
	private void clientSetup(final FMLClientSetupEvent event) {
//		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityRegistry.COW.get(), CowRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityRegistry.EXAMPLE_ANIMATED.get(), ExampleEndimatedEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityRegistry.BOAT.get(), AbnormalsBoatRenderer::new);
		
		ClientRegistry.bindTileEntityRenderer(ExampleTileEntityRegistry.CHEST.get(), AbnormalsChestTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ExampleTileEntityRegistry.TRAPPED_CHEST.get(), AbnormalsChestTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ExampleTileEntityRegistry.SIGN.get(), AbnormalsSignTileEntityRenderer::new);
	}
	
	@Test
	private void registerLootInjectors() {
		LootInjector injector = new LootInjector(MODID);
		injector.registerLootInjection(injector.buildLootBool("test", 1, 0), LootTables.CHESTS_NETHER_BRIDGE, LootTables.CHESTS_JUNGLE_TEMPLE);
	}
	
	@OnlyIn(Dist.CLIENT)
	private void registerItemColors(ColorHandlerEvent.Item event) {
		REGISTRY_HELPER.processSpawnEggColors(event);
	}
	
    private void replaceBeehivePOI(final FMLCommonSetupEvent event) {
    	ImmutableList<Block> BEEHIVES = ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block instanceof AbnormalsBeehiveBlock).collect(ImmutableList.toImmutableList());
        PointOfInterestType.field_226356_s_.blockStates = BlockTags.BEEHIVES.getAllElements().stream().flatMap((map) -> map.getStateContainer().getValidStates().stream()).collect(ImmutableSet.toImmutableSet());
    	Map<BlockState, PointOfInterestType> pointOfInterestTypeMap = new HashMap<>();
        BEEHIVES.stream().forEach(block -> block.getStateContainer().getValidStates().forEach(state -> pointOfInterestTypeMap.put(state, PointOfInterestType.field_226356_s_)));
        PointOfInterestType.field_221073_u.putAll(pointOfInterestTypeMap);
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
	}
}