package com.teamabnormals.blueprint.core;

import com.teamabnormals.blueprint.client.BlueprintShaders;
import com.teamabnormals.blueprint.client.RewardHandler;
import com.teamabnormals.blueprint.client.renderer.BlueprintBoatRenderer;
import com.teamabnormals.blueprint.client.renderer.block.BlueprintChestBlockEntityRenderer;
import com.teamabnormals.blueprint.client.screen.splash.BlueprintSplashManager;
import com.teamabnormals.blueprint.common.capability.chunkloading.ChunkLoaderCapability;
import com.teamabnormals.blueprint.common.capability.chunkloading.ChunkLoaderEvents;
import com.teamabnormals.blueprint.common.network.MessageC2SUpdateSlabfishHat;
import com.teamabnormals.blueprint.common.network.entity.MessageS2CEndimation;
import com.teamabnormals.blueprint.common.network.entity.MessageS2CTeleportEntity;
import com.teamabnormals.blueprint.common.network.entity.MessageS2CUpdateEntityData;
import com.teamabnormals.blueprint.common.network.particle.MessageS2CSpawnParticle;
import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSource;
import com.teamabnormals.blueprint.common.world.storage.tracking.DataProcessors;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.blueprint.core.api.WoodTypeRegistryHelper;
import com.teamabnormals.blueprint.core.api.conditions.BlueprintAndCondition;
import com.teamabnormals.blueprint.core.api.conditions.QuarkFlagRecipeCondition.Serializer;
import com.teamabnormals.blueprint.core.api.conditions.config.*;
import com.teamabnormals.blueprint.core.api.model.FullbrightModel;
import com.teamabnormals.blueprint.core.data.server.BlueprintDatapackBuiltinEntriesProvider;
import com.teamabnormals.blueprint.core.data.server.tags.*;
import com.teamabnormals.blueprint.core.endimator.EndimationLoader;
import com.teamabnormals.blueprint.core.other.BlueprintEvents;
import com.teamabnormals.blueprint.core.other.tags.BlueprintItemTags;
import com.teamabnormals.blueprint.core.registry.*;
import com.teamabnormals.blueprint.core.util.DataUtil;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import com.teamabnormals.blueprint.core.util.item.CreativeModeTabContentsPopulator;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
@EventBusSubscriber(modid = Blueprint.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
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
		BlueprintLootConditions.LOOT_CONDITION_TYPES.register(bus);

		bus.addListener((ModConfigEvent event) -> {
			final ModConfig config = event.getConfig();
			if (config.getSpec() == BlueprintConfig.CLIENT_SPEC) {
				BlueprintConfig.CLIENT.load();
			}
		});

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
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
			bus.addListener(this::modelSetup);
			bus.addListener(this::registerLayerDefinitions);
			bus.addListener(this::rendererSetup);
			bus.addListener(CreativeModeTabContentsPopulator::onBuildCreativeModeTabContents);
			bus.addListener(BlueprintSplashManager::onRegisterClientReloadListeners);
			bus.addListener(RewardHandler::clientSetup);
			bus.addListener(RewardHandler::addLayers);
			bus.addListener(BlueprintShaders::registerShaders);
		});

		bus.addListener(BlueprintDataPackRegistries::registerRegistries);
		bus.addListener(this::registerOnEvent);
		bus.addListener(EventPriority.LOWEST, this::commonSetup);
		bus.addListener(EventPriority.LOWEST, this::postLoadingSetup);
		bus.addListener(this::dataSetup);
		bus.addListener(this::registerCapabilities);
		context.registerConfig(ModConfig.Type.CLIENT, BlueprintConfig.CLIENT_SPEC);
		context.registerConfig(ModConfig.Type.COMMON, BlueprintConfig.COMMON_SPEC);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "slabfish_head"), SLABFISH_SETTINGS);
		event.enqueueWork(WoodTypeRegistryHelper::registerWoodTypes);
	}

	private void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(WoodTypeRegistryHelper::setupAtlas);
	}

	private void dataSetup(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();

		boolean includeServer = event.includeServer();
		BlueprintBlockTagsProvider blockTags = new BlueprintBlockTagsProvider(MOD_ID, packOutput, lookupProvider, fileHelper);
		generator.addProvider(includeServer, blockTags);
		generator.addProvider(includeServer, new BlueprintItemTagsProvider(MOD_ID, packOutput, lookupProvider, blockTags.contentsGetter(), fileHelper));
		generator.addProvider(includeServer, new BlueprintEntityTypeTagsProvider(MOD_ID, packOutput, lookupProvider, fileHelper));
		generator.addProvider(includeServer, new BlueprintBiomeTagsProvider(MOD_ID, packOutput, lookupProvider, fileHelper));
		generator.addProvider(includeServer, new BlueprintPoiTypeTagsProvider(MOD_ID, packOutput, lookupProvider, fileHelper));
		generator.addProvider(includeServer, new BlueprintDatapackBuiltinEntriesProvider(packOutput, lookupProvider));
	}

	private void registerOnEvent(RegisterEvent event) {
		event.register(Registries.BIOME_SOURCE, (helper) -> {
			helper.register("modded", ModdedBiomeSource.CODEC);
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
			Chicken.FOOD_ITEMS = CompoundIngredient.of(Chicken.FOOD_ITEMS, Ingredient.of(BlueprintItemTags.CHICKEN_FOOD));
			Pig.FOOD_ITEMS = CompoundIngredient.of(Pig.FOOD_ITEMS, Ingredient.of(BlueprintItemTags.PIG_FOOD));
			Strider.FOOD_ITEMS = CompoundIngredient.of(Strider.FOOD_ITEMS, Ingredient.of(BlueprintItemTags.STRIDER_FOOD));
			Strider.TEMPT_ITEMS = CompoundIngredient.of(Strider.TEMPT_ITEMS, Ingredient.of(BlueprintItemTags.STRIDER_TEMPT_ITEMS));
			Ocelot.TEMPT_INGREDIENT = CompoundIngredient.of(Ocelot.TEMPT_INGREDIENT, Ingredient.of(BlueprintItemTags.OCELOT_FOOD));
			Cat.TEMPT_INGREDIENT = CompoundIngredient.of(Cat.TEMPT_INGREDIENT, Ingredient.of(BlueprintItemTags.CAT_FOOD));

			DataUtil.getSortedAlternativeDispenseBehaviors().forEach(DataUtil.AlternativeDispenseBehavior::register);
			BlueprintEvents.SORTED_CUSTOM_NOTE_BLOCK_INSTRUMENTS = DataUtil.getSortedCustomNoteBlockInstruments();
		});
	}

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		ChunkLoaderCapability.register(event);
	}

	private void modelSetup(RegisterGeometryLoaders event) {
		event.register("fullbright", FullbrightModel.Loader.INSTANCE);
	}

	private void registerMessages() {
		int id = -1;
		CHANNEL.registerMessage(++id, MessageS2CEndimation.class, MessageS2CEndimation::serialize, MessageS2CEndimation::deserialize, MessageS2CEndimation::handle);
		CHANNEL.registerMessage(++id, MessageS2CTeleportEntity.class, MessageS2CTeleportEntity::serialize, MessageS2CTeleportEntity::deserialize, MessageS2CTeleportEntity::handle);
		CHANNEL.registerMessage(++id, MessageS2CSpawnParticle.class, MessageS2CSpawnParticle::serialize, MessageS2CSpawnParticle::deserialize, MessageS2CSpawnParticle::handle);
		CHANNEL.registerMessage(++id, MessageS2CUpdateEntityData.class, MessageS2CUpdateEntityData::serialize, MessageS2CUpdateEntityData::deserialize, MessageS2CUpdateEntityData::handle);
		CHANNEL.registerMessage(++id, MessageC2SUpdateSlabfishHat.class, MessageC2SUpdateSlabfishHat::serialize, MessageC2SUpdateSlabfishHat::deserialize, MessageC2SUpdateSlabfishHat::handle);
	}
}