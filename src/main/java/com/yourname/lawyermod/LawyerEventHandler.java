package com.yourname.lawyermod;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LawyerMod.MOD_ID)
public class LawyerEventHandler {

    @SubscribeEvent
    public static void onPlayerMove(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (PlayerDataManager.isJudged(player) && !PlayerDataManager.canPlayerMove(player)) {
                // Cancelar movimento mantendo o jogador na posição
                double x = player.getX();
                double y = player.getY();
                double z = player.getZ();
                
                // Verificar se o jogador tentou se mover
                if (player.getDeltaMovement().lengthSqr() > 0.001) {
                    // Resetar posição
                    player.setDeltaMovement(0, player.getDeltaMovement().y, 0);
                    
                    // Mensagem ocasional lembrando que não pode se mover
                    if (player.tickCount % 100 == 0) { // A cada 5 segundos
                        player.sendSystemMessage(Component.literal("§cVocê não pode se mover durante o julgamento!"));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        // Limpar dados quando o jogador sai
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerDataManager.clearPlayerData(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // Mensagem de boas-vindas com informações do mod
        if (event.getEntity() instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.literal("§6=== LawyerMod Ativo ==="));
            player.sendSystemMessage(Component.literal("§eUse /givefunctionpaper <player> para dar papéis de função (Admin)"));
            player.sendSystemMessage(Component.literal("§eClique no papel de função para escolher sua profissão!"));
        }
    }
}