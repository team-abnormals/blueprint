package com.teamabnormals.abnormals_core.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("abnormals_core")
@Mod.EventBusSubscriber(modid = "abnormals_core", bus = Mod.EventBusSubscriber.Bus.MOD)
public class AbnormalsCore
{
    //private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "abnormals_core";

    public AbnormalsCore() {
    	IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        
        modEventBus.addListener(this::commonSetup);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> { modEventBus.addListener(this::clientSetup); });
    }
    
    private void commonSetup(final FMLCommonSetupEvent event)
	{    	
	}
    
    private void clientSetup(final FMLClientSetupEvent event) 
    {
    }
}