package com.teamabnormals.abnormals_core.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamabnormals.abnormals_core.common.network.MessageS2CEndimation;
import com.teamabnormals.abnormals_core.core.library.api.IAddToBiomes;
import com.teamabnormals.abnormals_core.core.utils.RegistryHelper;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("abnormals_core")
@Mod.EventBusSubscriber(modid = "abnormals_core", bus = Mod.EventBusSubscriber.Bus.MOD)
public class AbnormalsCore {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "abnormals_core";
	public static final String NETWORK_PROTOCOL = "AC1";
	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MODID);
	
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "net"))
		.networkProtocolVersion(() -> NETWORK_PROTOCOL)
		.clientAcceptedVersions(NETWORK_PROTOCOL::equals)
		.serverAcceptedVersions(NETWORK_PROTOCOL::equals)
		.simpleChannel();

	public AbnormalsCore() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftForge.EVENT_BUS.register(this);
		
		this.setupMessages();
        
		//REGISTRY_HELPER.getDeferredItemRegister().register(modEventBus);
		//REGISTRY_HELPER.getDeferredBlockRegister().register(modEventBus);
		//REGISTRY_HELPER.getDeferredEntityRegister().register(modEventBus);
		//REGISTRY_HELPER.getDeferredSoundRegister().register(modEventBus);
		
		modEventBus.addListener(this::commonSetup);
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> { 
			modEventBus.addListener(this::clientSetup);
			modEventBus.addListener(EventPriority.LOWEST, this::commonSetup);
			//modEventBus.addListener(EventPriority.LOWEST, this::registerItemColors);
		});
	}
    
	private void commonSetup(final FMLCommonSetupEvent event) {
		ForgeRegistries.FEATURES.getValues().stream().filter(feature -> feature instanceof IAddToBiomes).forEach((feature) -> {
			ForgeRegistries.BIOMES.forEach(((IAddToBiomes) feature).processBiomeAddition());
		});
		//ExampleEntitySpawnHandler.processSpawnAdditions();
	}
    
	@OnlyIn(Dist.CLIENT)
	private void clientSetup(final FMLClientSetupEvent event) {
		//RenderingRegistry.registerEntityRenderingHandler(ExampleEntityRegistry.COW.get(), CowRenderer::new);
	}
	
	@OnlyIn(Dist.CLIENT)
	private void registerItemColors(ColorHandlerEvent.Item event) {
		REGISTRY_HELPER.processSpawnEggColors(event);
	}
	
	private void setupMessages() {
		int id = -1;
		
		CHANNEL.messageBuilder(MessageS2CEndimation.class, id++)
		.encoder(MessageS2CEndimation::serialize).decoder(MessageS2CEndimation::deserialize)
		.consumer(MessageS2CEndimation::handle)
		.add();
	}
}