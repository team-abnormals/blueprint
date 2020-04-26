package com.teamabnormals.abnormals_core.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamabnormals.abnormals_core.core.utils.RegistryHelper;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("abnormals_core")
@Mod.EventBusSubscriber(modid = "abnormals_core", bus = Mod.EventBusSubscriber.Bus.MOD)
public class AbnormalsCore {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "abnormals_core";
	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MODID);

	public AbnormalsCore() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftForge.EVENT_BUS.register(this);
        
		//REGISTRY_HELPER.getDeferredItemRegister().register(modEventBus);
		//REGISTRY_HELPER.getDeferredBlockRegister().register(modEventBus);
		
		modEventBus.addListener(this::commonSetup);
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> { 
			modEventBus.addListener(this::clientSetup);
			//modEventBus.addListener(EventPriority.LOWEST, this::registerItemColors);
		});
	}
    
	private void commonSetup(final FMLCommonSetupEvent event) {}
    
	private void clientSetup(final FMLClientSetupEvent event) {}
	
	@OnlyIn(Dist.CLIENT)
	private void registerItemColors(ColorHandlerEvent.Item event) {
		REGISTRY_HELPER.processSpawnEggColors(event);
	}
}