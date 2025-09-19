package com.yourname.lawyermod;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MissionSystem {
    private static final Map<UUID, Mission> playerMissions = new HashMap<>();
    private static final Map<UUID, Integer> playerProgress = new HashMap<>();
    private static final Map<UUID, Integer> playerLevels = new HashMap<>();

    public static class Mission {
        public final String name;
        public final String description;
        public final ItemStack requiredItem;
        public final int requiredAmount;

        public Mission(String name, String description, ItemStack requiredItem, int requiredAmount) {
            this.name = name;
            this.description = description;
            this.requiredItem = requiredItem;
            this.requiredAmount = requiredAmount;
        }
    }

    public static void assignMission(Player player) {
        String function = PlayerDataManager.getPlayerFunction(player);
        if (function == null) return;

        Mission mission = null;
        switch (function) {
            case "lawyer":
                mission = new Mission("Coletor de Justiça", "Colete 10 pedras para fortalecer a justiça", 
                                    new ItemStack(Items.STONE), 10);
                break;
            case "angel":
                mission = new Mission("Guardião da Natureza", "Colete 15 madeiras para proteger a natureza", 
                                    new ItemStack(Items.OAK_LOG), 15);
                break;
        }

        if (mission != null) {
            playerMissions.put(player.getUUID(), mission);
            playerProgress.put(player.getUUID(), 0);
            playerLevels.put(player.getUUID(), 1);
            
            if (player instanceof ServerPlayer serverPlayer) {
                showMissionHUD(serverPlayer);
            }
        }
    }

    public static void showMissionHUD(ServerPlayer player) {
        Mission mission = playerMissions.get(player.getUUID());
        int progress = playerProgress.getOrDefault(player.getUUID(), 0);
        int level = playerLevels.getOrDefault(player.getUUID(), 1);
        
        if (mission != null) {
            // Simular HUD no canto superior direito via actionbar e title
            String progressBar = createProgressBar(progress, mission.requiredAmount);
            
            // Usar title para mostrar no canto superior
            player.sendSystemMessage(Component.literal("§6=== MISSÃO ATIVA ==="));
            player.sendSystemMessage(Component.literal("§e" + mission.name + " §7(Nível " + level + ")"));
            player.sendSystemMessage(Component.literal("§f" + mission.description));
            player.sendSystemMessage(Component.literal("§a" + progressBar + " §f" + progress + "/" + mission.requiredAmount));
        }
    }

    private static String createProgressBar(int current, int max) {
        int filled = (int) ((double) current / max * 20); // Barra de 20 caracteres
        StringBuilder bar = new StringBuilder("§a");
        
        for (int i = 0; i < 20; i++) {
            if (i < filled) {
                bar.append("█");
            } else if (i == filled && current < max) {
                bar.append("§7▒");
            } else {
                bar.append("§7░");
            }
        }
        
        return bar.toString();
    }

    public static void updateProgress(Player player, ItemStack item) {
        Mission mission = playerMissions.get(player.getUUID());
        if (mission == null) return;

        if (ItemStack.isSameItem(item, mission.requiredItem)) {
            int currentProgress = playerProgress.getOrDefault(player.getUUID(), 0);
            int newProgress = Math.min(currentProgress + item.getCount(), mission.requiredAmount);
            playerProgress.put(player.getUUID(), newProgress);

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.literal("§a+" + item.getCount() + " " + mission.requiredItem.getHoverName().getString() + " coletado!"));
                
                // Verificar se completou a missão
                if (newProgress >= mission.requiredAmount) {
                    completeMission(serverPlayer);
                }
            }
        }
    }

    private static void completeMission(ServerPlayer player) {
        int currentLevel = playerLevels.getOrDefault(player.getUUID(), 1);
        
        if (currentLevel < 2) {
            // Level up para nível 2
            playerLevels.put(player.getUUID(), 2);
            showLevelUpMessage(player);
            
            // Resetar progresso para nova missão do nível 2
            Mission mission = playerMissions.get(player.getUUID());
            if (mission != null) {
                // Dobrar a quantidade necessária para nível 2
                Mission newMission = new Mission(mission.name + " Avançada", 
                                               mission.description + " (Missão Avançada)", 
                                               mission.requiredItem, mission.requiredAmount * 2);
                playerMissions.put(player.getUUID(), newMission);
                playerProgress.put(player.getUUID(), 0);
            }
        } else {
            // Missão nível 2 completa
            player.sendSystemMessage(Component.literal("§6§l=== MISSÃO COMPLETA! ==="));
            player.sendSystemMessage(Component.literal("§aVocê completou todas as missões disponíveis!"));
            player.sendSystemMessage(Component.literal("§eVocê é agora um mestre em sua função!"));
            
            // Remover missão
            playerMissions.remove(player.getUUID());
            playerProgress.remove(player.getUUID());
        }
    }

    private static void showLevelUpMessage(ServerPlayer player) {
        // Mensagem de level up que aparece e some
        player.displayClientMessage(Component.literal("§6§l✦ LEVEL UP! ✦"), false);
        player.displayClientMessage(Component.literal("§eVocê alcançou o Nível 2!"), false);
        
        // Mensagem no chat também
        player.sendSystemMessage(Component.literal("§6§l=== LEVEL UP! ==="));
        player.sendSystemMessage(Component.literal("§eParabéns! Você subiu para o nível 2!"));
        player.sendSystemMessage(Component.literal("§aNova missão desbloqueada com maior dificuldade!"));
        
        // Efeitos visuais (sons, partículas, etc podem ser adicionados aqui)
    }

    public static Mission getPlayerMission(Player player) {
        return playerMissions.get(player.getUUID());
    }

    public static int getPlayerProgress(Player player) {
        return playerProgress.getOrDefault(player.getUUID(), 0);
    }

    public static int getPlayerLevel(Player player) {
        return playerLevels.getOrDefault(player.getUUID(), 1);
    }

    public static void clearPlayerMission(Player player) {
        playerMissions.remove(player.getUUID());
        playerProgress.remove(player.getUUID());
        playerLevels.remove(player.getUUID());
    }

    public static void showMissionStatus(ServerPlayer player) {
        Mission mission = getPlayerMission(player);
        if (mission != null) {
            showMissionHUD(player);
        } else {
            player.sendSystemMessage(Component.literal("§cVocê não possui nenhuma missão ativa."));
        }
    }
}