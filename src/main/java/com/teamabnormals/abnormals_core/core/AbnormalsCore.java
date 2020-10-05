package com.teamabnormals.abnormals_core.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.teamabnormals.abnormals_core.client.renderer.AbnormalsBoatRenderer;
import com.teamabnormals.abnormals_core.client.tile.AbnormalsChestTileEntityRenderer;
import com.teamabnormals.abnormals_core.common.blocks.AbnormalsBeehiveBlock;
import com.teamabnormals.abnormals_core.common.capability.chunkloading.ChunkLoaderCapability;
import com.teamabnormals.abnormals_core.common.capability.chunkloading.ChunkLoaderEvents;
import com.teamabnormals.abnormals_core.common.network.MessageS2CServerRedirect;
import com.teamabnormals.abnormals_core.common.network.entity.MessageS2CEndimation;
import com.teamabnormals.abnormals_core.common.network.entity.MessageS2CTeleportEntity;
import com.teamabnormals.abnormals_core.common.network.entity.MessageS2CUpdateEntityData;
import com.teamabnormals.abnormals_core.common.network.particle.MessageC2S2CSpawnParticle;
import com.teamabnormals.abnormals_core.common.network.particle.MessageS2CSpawnParticle;
import com.teamabnormals.abnormals_core.common.world.biome.AbnormalsBiome;
import com.teamabnormals.abnormals_core.common.world.storage.tracking.DataProcessors;
import com.teamabnormals.abnormals_core.common.world.storage.tracking.TrackedData;
import com.teamabnormals.abnormals_core.core.config.ACConfig;
import com.teamabnormals.abnormals_core.core.examples.ExampleEntityRegistry;
import com.teamabnormals.abnormals_core.core.examples.ExampleTileEntityRegistry;
import com.teamabnormals.abnormals_core.core.library.Test;
import com.teamabnormals.abnormals_core.core.library.api.BannerManager;
import com.teamabnormals.abnormals_core.core.library.api.IAddToBiomes;
import com.teamabnormals.abnormals_core.core.library.api.conditions.ACAndRecipeCondition;
import com.teamabnormals.abnormals_core.core.library.api.conditions.QuarkFlagRecipeCondition;
import com.teamabnormals.abnormals_core.core.library.endimator.EndimationDataManager;
import com.teamabnormals.abnormals_core.core.registry.LootInjectionRegistry.LootInjector;
import com.teamabnormals.abnormals_core.core.utils.RegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.PointOfInterestType;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;

//TODO: update package to com.minecraftabnormals.abnormals_core
@SuppressWarnings("deprecation")
@Mod(AbnormalsCore.MODID)
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AbnormalsCore {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "abnormals_core";
	public static final String NETWORK_PROTOCOL = "AC1";
	public static final EndimationDataManager ENDIMATION_DATA_MANAGER = new EndimationDataManager();
	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MODID);
	public static final TrackedData<Boolean> TEST_TRACKED_DATA = TrackedData.Builder.create(DataProcessors.BOOLEAN, () -> false).enablePersistence().enableSaving().build();
	public static final TrackedData<CompoundNBT> TEST_TRACKED_DATA_NBT = TrackedData.Builder.create(DataProcessors.COMPOUND, CompoundNBT::new).enablePersistence().enableSaving().build();

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
		
