package com.yourname.lawyermod;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, LawyerMod.MOD_ID);

    public static final RegistryObject<Item> FUNCTION_PAPER = ITEMS.register("function_paper",
            () -> new FunctionPaperItem(new Item.Properties()
                    .tab(CreativeModeTab.TAB_MISC)
                    .stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}