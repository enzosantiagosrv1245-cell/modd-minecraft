package com.yourname.lawyermod;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FunctionPaperItem extends Item {
    
    public FunctionPaperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // Abrir GUI de seleção de função
            openFunctionSelectionGUI(serverPlayer);
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    private void openFunctionSelectionGUI(ServerPlayer player) {
        // Verificar se já tem função
        if (PlayerDataManager.getPlayerFunction(player) != null) {
            player.sendSystemMessage(Component.literal("§cVocê já possui uma função! Apenas administradores podem remover."));
            return;
        }

        // Simular tela preta transparente com opções
        player.sendSystemMessage(Component.literal("§0§l===== SELEÇÃO DE FUNÇÃO ====="));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§e1. Advogado - Sistema de julgamento"));
        player.sendSystemMessage(Component.literal("§b2. Anjo - Imortalidade e poderes celestiais"));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§7Digite: /selectfunction lawyer ou /selectfunction angel"));
        player.sendSystemMessage(Component.literal("§c⚠ AVISO: Função permanente até remoção por admin!"));
        player.sendSystemMessage(Component.literal("§0§l==========================="));
        
        // Marcar que o jogador está no processo de seleção
        PlayerDataManager.setSelectingFunction(player, true);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("§6Papel de Função");
    }
}