//		REGISTRY_HELPER.getDeferredItemRegister().register(modEventBus);
//		REGISTRY_HELPER.getDeferredBlockRegister().register(modEventBus);
//		REGISTRY_HELPER.getDeferredSoundRegister().register(modEventBus);
		REGISTRY_HELPER.getDeferredEntityRegister().register(modEventBus);
		REGISTRY_HELPER.getDeferredTileEntityRegister().register(modEventBus);
		BannerManager.RECIPE_SERIALIZERS.register(modEventBus);
		
		modEventBus.addListener((ModConfig.ModConfigEvent event) -> {
			final ModConfig config = event.getConfig();
			if (config.getSpec() == ACConfig.COMMON_SPEC) {
				ACConfig.ValuesHolder.updateCommonValuesFromConfig(config);
			}
		});

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			modEventBus.addListener(this::clientSetup);
			modEventBus.addListener(EventPriority.LOWEST, this::registerItemColors);
		});
		
		modEventBus.addListener(EventPriority.LOWEST, this::commonSetup);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ACConfig.COMMON_SPEC);
	}
    
	private void commonSetup(final FMLCommonSetupEvent event) {
		DeferredWorkQueue.runLater(() -> {
//			BannerManager.addPattern(BannerManager.ABNORMALS, Items.CREEPER_SPAWN_EGG);
//			REGISTRY_HELPER.processSpawnEggDispenseBehaviors();
			ForgeRegistries.BIOMES.getValues().stream().filter(biome -> biome instanceof AbnormalsBiome).forEach((biome) -> {
				((AbnormalsBiome)biome).addFeatures();
				((AbnormalsBiome)biome).addSpawns();
			});
			ForgeRegistries.FEATURES.getValues().stream().filter(feature -> feature instanceof IAddToBiomes).forEach((feature) -> {
				ForgeRegistries.BIOMES.forEach(((IAddToBiomes) feature).processBiomeAddition());
			});
			this.replaceBeehivePOI();
		});
//		TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MODID, "test_tracked"), TEST_TRACKED_DATA);
		ChunkLoaderCapability.register();
//		ExampleEntitySpawnHandler.processSpawnAdditions();
	}
    
	@OnlyIn(Dist.CLIENT)
	private void clientSetup(final FMLClientSetupEvent event) {
		((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(ENDIMATION_DATA_MANAGER);

//		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityRegistry.COW.get(), CowRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityRegistry.EXAMPLE_ANIMATED.get(), ExampleEndimatedEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityRegistry.BOAT.get(), AbnormalsBoatRenderer::new);
		
		ClientRegistry.bindTileEntityRenderer(ExampleTileEntityRegistry.CHEST.get(), AbnormalsChestTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ExampleTileEntityRegistry.TRAPPED_CHEST.get(), AbnormalsChestTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ExampleTileEntityRegistry.SIGN.get(), SignTileEntityRenderer::new);
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
	
	private void replaceBeehivePOI() {
		ImmutableList<Block> BEEHIVES = ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block instanceof AbnormalsBeehiveBlock).collect(ImmutableList.toImmutableList());
		PointOfInterestType.BEEHIVE.blockStates = this.makePOIStatesMutable(PointOfInterestType.BEEHIVE.blockStates);
		BEEHIVES.stream().forEach((block) -> {
			block.getStateContainer().getValidStates().forEach(state -> { 
				PointOfInterestType.POIT_BY_BLOCKSTATE.put(state, PointOfInterestType.BEEHIVE);
				PointOfInterestType.BEEHIVE.blockStates.add(state);
			});
		});
	}
	
	private void setupMessages() {
		int id = -1;
		
		CHANNEL.messageBuilder(MessageS2CEndimation.class, id++)
		.encoder(MessageS2CEndimation::serialize).decoder(MessageS2CEndimation::deserialize)
		.consumer(MessageS2CEndimation::handle)
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
	
	/*
	 * Currently unused
	 */
	@SuppressWarnings("unused")
	private <K, V> Map<K, V> copyMap(Map<K, V> toCopy) {
		Map<K, V> copy = Maps.newHashMap();
		toCopy.forEach((k, v) -> {
			copy.put(k, v);
		});
		return copy;
	}
	
	private Set<BlockState> makePOIStatesMutable(Set<BlockState> toCopy) {
		Set<BlockState> copy = Sets.newHashSet();
		toCopy.forEach((state) -> {
			copy.add(state);
		});
		return copy;
	}
}
