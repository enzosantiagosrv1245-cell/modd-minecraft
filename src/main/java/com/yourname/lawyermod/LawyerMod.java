package com.yourname.lawyermod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LawyerMod.MOD_ID)
public class LawyerMod {
    public static final String MOD_ID = "lawyermod";
    public static final Logger LOGGER = LogManager.getLogger();

    public LawyerMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);

        ModItems.register(modEventBus);
        ModCommands.register();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new LawyerEventHandler());
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("LawyerMod setup complete!");
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        LOGGER.info("LawyerMod client setup complete!");
    }
}