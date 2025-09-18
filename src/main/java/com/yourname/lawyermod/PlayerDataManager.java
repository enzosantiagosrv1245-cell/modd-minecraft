package com.yourname.lawyermod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private static final Map<UUID, String> playerFunctions = new HashMap<>();
    private static final Map<UUID, Boolean> selectingFunction = new HashMap<>();
    private static final Map<UUID, UUID> judgedPlayers = new HashMap<>(); // jogador julgado -> advogado
    private static final Map<UUID, Boolean> canMove = new HashMap<>();

    // Funções do jogador
    public static void setPlayerFunction(Player player, String function) {
        playerFunctions.put(player.getUUID(), function);
    }

    public static String getPlayerFunction(Player player) {
        return playerFunctions.get(player.getUUID());
    }

    public static boolean hasFunction(Player player, String function) {
        String playerFunc = playerFunctions.get(player.getUUID());
        return playerFunc != null && playerFunc.equals(function);
    }

    // Seleção de função
    public static void setSelectingFunction(Player player, boolean selecting) {
        selectingFunction.put(player.getUUID(), selecting);
    }

    public static boolean isSelectingFunction(Player player) {
        return selectingFunction.getOrDefault(player.getUUID(), false);
    }

    // Sistema de julgamento
    public static void setJudgedBy(Player judged, Player lawyer) {
        judgedPlayers.put(judged.getUUID(), lawyer.getUUID());
        canMove.put(judged.getUUID(), false);
    }

    public static void freePlayer(Player player) {
        judgedPlayers.remove(player.getUUID());
        canMove.remove(player.getUUID());
    }

    public static boolean isJudged(Player player) {
        return judgedPlayers.containsKey(player.getUUID());
    }

    public static Player getLawyer(Player judged) {
        UUID lawyerUUID = judgedPlayers.get(judged.getUUID());
        if (lawyerUUID != null && judged instanceof ServerPlayer) {
            return ((ServerPlayer) judged).getServer().getPlayerList().getPlayer(lawyerUUID);
        }
        return null;
    }

    public static boolean canPlayerMove(Player player) {
        return canMove.getOrDefault(player.getUUID(), true);
    }

    public static void clearPlayerData(Player player) {
        UUID uuid = player.getUUID();
        playerFunctions.remove(uuid);
        selectingFunction.remove(uuid);
        judgedPlayers.remove(uuid);
        canMove.remove(uuid);
    }
}