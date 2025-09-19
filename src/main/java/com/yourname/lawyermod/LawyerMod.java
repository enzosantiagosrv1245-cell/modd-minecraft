package com.yourname.lawyermod;

import com.yourname.lawyermod.items.ModItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LawyerMod.MOD_ID)
public class LawyerMod {

    public static final String MOD_ID = "lawyermod";

    public LawyerMod() {
        // Registra os itens
        ModItems.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}

