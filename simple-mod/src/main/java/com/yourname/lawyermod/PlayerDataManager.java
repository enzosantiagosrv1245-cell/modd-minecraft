package com.yourname.lawyermod;

import java.util.HashMap;
import java.util.Map;

public class PlayerDataManager {
    private static final Map<String, String> playerFunctions = new HashMap<>();
    private static final Map<String, Boolean> immortalPlayers = new HashMap<>();
    
    public static void setPlayerFunction(String playerName, String function) {
        playerFunctions.put(playerName, function);
        System.out.println("Jogador " + playerName + " agora é: " + function);
        
        if (function.equals("angel")) {
            immortalPlayers.put(playerName, true);
            System.out.println("Jogador " + playerName + " agora é IMORTAL!");
        }
    }
    
    public static String getPlayerFunction(String playerName) {
        return playerFunctions.get(playerName);
    }
    
    public static boolean isImmortal(String playerName) {
        return immortalPlayers.getOrDefault(playerName, false);
    }
    
    public static void showAllPlayers() {
        System.out.println("=== JOGADORES COM FUNÇÕES ===");
        for (Map.Entry<String, String> entry : playerFunctions.entrySet()) {
            String player = entry.getKey();
            String function = entry.getValue();
            String status = isImmortal(player) ? " (IMORTAL)" : "";
            System.out.println("• " + player + " -> " + function + status);
        }
    }
}
