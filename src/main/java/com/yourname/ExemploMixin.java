package com.seunome.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class ExemploMixin {

    @Inject(at = @At("HEAD"), method = "startGame")
    private void inicioDoJogo(CallbackInfo info) {
        System.out.println("Meu Mixin está funcionando!");
    }
}
