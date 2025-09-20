package com.yourname.lawyermod;

import java.util.HashMap;
import java.util.Map;

public class MissionSystem {
    private static final Map<String, String> playerMissions = new HashMap<>();
    private static final Map<String, Integer> playerProgress = new HashMap<>();
    
    public static void assignMission(String playerName, String function) {
        String mission = "";
        switch (function) {
            case "lawyer":
                mission = "Coletar 10 pedras para fortalecer a justiça";
                break;
            case "angel":
                mission = "Coletar 15 madeiras para proteger a natureza";
                break;
        }
        
        playerMissions.put(playerName, mission);
        playerProgress.put(playerName, 0);
        System.out.println("Missão atribuída para " + playerName + ": " + mission);
    }
    
    public static void updateProgress(String playerName, int items) {
        int current = playerProgress.getOrDefault(playerName, 0);
        playerProgress.put(playerName, current + items);
        System.out.println("Progresso " + playerName + ": +" + items + " itens coletados!");
    }
    
    public static void showMissions() {
        System.out.println("=== MISSÕES ATIVAS ===");
        for (Map.Entry<String, String> entry : playerMissions.entrySet()) {
            String player = entry.getKey();
            String mission = entry.getValue();
            int progress = playerProgress.getOrDefault(player, 0);
            System.out.println("• " + player + ": " + mission + " (Progresso: " + progress + ")");
        }
    }
}
